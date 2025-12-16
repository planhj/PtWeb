package com.example.ptweb.controller.item;

import cn.dev33.satoken.stp.StpUtil;
import com.example.ptweb.entity.item_categories;
import com.example.ptweb.service.itemService;
import com.example.ptweb.service.itemService.PurchaseResult;
import com.example.ptweb.service.itemService.BusinessException;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/item")
@RequiredArgsConstructor
public class ItemController {

    private final itemService itemService;

    /**
     * 获取所有上架商品
     */
    @GetMapping("/list")
    public List<item_categories> listItems() {
        return itemService.getAllActiveItems();
    }

    /**
     * 用户兑换商品
     */
    @PostMapping("/exchange/{itemId}")
    public ResponseEntity<?> exchangeItem(@PathVariable long itemId) {
        Long userId = StpUtil.getLoginIdAsLong();
        try {
            PurchaseResult result = itemService.exchangeItem(userId, itemId, 1); // 数量写死为1
            return ResponseEntity.ok(result);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    //测试类
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    /**
     * 统一处理商城业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<String> handleBusinessException(BusinessException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("操作失败：" + ex.getMessage());
    }
}
