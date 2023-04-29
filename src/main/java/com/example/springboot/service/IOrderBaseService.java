package com.example.springboot.service;

import com.example.springboot.entity.OrderBase;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 * @since 2023-04-24
 */
@Component
public interface IOrderBaseService extends IService<OrderBase> {

    /**
     * 更新订单支付信息，包括状态、支付时间,支付方式，支付凭证号
     * @param orderId               订单id
     * @param orderStatus           订单状态
     * @param payDate               支付时间
     * @param payMode               支付方式
     * @param payVoucherNo          支付凭证号
     * @return
     */
    int updatePayInfo(Long orderId, int orderStatus, Date payDate, int payMode, String payVoucherNo);

    /**
     * 更新订单发货信息，包括状态、发货时间
     * @param orderId
     * @param orderStatus
     * @param deliveryDate
     * @return
     */
    int updateDeliveryInfo(Long orderId, int orderStatus, Date deliveryDate);

    /**
     * 更新订单签收信息，包括状态、签收时间
     * @param orderId
     * @param orderStatus
     * @param signDate
     * @return
     */
    int updateSignInfo(Long orderId, int orderStatus, Date signDate);
}
