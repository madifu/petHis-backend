package com.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

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
@TableName("order_base")
@ApiModel(value = "OrderBase对象", description = "")
public class OrderBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @ApiModelProperty("项目简要")
    private String overview;

    @ApiModelProperty("用户id")
    private Integer userId;

    @ApiModelProperty("用户姓名")
    private String userName;

    @ApiModelProperty("收货人电话")
    private String receiverPhone;

    @ApiModelProperty("收货人地址")
    private String receiverAddress;

    @ApiModelProperty("订单总金额")
    private BigDecimal amount;

    @ApiModelProperty("订单状态.0-未知,10-待支付,20-已支付(待发货),30-已发货,40-已收货")
    private Integer status;

    @ApiModelProperty("支付方式.0-未知,10-余额支付,20-淘宝支付,30-微信支付")
    private Integer payMode;

    @ApiModelProperty("支付时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date payTime;

    @ApiModelProperty("支付凭证号")
    private String payVoucherNo;

    @ApiModelProperty("发货时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date deliveryTime;

    @ApiModelProperty("签收时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date signTime;


    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建人名称")
    private String createName;

    @ApiModelProperty("创建人账号ID")
    private String createBy;

    @ApiModelProperty("创建人角色")
    private String createRole;

    @ApiModelProperty("创建时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;


    /**
     * 订单状态
     */
    public interface OrderStatus{
        /** 0-未知 */
        final int  UNKNOWN = 0;

        /** 10-待支付 */
        final int  UN_PAY = 10;

        /** 20-已支付，待发货 */
        final int  PAYED = 20;

        /** 30-已发货 */
        final int  DELIVERED = 30;

        /** 40-已收货 */
        final int  SIGN = 40;

    }

    /**
     * 支付方式
     */
    public interface PayMode{
        /** 0-未知 */
        final int  UNKNOWN = 0;

        /** 10-余额支付 */
        final int  BALANCE = 10;

        /** 20-淘宝支付 */
        final int  TAOBAO = 20;

        /** 30-微信支付 */
        final int  WX = 30;

    }


}
