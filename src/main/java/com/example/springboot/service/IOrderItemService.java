package com.example.springboot.service;

import com.example.springboot.entity.OrderItem;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 * @since 2023-04-24
 */
@Component
public interface IOrderItemService extends IService<OrderItem> {

    /**
     * 根据订单ID 删除订单项目
     * @param orderId
     * @return
     */
    int delByOrderId(long orderId);
}
