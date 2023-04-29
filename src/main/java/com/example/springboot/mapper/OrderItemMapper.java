package com.example.springboot.mapper;

import com.example.springboot.entity.OrderItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 
 * @since 2023-04-24
 */
public interface OrderItemMapper extends BaseMapper<OrderItem> {


    /**
     * 根据订单ID 删除订单项目
     * @param orderId
     * @return
     */
    @Update("delete from order_item where order_id=#{orderId}")
    int delByOrderId(Long orderId);
}
