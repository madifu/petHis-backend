package com.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
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
@ApiModel(value = "Diagnosis对象", description = "")
public class Diagnosis implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("顾客id")
    private Integer customId;

    @ApiModelProperty("顾客姓名")
    private String customName;

    @ApiModelProperty("顾客手机")
    private String customPhone;

    @ApiModelProperty("宠物种类Id")
    private Integer petKindId;

    @ApiModelProperty("宠物种类名称")
    private String petKindName;

    @ApiModelProperty("宠物名称")
    private String petName;

    @ApiModelProperty("会诊所见")
    private String consultations;

    @ApiModelProperty("诊断意见")
    private String opinions;

    @ApiModelProperty("治疗方案")
    private String treatments;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("预约单id")
    private String appointmentId;

    @ApiModelProperty("会诊科室id")
    private Integer deptId;

    @ApiModelProperty("会诊科室名称")
    private String deptName;

    @ApiModelProperty("医生id")
    private Integer doctorId;

    @ApiModelProperty("医生姓名")
    private String doctorName;

    @ApiModelProperty("创建时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;


}
