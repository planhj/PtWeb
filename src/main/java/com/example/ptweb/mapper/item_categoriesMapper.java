import org.apache.ibatis.annotations.*;
package com.example.ptweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ptweb.entity.LoginHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import com.example.ptweb.entity.item_categories.java;

import com.example.ptweb.service.itemService.java;

import java.sql.Timestamp;
import java.util.List;

public interface BonusStoreMapper {

    // 根据ID查询商品
    @Select("SELECT * FROM store_items WHERE item_id = #{itemId} AND is_active = TRUE")
    StoreItem findItemById(@Param("itemId") int itemId);

    // 查询所有可用商品（按分类和显示顺序排序）
    @Select("SELECT * FROM store_items WHERE is_active = TRUE ORDER BY category_id, display_order ASC")
    List<StoreItem> findAllActiveItems();

    // 查询指定分类的商品
    @Select("SELECT * FROM store_items WHERE category_id = #{categoryId} AND is_active = TRUE ORDER BY display_order ASC")
    List<StoreItem> findItemsByCategory(@Param("categoryId") int categoryId);

    // 查询用户积分余额
    @Select("SELECT bonus_points FROM users WHERE user_id = #{userId}")
    BigDecimal getUserBonusPoints(@Param("userId") int userId);

    // 扣除用户积分
    @Update("UPDATE users SET bonus_points = bonus_points - #{deduction} WHERE user_id = #{userId} AND bonus_points >= #{deduction}")
    int deductUserPoints(@Param("userId") int userId, @Param("deduction") BigDecimal deduction);

    // 增加用户积分
    @Update("UPDATE users SET bonus_points = bonus_points + #{addition} WHERE user_id = #{userId}")
    int addUserPoints(@Param("userId") int userId, @Param("addition") BigDecimal addition);

    // 创建兑换记录
    @Insert("INSERT INTO user_purchases (user_id, item_id, quantity, total_price, status) " +
            "VALUES (#{userId}, #{itemId}, #{quantity}, #{totalPrice}, 'completed')")
    @Options(useGeneratedKeys = true, keyProperty = "purchaseId")
    int createPurchaseRecord(PurchaseRecord record);

    // 记录积分交易
    @Insert("INSERT INTO bonus_transactions (user_id, amount, balance_after, type, related_id, description) " +
            "VALUES (#{userId}, #{amount}, #{balanceAfter}, #{type}, #{relatedId}, #{description})")
    int recordTransaction(BonusTransaction transaction);

    // 减少商品库存（如果有限库存）
    @Update("UPDATE store_items SET stock = stock - #{quantity} " +
            "WHERE item_id = #{itemId} AND (stock IS NULL OR stock >= #{quantity})")
    int reduceItemStock(@Param("itemId") int itemId, @Param("quantity") int quantity);

    // 查询用户兑换历史
    @Select("SELECT p.*, i.name as item_name FROM user_purchases p " +
            "JOIN store_items i ON p.item_id = i.item_id " +
            "WHERE p.user_id = #{userId} ORDER BY p.purchase_date DESC")
    List<PurchaseHistory> getUserPurchaseHistory(@Param("userId") int userId);

    // 检查商品库存
    @Select("SELECT stock FROM store_items WHERE item_id = #{itemId}")
    Integer checkItemStock(@Param("itemId") int itemId);
}