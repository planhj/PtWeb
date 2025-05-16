package com.example.ptweb.service;

import com.example.ptweb.mapper.item_categoriesMapper;
import com.example.ptweb.entity.item_categories;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class itemService {

    private final item_categoriesMapper itemCategoriesMapper;

    @Transactional(rollbackFor = Exception.class)
    public PurchaseResult exchangeItem(int userId, int itemId, int quantity) {
        if (quantity <= 0) {
            throw new BusinessException("兑换数量必须大于0");
        }

        item_categories item = getAvailableItem(itemId);

        // 可按需启用库存检查
        // checkItemStock(item, quantity);

        int totalCost = calculateTotalCost(item, quantity);
        deductUserPoints(userId, totalCost);
        reduceItemStock(itemId, quantity);
        sendNotification(userId, item, quantity, totalCost);

        return buildPurchaseResult(userId, item, quantity, totalCost);
    }

    private item_categories getAvailableItem(int itemId) {
        item_categories item = itemCategoriesMapper.selectById(itemId);
        if (item == null) throw new ItemNotFoundException("商品不存在");
        if (!Boolean.TRUE.equals(item.getIsActive())) throw new ItemNotAvailableException("商品已下架");
        return item;
    }

    /*
    private void checkItemStock(item_categories item, int quantity) {
        if (item.getStock() != null && item.getStock() < quantity) {
            throw new InsufficientStockException("商品库存不足，剩余" + item.getStock() + "件");
        }
    }
    */

    private int calculateTotalCost(item_categories item, int quantity) {
        Integer price = item.getPrice();
        if (price == null) {
            throw new BusinessException("商品价格未设置");
        }
        return price * quantity;
    }

    private void deductUserPoints(int userId, int amount) {
        BigDecimal currentPoints = itemCategoriesMapper.getUserBonusPoints(userId);
        if (currentPoints.compareTo(BigDecimal.valueOf(amount)) < 0) {
            throw new InsufficientPointsException("积分不足，需要" + amount + "，当前余额" + currentPoints);
        }
        int affectedRows = itemCategoriesMapper.deductUserPoints(userId, BigDecimal.valueOf(amount));
        if (affectedRows == 0) {
            throw new DeductPointsFailedException("扣除积分失败");
        }
    }

    private void reduceItemStock(int itemId, int quantity) {
        Integer stock = itemCategoriesMapper.checkItemStock(itemId);
        if (stock != null) {
            int affectedRows = itemCategoriesMapper.reduceItemStock(itemId, quantity);
            if (affectedRows == 0) {
                throw new ReduceStockFailedException("减少库存失败");
            }
        }
    }

    private void sendNotification(int userId, item_categories item, int quantity, int totalCost) {
        System.out.printf("用户 %d 兑换了商品 %s ×%d，花费积分 %d%n",
                userId, item.getName(), quantity, totalCost);
    }

    private PurchaseResult buildPurchaseResult(int userId, item_categories item, int quantity, int cost) {
        PurchaseResult result = new PurchaseResult();
        result.setUserId(userId);
        result.setItemName(item.getName());
        result.setQuantity(quantity);
        result.setCost(BigDecimal.valueOf(cost));
        result.setRemainingPoints(itemCategoriesMapper.getUserBonusPoints(userId));
        return result;
    }

    // 简化版兑换结果DTO
    public static class PurchaseResult {
        private int userId;
        private String itemName;
        private int quantity;
        private BigDecimal cost;
        private BigDecimal remainingPoints;

        // getter/setter
        public int getUserId() { return userId; }
        public void setUserId(int userId) { this.userId = userId; }
        public String getItemName() { return itemName; }
        public void setItemName(String itemName) { this.itemName = itemName; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public BigDecimal getCost() { return cost; }
        public void setCost(BigDecimal cost) { this.cost = cost; }
        public BigDecimal getRemainingPoints() { return remainingPoints; }
        public void setRemainingPoints(BigDecimal remainingPoints) { this.remainingPoints = remainingPoints; }
    }
    public List<item_categories> getAllActiveItems() {
        return itemCategoriesMapper.selectAllActive(); // 自定义 mapper 方法
    }

    // 获取商品（用于详情或验证）
    public item_categories getItemById(int itemId) {
        item_categories item = itemCategoriesMapper.selectById(itemId);
        if (item == null || !Boolean.TRUE.equals(item.getIsActive())) {
            throw new ItemNotAvailableException("商品不存在或未上架");
        }
        return item;
    }

    // 异常类
    public static class BusinessException extends RuntimeException {
        public BusinessException(String message) { super(message); }
    }

    public static class ItemNotFoundException extends BusinessException {
        public ItemNotFoundException(String message) { super(message); }
    }

    public static class ItemNotAvailableException extends BusinessException {
        public ItemNotAvailableException(String message) { super(message); }
    }

    public static class InsufficientStockException extends BusinessException {
        public InsufficientStockException(String message) { super(message); }
    }

    public static class InsufficientPointsException extends BusinessException {
        public InsufficientPointsException(String message) { super(message); }
    }

    public static class DeductPointsFailedException extends BusinessException {
        public DeductPointsFailedException(String message) { super(message); }
    }

    public static class ReduceStockFailedException extends BusinessException {
        public ReduceStockFailedException(String message) { super(message); }
    }
}
