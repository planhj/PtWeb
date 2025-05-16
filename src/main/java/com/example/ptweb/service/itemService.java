package com.example.ptweb.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.ptweb.entity.Category;
import com.example.ptweb.mapper.CategoryMapper;
import com.example.ptweb.mapper.item_categoriesMapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.ptweb.entity.item_categories.java;
import com.example.ptweb.mapper.item_categoriesMapper.java;



import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BonusStoreService {

    private final BonusStoreMapper bonusStoreMapper;
    private final UserService userService;
    private final NotificationService notificationService;

    /**
     * 兑换商品
     * @param userId 用户ID
     * @param itemId 商品ID
     * @param quantity 兑换数量
     * @return 兑换结果
     */
    @Transactional(rollbackFor = Exception.class)
    public PurchaseResult exchangeItem(int userId, int itemId, int quantity) {
        // 1. 参数校验
        if (quantity <= 0) {
            throw new BusinessException("兑换数量必须大于0");
        }

        // 2. 获取商品信息
        StoreItem item = getAvailableItem(itemId);

        // 3. 检查库存
        checkItemStock(item, quantity);

        // 4. 计算总价（考虑促销活动）
        BigDecimal totalCost = calculateTotalCost(item, quantity);

        // 5. 检查并扣除用户积分
        deductUserPoints(userId, totalCost);

        // 6. 减少商品库存
        reduceItemStock(itemId, quantity);

        // 7. 创建兑换记录
        PurchaseRecord record = createPurchaseRecord(userId, itemId, quantity, totalCost);

        // 8. 记录积分交易
        recordTransaction(userId, totalCost, record.getPurchaseId(), item.getName());

        // 9. 发送通知
        sendNotification(userId, item, quantity, totalCost);

        return buildPurchaseResult(record, item);
    }

    /**
     * 获取可用商品
     */
    private StoreItem getAvailableItem(int itemId) {
        StoreItem item = bonusStoreMapper.findItemById(itemId);
        if (item == null) {
            throw new ItemNotFoundException("商品不存在");
        }
        if (!item.getIsActive()) {
            throw new ItemNotAvailableException("商品已下架");
        }
        return item;
    }

    /**
     * 检查商品库存
     */
    private void checkItemStock(StoreItem item, int quantity) {
        if (item.getStock() != null && item.getStock() < quantity) {
            throw new InsufficientStockException(
                    String.format("商品库存不足，剩余%d件", item.getStock())
            );
        }
    }

    /**
     * 计算总成本（包含促销折扣）
     */
    private BigDecimal calculateTotalCost(StoreItem item, int quantity) {
        BigDecimal originalPrice = item.getPrice().multiply(BigDecimal.valueOf(quantity));

        // 这里可以添加促销折扣计算逻辑
        // BigDecimal discount = calculateDiscount(item, quantity);
        // return originalPrice.subtract(discount);

        return originalPrice;
    }

    /**
     * 扣除用户积分
     */
    private void deductUserPoints(int userId, BigDecimal amount) {
        BigDecimal currentPoints = bonusStoreMapper.getUserBonusPoints(userId);
        if (currentPoints.compareTo(amount) < 0) {
            throw new InsufficientPointsException(
                    String.format("积分不足，需要%.2f，当前余额%.2f", amount, currentPoints)
            );
        }

        int affectedRows = bonusStoreMapper.deductUserPoints(userId, amount);
        if (affectedRows == 0) {
            throw new DeductPointsFailedException("扣除积分失败");
        }
    }

    /**
     * 减少商品库存
     */
    private void reduceItemStock(int itemId, int quantity) {
        if (quantity <= 0) return;

        Integer stock = bonusStoreMapper.checkItemStock(itemId);
        if (stock != null) {
            int affectedRows = bonusStoreMapper.reduceItemStock(itemId, quantity);
            if (affectedRows == 0) {
                throw new ReduceStockFailedException("减少库存失败");
            }
        }
    }

    /**
     * 创建兑换记录
     */
    private PurchaseRecord createPurchaseRecord(int userId, int itemId, int quantity, BigDecimal totalPrice) {
        PurchaseRecord record = new PurchaseRecord();
        record.setUserId(userId);
        record.setItemId(itemId);
        record.setQuantity(quantity);
        record.setTotalPrice(totalPrice);
        record.setStatus("completed");

        bonusStoreMapper.createPurchaseRecord(record);
        return record;
    }

    /**
     * 记录积分交易
     */
    private void recordTransaction(int userId, BigDecimal amount, int relatedId, String description) {
        BigDecimal balanceAfter = bonusStoreMapper.getUserBonusPoints(userId);

        BonusTransaction transaction = new BonusTransaction();
        transaction.setUserId(userId);
        transaction.setAmount(amount.negate()); // 负值表示扣除
        transaction.setBalanceAfter(balanceAfter);
        transaction.setType("purchase");
        transaction.setRelatedId(relatedId);
        transaction.setDescription("兑换商品: " + description);

        bonusStoreMapper.recordTransaction(transaction);
    }

    /**
     * 发送通知
     */
    private void sendNotification(int userId, StoreItem item, int quantity, BigDecimal totalCost) {
        String message = String.format(
                "您已成功兑换【%s】×%d，消耗%.2f积分",
                item.getName(), quantity, totalCost
        );
        notificationService.send(userId, "商品兑换成功", message);
    }

    /**
     * 构建返回结果
     */
    private PurchaseResult buildPurchaseResult(PurchaseRecord record, StoreItem item) {
        PurchaseResult result = new PurchaseResult();
        result.setOrderId(record.getPurchaseId());
        result.setItemName(item.getName());
        result.setQuantity(record.getQuantity());
        result.setCost(record.getTotalPrice());
        result.setRemainingPoints(bonusStoreMapper.getUserBonusPoints(record.getUserId()));
        result.setExchangeTime(record.getPurchaseDate());
        return result;
    }

    /**
     * 获取用户兑换历史
     */
    public List<PurchaseHistory> getUserPurchaseHistory(int userId) {
        return bonusStoreMapper.getUserPurchaseHistory(userId);
    }

    /**
     * 获取商品详情
     */
    public StoreItem getItemDetail(int itemId) {
        return getAvailableItem(itemId);
    }
}

// 兑换结果DTO
@Data
public class PurchaseResult {
    private Long orderId;
    private String itemName;
    private Integer quantity;
    private BigDecimal cost;
    private BigDecimal remainingPoints;
    private Date exchangeTime;
}

// 自定义异常类
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}

public class ItemNotFoundException extends BusinessException {
    public ItemNotFoundException(String message) {
        super(message);
    }
}

public class ItemNotAvailableException extends BusinessException {
    public ItemNotAvailableException(String message) {
        super(message);
    }
}

public class InsufficientStockException extends BusinessException {
    public InsufficientStockException(String message) {
        super(message);
    }
}

public class InsufficientPointsException extends BusinessException {
    public InsufficientPointsException(String message) {
        super(message);
    }
}

public class DeductPointsFailedException extends BusinessException {
    public DeductPointsFailedException(String message) {
        super(message);
    }
}

public class ReduceStockFailedException extends BusinessException {
    public ReduceStockFailedException(String message) {
        super(message);
    }
}