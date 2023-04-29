package com.example.springboot.service.impl;

import com.example.springboot.entity.OrderBase;
import com.example.springboot.mapper.BalanceLogMapper;
import com.example.springboot.mapper.OrderBaseMapper;
import com.example.springboot.service.IOrderBaseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * <p>
 *  订单服务实现类
 * </p>
 *
 * @author 
 * @since 2023-04-24
 */
@Service
public class OrderBaseServiceImpl extends ServiceImpl<OrderBaseMapper, OrderBase> implements IOrderBaseService {

    @Resource
    private OrderBaseMapper orderBaseMapper;

    @Override
    public int updatePayInfo(Long orderId, int orderStatus, Date payDate, int payMode, String payVoucherNo) {
        return orderBaseMapper.updatePayInfo(orderId,orderStatus,payDate,payMode,payVoucherNo);
    }

    @Override
    public int updateDeliveryInfo(Long orderId, int orderStatus, Date deliveryDate) {
        return orderBaseMapper.updateDeliveryInfo(orderId,orderStatus,deliveryDate);
    }

    @Override
    public int updateSignInfo(Long orderId, int orderStatus, Date signDate) {
        return orderBaseMapper.updateSignInfo(orderId,orderStatus,signDate);
    }
}
