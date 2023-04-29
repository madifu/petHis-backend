package com.example.springboot.controller.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.example.springboot.entity.OrderBase;
import com.example.springboot.entity.OrderItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 接受前端订单请求的参数
 */
@Data
public class OrderDTO extends OrderBase {

    @ApiModelProperty("订单项目")
    private List<OrderItem> items;


}
