package com.example.ptweb.controller.item;

import com.example.ptweb.service.itemService;
import com.example.ptweb.service.itemService.BusinessException;
import com.example.ptweb.entity.item_categories;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/store")
@RequiredArgsConstructor
@Validated
public class BonusStoreController {

    private final itemService itemService;

    // 获取所有商品（不分页，示例）
    @GetMapping("/items")
    public List<item_categories> listItems() {
        return itemService.getAllActiveItems();
    }

    // 根据ID获取商品详情
    @GetMapping("/items/{itemId}")
    public ResponseEntity<item_categories> getItemDetail(@PathVariable int itemId) {
        try {
            item_categories item = itemService.getItemById(itemId);
            return ResponseEntity.ok(item);
        } catch (BusinessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // 用户兑换商品（固定参数示例）
    @PostMapping("/exchange")
    public ResponseEntity<String> exchangeItem(
            @RequestParam int userId,
            @RequestParam int itemId,
            @RequestParam int quantity) {
        try {
            itemService.exchangeItem(userId, itemId, quantity);
            return ResponseEntity.ok("兑换成功");
        } catch (BusinessException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    // 异常处理（可选）
    @ExceptionHandler(itemService.BusinessException.class)
    public ResponseEntity<String> handleBusinessException(itemService.BusinessException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("操作失败：" + ex.getMessage());
    }
}
