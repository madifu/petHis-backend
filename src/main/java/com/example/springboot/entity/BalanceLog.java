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
@TableName("balance_log")
@ApiModel(value = "BalanceLog对象", description = "")
public class BalanceLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("用户id")
    private Integer userId;

    @ApiModelProperty("用户姓名")
    private String userName;

    @ApiModelProperty("交易方式.0-未知,10-充值,20-扣减")
    private Integer tranMode;

    @ApiModelProperty("业务类型.0-未知,10-现金充值,11-第三方交易充值,20-订单支付,30-退款")
    private Integer businessType;

    @ApiModelProperty("业务单号")
    private String businessNo;

    @ApiModelProperty("交易前余额")
    private BigDecimal beforeBalance;

    @ApiModelProperty("操作金额")
    private BigDecimal operationAmount;

    @ApiModelProperty("交易后余额")
    private BigDecimal afterBalance;

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
     * 交易方式
     */
    public interface TranMode{
        /** 0-未知 */
        final int  UNKNOWN = 0;

        /** 10-充值 */
        final int  RECHARGE = 10;

        /** 20-扣减 */
        final int  DEDUCT = 20;
    }


    /**
     * 业务类型
     */
    public interface BusinessType{
        /** 0-未知 */
        final int  UNKNOWN = 0;

        /** 10-现金充值 */
        final int  RECHARGE_BY_CASH = 10;

        /** 11-淘宝交易充值 */
        final int  RECHARGE_BY_TAOBAO = 11;

        /** 12-微信交易充值 */
        final int  RECHARGE_BY_WX = 12;

        /** 20-订单支付 */
        final int  ORDER_PAY = 20;

        /** 30-退款 */
        final int  REFUND = 30;

    }







}
