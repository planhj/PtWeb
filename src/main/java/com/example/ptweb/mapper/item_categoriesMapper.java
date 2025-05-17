package com.example.ptweb.mapper;



import org.apache.ibatis.annotations.*;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ptweb.entity.LoginHistory;
import com.example.ptweb.entity.item_categories;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public interface item_categoriesMapper extends BaseMapper<item_categories>{


    // 查询所有可用商品（按分类和显示顺序排序）
    @Select("SELECT * FROM store_items WHERE is_active = TRUE ORDER BY category_id, display_order ASC")
    List<item_categories> findAllActiveItems();

    // 查询指定分类的商品
    @Select("SELECT * FROM store_items WHERE category_id = #{categoryId} AND is_active = TRUE ORDER BY display_order ASC")
    List<item_categories> findItemsByCategory(@Param("categoryId") int categoryId);

    // 查询用户积分余额
    @Select("SELECT bonus_points FROM users WHERE user_id = #{userId}")
    BigDecimal getUserBonusPoints(@Param("userId") int userId);

    // 扣除用户积分
    @Update("UPDATE users SET bonus_points = bonus_points - #{deduction} WHERE user_id = #{userId} AND bonus_points >= #{deduction}")
    int deductUserPoints(@Param("userId") int userId, @Param("deduction") BigDecimal deduction);

    // 增加用户积分
    @Update("UPDATE users SET bonus_points = bonus_points + #{addition} WHERE user_id = #{userId}")
    int addUserPoints(@Param("userId") int userId, @Param("addition") BigDecimal addition);


/*
    // 记录积分交易
    @Insert("INSERT INTO bonus_transactions (user_id, amount, balance_after, type, related_id, description) " +
            "VALUES (#{userId}, #{amount}, #{balanceAfter}, #{type}, #{relatedId}, #{description})")
    int recordTransaction(BonusTransaction transaction);
*/
    // 减少商品库存
    @Update("UPDATE store_items SET stock = stock - #{quantity} " +
            "WHERE item_id = #{itemId} AND (stock IS NULL OR stock >= #{quantity})")
    int reduceItemStock(@Param("itemId") int itemId, @Param("quantity") int quantity);



    // 查询库存
    @Select("SELECT stock FROM store_items WHERE item_id = #{itemId}")
    Integer checkItemStock(@Param("itemId") int itemId);

    // 查询所有 is_active = true 的商品
    @Select("SELECT * FROM item_categories WHERE is_active = TRUE")
    List<item_categories> selectAllActive();


}