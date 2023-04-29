package com.example.springboot.service.impl;

import com.example.springboot.common.Constants;
import com.example.springboot.entity.OrderItem;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.OrderItemMapper;
import com.example.springboot.mapper.UserMapper;
import com.example.springboot.service.IOrderItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 
 * @since 2023-04-24
 */
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements IOrderItemService {
    @Resource
    private OrderItemMapper orderItemMapper;

    @Override
    public int delByOrderId(long orderId) {
        return orderItemMapper.delByOrderId(orderId);
    }
}
