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
 * @since 2023-04-23
 */
@Getter
@Setter
@ApiModel(value = "Appointment对象", description = "")
public class Appointment implements Serializable {

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

    @ApiModelProperty("医生id")
    private Integer doctorId;

    @ApiModelProperty("医生姓名")
    private String doctorName;

    @ApiModelProperty("医生手机")
    private String doctorPhone;

    @ApiModelProperty("预约时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date appointmentTime;

    @ApiModelProperty("预约状态,0-未知,0-未知,10-待审核,20-已接受,21-已婉拒,30-已到诊,31-未到诊,40-已过期")
    private Integer status;

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
     * 预约状态
     */
    public interface AppointmentStatus{
        /** 0-未知 */
        final int  UNKNOWN = 0;

        /** 10-待审核 */
        final int  WAIT_AUDIT = 10;

        /** 20-已接受 */
        final int  ACCEPT = 20;

        /** 21-已婉拒 */
        final int  REFUSE = 21;

        /** 30-已到诊 */
        final int  PRESENT = 30;

        /** 31-未到诊 */
        final int  UNPRESENT = 31;

        /** 40-已过期 */
        final int  Expired = 40;
    }
}
