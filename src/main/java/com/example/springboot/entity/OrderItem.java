package com.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author 
 * @since 2023-04-24
 */
@Getter
@Setter
@TableName("order_item")
@ApiModel(value = "OrderItem对象", description = "")
public class OrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("订单主表id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long orderId;

    @ApiModelProperty("项目id")
    private Long itemId;

    @ApiModelProperty("项目名称")
    private String name;

    @ApiModelProperty("项目单价")
    private BigDecimal price;

    @ApiModelProperty("项目单位")
    private String unit;

    @ApiModelProperty("项目数量")
    private Integer qty;

    @ApiModelProperty("排序(小的在前)")
    private Integer seq;


}
