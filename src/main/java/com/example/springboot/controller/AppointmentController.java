package com.example.springboot.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelWriter;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import java.net.URLEncoder;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.Constants;
import com.example.springboot.common.RoleEnum;
import com.example.springboot.service.IDoctorService;
import com.example.springboot.service.IUserService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.springboot.common.Result;
import org.springframework.web.multipart.MultipartFile;
import com.example.springboot.entity.User;
import com.example.springboot.utils.TokenUtils;

import com.example.springboot.service.IAppointmentService;
import com.example.springboot.entity.Appointment;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  预约管理控制器
 * </p>
 *
 * @author 
 * @since 2023-04-23
 */
@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    @Resource
    private IAppointmentService appointmentService;

    @Resource
    private IUserService userService;

    @Resource
    private IDoctorService doctorService;

    private final String now = DateUtil.now();

    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody Appointment appointment) {
        if (appointment.getAppointmentTime() == null) {
            return Result.error(Constants.CODE_400, "参数错误:请提供预约时间");
        }

        //查找顾客信息
        QueryWrapper<User> queryWrapperCustom = new QueryWrapper<>();
        queryWrapperCustom.eq("username", appointment.getCustomName());
        queryWrapperCustom.eq("role", RoleEnum.ROLE_USER.toString());
        User findCustom = userService.getOne(queryWrapperCustom);
        if (null == findCustom) {
            return Result.error(Constants.CODE_404, "顾客不存在");
        }
        appointment.setCustomId(findCustom.getId());
        appointment.setCustomName(findCustom.getUsername());
        appointment.setCustomPhone(findCustom.getPhone());

        //查找医生信息
        QueryWrapper<User> queryWrapperDoctor = new QueryWrapper<>();
        queryWrapperDoctor.eq("username", appointment.getDoctorName());
        queryWrapperDoctor.eq("role", RoleEnum.ROLE_DOCTOR.toString());
        User findDoctor = userService.getOne(queryWrapperDoctor);
        if (null == findDoctor) {
            return Result.error(Constants.CODE_404, "医生不存在");
        }
        appointment.setDoctorId(findDoctor.getId());
        appointment.setDoctorName(findDoctor.getUsername());
        appointment.setDoctorPhone(findDoctor.getPhone());

        if (appointment.getRemark() == null) {
            appointment.setRemark("");
        }

        if (appointment.getId() == null) {
            //添加操作
            appointment.setStatus(Appointment.AppointmentStatus.WAIT_AUDIT);

            User currentUser = TokenUtils.getCurrentUser();
            appointment.setCreateName(currentUser.getUsername());
            appointment.setCreateBy(String.valueOf(currentUser.getId()));
            appointment.setCreateRole(currentUser.getRole());
            appointment.setCreateTime(new Date());
        }else {
            //更新操作
            if (appointment.getStatus() == null) {
                return Result.error(Constants.CODE_400, "参数错误:请提供预约状态");
            }

            //不更新原 创建人信息
            Appointment  oldAppointment = appointmentService.getById(appointment.getId());
            if (null==oldAppointment) {
                return Result.error(Constants.CODE_404, "预约记录不存在");
            }

            appointment.setCreateName(oldAppointment.getCreateName());
            appointment.setCreateBy(oldAppointment.getCreateBy());
            appointment.setCreateRole(oldAppointment.getCreateRole());
            appointment.setCreateTime(oldAppointment.getCreateTime());
        }
        appointmentService.saveOrUpdate(appointment);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        appointmentService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Long> ids) {
        appointmentService.removeByIds(ids);
        return Result.success();
    }

//    @GetMapping
//    public Result findAll() {
//        return Result.success(appointmentService.list());
//    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Long id) {
        return Result.success(appointmentService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String customName,
                           @RequestParam(defaultValue = "") String doctorName) {
        QueryWrapper<Appointment> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (!"".equals(customName)) {
            queryWrapper.like("custom_name", customName);
        }

        if (!"".equals(doctorName)) {
            queryWrapper.like("doctor_name", doctorName);
        }

        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser.getRole().equals(RoleEnum.ROLE_USER.toString())) {
            //普通用户只能查看自己的预约记录
            queryWrapper.eq("create_by", currentUser.getId());
        }else if (currentUser.getRole().equals(RoleEnum.ROLE_DOCTOR.toString())) {
            //医生可以查看自己的记录和 别人约自己的
            queryWrapper.eq("create_by", currentUser.getId()).or().eq("doctor_id", currentUser.getId());
        }
        return Result.success(appointmentService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    /**
    * 导出接口
    */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        // 从数据库查询出所有的数据
        List<Appointment> list = appointmentService.list();
        // 在内存操作，写出到浏览器
        ExcelWriter writer = ExcelUtil.getWriter(true);

        // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
        writer.write(list, true);

        // 设置浏览器响应的格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("Appointment信息表", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        out.close();
        writer.close();

        }

    /**
     * excel 导入
     * @param file
     * @throws Exception
     */
    @PostMapping("/import")
    public Result imp(MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        // 通过 javabean的方式读取Excel内的对象，但是要求表头必须是英文，跟javabean的属性要对应起来
        List<Appointment> list = reader.readAll(Appointment.class);

        appointmentService.saveBatch(list);
        return Result.success();
    }

    private User getUser() {
        return TokenUtils.getCurrentUser();
    }

}

