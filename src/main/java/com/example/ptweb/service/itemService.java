package com.example.ptweb.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.ptweb.entity.InviteCode;
import com.example.ptweb.entity.User;
import com.example.ptweb.mapper.UserMapper;
import com.example.ptweb.mapper.item_categoriesMapper;
import com.example.ptweb.entity.item_categories;
import com.example.ptweb.type.CustomTitle;
import com.example.ptweb.mapper.InviteCodeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class itemService {

    private final item_categoriesMapper itemCategoriesMapper;
    private final UserMapper userMapper;
    private final InviteCodeMapper inviteCodeMapper;

    @Transactional(rollbackFor = Exception.class)
    public PurchaseResult exchangeItem(long userId, long itemId, long quantity) {
        if (quantity <= 0) {
            throw new BusinessException("兑换数量必须大于0");
        }

        item_categories item = getAvailableItem(itemId);
        if (item.getId() == 2) {
            long currentDownloaded = userMapper.getDownloadedByUserId(userId);
            long minRequired = 1024L * 1024 * 5000;
            if (currentDownloaded < minRequired) {
                throw new BusinessException("当前下载量不足5GB，无法兑换该下载量商品");
            }
        }
        User user = userMapper.selectById(userId);
        if (item.getId() == 3L) {
            if (CustomTitle.SVIP.equals(user.getCustomTitle())) {
                throw new BusinessException("无法降级为vip");
            }
        }
        if (item.getId() == 4L) {
            if (CustomTitle.SVIP.equals(user.getCustomTitle())) {
                throw new BusinessException("无法重复购买");
            }
        }
        long totalCost = calculateTotalCost(userId,item, quantity);

        deductUserPoints(userId, totalCost);
        applySpecialItemEffect(userId, item, quantity);
        updateUserTitleByCategory(userId, item.getCategoryId());
        generateInviteCode(userId,item,quantity);
        sendNotification(userId, item, quantity, totalCost);

        return buildPurchaseResult(userId, item, quantity, totalCost);
    }


    // ✅ 增加上传/下载量
    private void applySpecialItemEffect(long userId, item_categories item, long quantity) {
        long amountPerUnit = 1024L * 1024 * 5000;

        if (item.getId() == 1) {
            userMapper.increaseUserUpload(userId, amountPerUnit * quantity);
        } else if (item.getId() == 2) {
            userMapper.increaseUserDownload(userId, amountPerUnit * quantity);
        }
    }


    private void updateUserTitleByCategory(long userId, Long categoryId) {
        if (categoryId == null) return;

        User user = userMapper.selectById(userId);
        if (user == null) return;

        // 获取当前称号
        CustomTitle currentTitle = user.getCustomTitle();

        if (categoryId == 3L) {
            // 如果当前是 SVIP，就不能降级为 VIP
            if (CustomTitle.SVIP.equals(currentTitle)) {
                return; // ❌ 不允许降级，直接返回
            }
            user.setCustomTitle(CustomTitle.VIP);
        } else if (categoryId == 4L) {
            user.setCustomTitle(CustomTitle.SVIP);
        } else {
            return; // 其他分类不处理
        }

        userMapper.updateById(user); // ✅ 更新数据库中的用户称号
    }


    private item_categories getAvailableItem(long itemId) {
        item_categories item = itemCategoriesMapper.selectById(itemId);
        if (item == null) throw new ItemNotFoundException("商品不存在");
        if (!Boolean.TRUE.equals(item.getIsActive())) throw new ItemNotAvailableException("商品已下架");
        return item;
    }

    private long calculateTotalCost(long userId, item_categories item, long quantity) {
        User user = userMapper.selectById(userId);
        CustomTitle currentTitle = user.getCustomTitle();
        if (CustomTitle.SVIP.equals(currentTitle)&&item.getId()==3) {
            return 0; // ❌ 不允许降级，直接返回
        }
        Integer price = item.getPrice();
        if (price == null) {
            throw new BusinessException("商品价格未设置");
        }
        return price * quantity;
    }

    private void deductUserPoints(long userId, long amount) {
        BigDecimal currentPoints = itemCategoriesMapper.getUserBonusPoints(userId);
        if (currentPoints.compareTo(BigDecimal.valueOf(amount)) < 0) {
            throw new InsufficientPointsException("积分不足，需要" + amount + "，当前余额" + currentPoints);
        }
        int affectedRows = itemCategoriesMapper.deductUserPoints(userId, BigDecimal.valueOf(amount));
        if (affectedRows == 0) {
            throw new DeductPointsFailedException("扣除积分失败");
        }
    }

    private void sendNotification(long userId, item_categories item, long quantity, long totalCost) {
        System.out.printf("用户 %d 兑换了商品 %s ×%d，花费积分 %d%n",
                userId, item.getName(), quantity, totalCost);
    }

    private PurchaseResult buildPurchaseResult(long userId, item_categories item, long quantity, long cost) {
        PurchaseResult result = new PurchaseResult();
        result.setUserId(userId);
        result.setItemName(item.getName());
        result.setQuantity(quantity);
        result.setCost(BigDecimal.valueOf(cost));
        result.setRemainingPoints(itemCategoriesMapper.getUserBonusPoints(userId));
        return result;
    }

    public List<item_categories> getAllActiveItems() {
        return itemCategoriesMapper.selectAllActive();
    }

    public item_categories getItemById(int itemId) {
        item_categories item = itemCategoriesMapper.selectById(itemId);
        if (item == null || !Boolean.TRUE.equals(item.getIsActive())) {
            throw new ItemNotAvailableException("商品不存在或未上架");
        }
        return item;
    }

    // DTO
    public static class PurchaseResult {
        private long userId;
        private String itemName;
        private long quantity;
        private BigDecimal cost;
        private BigDecimal remainingPoints;

        public long getUserId() { return userId; }
        public void setUserId(long userId) { this.userId = userId; }
        public String getItemName() { return itemName; }
        public void setItemName(String itemName) { this.itemName = itemName; }
        public long getQuantity() { return quantity; }
        public void setQuantity(long quantity) { this.quantity = quantity; }
        public BigDecimal getCost() { return cost; }
        public void setCost(BigDecimal cost) { this.cost = cost; }
        public BigDecimal getRemainingPoints() { return remainingPoints; }
        public void setRemainingPoints(BigDecimal remainingPoints) { this.remainingPoints = remainingPoints; }
    }

    public void generateInviteCode(long userId, item_categories item, long quantity) {
        // 生成唯一邀请码
        if (item.getId() == 5) {
            String code;
            do {
                code = UUID.randomUUID().toString().replace("-", "").substring(0, 10); // 截取10位
            } while (inviteCodeMapper.selectOne(new QueryWrapper<InviteCode>().eq("code", code)) != null);

            // 保存邀请码
            InviteCode inviteCode = new InviteCode();
            inviteCode.setCode(code);
            inviteCode.setCreatorId(userId);
            inviteCode.setUsed(false);
            inviteCode.setCreateTime(LocalDateTime.now());
            inviteCodeMapper.insert(inviteCode);
        }
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

    public static class InsufficientPointsException extends BusinessException {
        public InsufficientPointsException(String message) { super(message); }
    }

    public static class DeductPointsFailedException extends BusinessException {
        public DeductPointsFailedException(String message) { super(message); }
    }
}
