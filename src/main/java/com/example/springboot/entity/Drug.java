package com.example.springboot.entity;

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
 * @since 2023-04-21
 */
@Getter
@Setter
@ApiModel(value = "Drug对象", description = "")
public class Drug implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("通用名称")
    private String name;

    @ApiModelProperty("商品名称")
    private String productName;

    @ApiModelProperty("商品备注")
    private String productRemark;

    @ApiModelProperty("图片url")
    private String img;

    @ApiModelProperty("剂型")
    private String dosageForm;

    @ApiModelProperty("规格")
    private String specs;

    @ApiModelProperty("单位")
    private String unit;

    @ApiModelProperty("包装")
    private String packing;

    @ApiModelProperty("包装备注")
    private String packingRemark;

    @ApiModelProperty("生产厂家")
    private String manufacturer;

    @ApiModelProperty("采购价格")
    private BigDecimal purchasePrice;

    @ApiModelProperty("零售价格")
    private BigDecimal retailPrice;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建人名称")
    private String createName;

    @ApiModelProperty("创建人账号")
    private String createBy;

    @ApiModelProperty("创建时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;


}
