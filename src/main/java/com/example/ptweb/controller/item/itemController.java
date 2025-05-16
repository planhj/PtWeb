package com.example.ptweb.controller.announce;

import cn.dev33.satoken.stp.StpUtil;
import com.example.ptweb.config.TrackerConfig;
import com.example.ptweb.entity.Peer;
import com.example.ptweb.entity.Torrent;
import com.example.ptweb.entity.User;
import com.example.ptweb.exception.APIGenericException;
import com.example.ptweb.exception.FixedAnnounceException;
import com.example.ptweb.exception.InvalidAnnounceException;
import com.example.ptweb.exception.RetryableAnnounceException;
import com.example.ptweb.service.*;
import com.example.ptweb.type.AnnounceEventType;
import com.example.ptweb.util.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.ptweb.entity.item_categories.java;
import com.example.ptweb.mapper.item_categoriesMapper.java;
import com.example.ptweb.service.itemService.java;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.regex.Pattern;






@RestController
@RequestMapping("/api/v1/store")
@RequiredArgsConstructor
@Validated
public class BonusStoreController {

    private final BonusStoreService bonusStoreService;
    private final UserAuthService authService;

    //------------------------ 用户端接口 ------------------------

    /**
     * 分页获取商品列表
     */
    @GetMapping("/items")
    public ApiResponse<PageResult<StoreItemVO>> listItems(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageRequest pageRequest = PageRequest.of(page-1, size, Sort.by("displayOrder").ascending());
        PageResult<StoreItemVO> result = bonusStoreService.listItems(categoryId, pageRequest);
        return ApiResponse.success(result);
    }

    /**
     * 获取商品详情
     */
    @GetMapping("/items/{itemId}")
    public ApiResponse<StoreItemDetailVO> getItemDetail(@PathVariable int itemId) {
        StoreItemDetailVO detail = bonusStoreService.getItemDetail(itemId);
        return ApiResponse.success(detail);
    }

    /**
     * 兑换商品
     */
    @PostMapping("/exchange")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<ExchangeResultVO> exchangeItem(
            @RequestBody @Valid ExchangeRequest request,
            @RequestHeader("Authorization") String token) {

        int userId = authService.getCurrentUserId(token);
        ExchangeResultVO result = bonusStoreService.exchangeItem(userId, request.getItemId(), request.getQuantity());
        return ApiResponse.success(result);
    }

    /**
     * 获取用户兑换记录
     */
    @GetMapping("/purchases")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<List<PurchaseHistoryVO>> getPurchaseHistory(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestHeader("Authorization") String token) {

        int userId = authService.getCurrentUserId(token);
        List<PurchaseHistoryVO> history = bonusStoreService.getPurchaseHistory(userId, startDate, endDate);
        return ApiResponse.success(history);
    }

    /**
     * 获取用户积分信息
     */
    @GetMapping("/points")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<UserPointsVO> getUserPoints(@RequestHeader("Authorization") String token) {
        int userId = authService.getCurrentUserId(token);
        UserPointsVO pointsInfo = bonusStoreService.getUserPoints(userId);
        return ApiResponse.success(pointsInfo);
    }

 //------------------------ 管理员接口 ------------------------

    /**
     * 创建商品
     */
/**    @PostMapping("/admin/items")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<StoreItemVO> createItem(@RequestBody @Valid StoreItemCreateDTO dto) {
        StoreItemVO item = bonusStoreService.createItem(dto);
        return ApiResponse.success(item);
    }


     //更新商品状态

    @PutMapping("/admin/items/{itemId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> updateItemStatus(
            @PathVariable int itemId,
            @RequestBody @Valid ItemStatusUpdateDTO dto) {

        bonusStoreService.updateItemStatus(itemId, dto.getIsActive());
        return ApiResponse.success();
    }


     //分页查询订单

    @GetMapping("/admin/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PageResult<OrderVO>> listOrders(
            @RequestParam(required = false) Integer userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageRequest pageRequest = PageRequest.of(page-1, size, Sort.by("purchaseDate").descending());
        PageResult<OrderVO> result = bonusStoreService.listOrders(userId, pageRequest);
        return ApiResponse.success(result);
    }

    //调整用户积分

    @PostMapping("/admin/users/{userId}/points")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PointsAdjustVO> adjustUserPoints(
            @PathVariable int userId,
            @RequestBody @Valid PointsAdjustDTO dto) {

        PointsAdjustVO result = bonusStoreService.adjustUserPoints(userId, dto);
        return ApiResponse.success(result);
    }
    */
    //------------------------ 异常处理 ------------------------

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException ex) {
        ApiResponse<?> response = ApiResponse.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDenied() {
        ApiResponse<?> response = ApiResponse.error("权限不足");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
}

//------------------------ 相关DTO定义 ------------------------

@Data
static class ExchangeRequest {
    @NotNull(message = "商品ID不能为空")
    @Min(value = 1, message = "无效的商品ID")
    private Integer itemId;

    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于0")
    private Integer quantity;
}

@Data
static class StoreItemCreateDTO {
    @NotBlank(message = "商品名称不能为空")
    private String name;

    @NotNull(message = "分类ID不能为空")
    private Integer categoryId;

    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal price;

    private Integer stock;
    private String description;
    private Boolean isFeatured;
}

@Data
static class ItemStatusUpdateDTO {
    @NotNull(message = "状态不能为空")
    private Boolean isActive;
}

@Data
static class PointsAdjustDTO {
    @NotBlank(message = "操作类型不能为空")
    @Pattern(regexp = "ADD|DEDUCT", message = "无效的操作类型")
    private String operation;

    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    private BigDecimal amount;

    private String remark;
}