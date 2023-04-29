package com.example.springboot.mapper;

import com.example.springboot.entity.OrderBase;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 
 * @since 2023-04-24
 */
public interface OrderBaseMapper extends BaseMapper<OrderBase> {

    /**
     * 更新订单支付信息，包括状态、支付时间,支付方式，支付凭证号
     * @param orderId               订单id
     * @param orderStatus           订单状态
     * @param payDate               支付时间
     * @param payMode               支付方式
     * @param payVoucherNo          支付凭证号
     * @return
     */
    @Update("update order_base set status = #{orderStatus}, pay_mode = #{payMode}, pay_time = #{payDate} , pay_voucher_no=#{payVoucherNo}  where id = #{orderId} ")
    int updatePayInfo(Long orderId, int orderStatus, Date payDate, int payMode, String payVoucherNo);


    /**
     * 更新订单发货信息，包括状态、发货时间
     * @param orderId
     * @param orderStatus
     * @param deliveryDate
     * @return
     */
    @Update("update order_base set status = #{orderStatus}, delivery_time = #{deliveryDate}  where id = #{orderId} ")
    int updateDeliveryInfo(Long orderId, int orderStatus, Date deliveryDate);

    /**
     * 更新订单签收信息，包括状态、签收时间
     * @param orderId
     * @param orderStatus
     * @param signDate
     * @return
     */
    @Update("update order_base set status = #{orderStatus}, sign_time = #{signDate}  where id = #{orderId} ")
    int updateSignInfo(Long orderId, int orderStatus, Date signDate);
}
