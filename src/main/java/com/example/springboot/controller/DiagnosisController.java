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
import com.example.springboot.entity.Appointment;
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

import com.example.springboot.service.IDiagnosisService;
import com.example.springboot.entity.Diagnosis;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 
 * @since 2023-04-24
 */
@RestController
@RequestMapping("/diagnosis")
public class DiagnosisController {

    @Resource
    private IDiagnosisService diagnosisService;

    @Resource
    private IUserService userService;

    @Resource
    private IDoctorService doctorService;

    private final String now = DateUtil.now();

    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody Diagnosis diagnosis) {
        if (null == diagnosis.getPetKindId()) {
            return Result.error(Constants.CODE_404, "请选择宠物种类");
        }

        //查找顾客信息
        QueryWrapper<User> queryWrapperCustom = new QueryWrapper<>();
        queryWrapperCustom.eq("username", diagnosis.getCustomName());
        queryWrapperCustom.eq("role", RoleEnum.ROLE_USER.toString());
        User findCustom = userService.getOne(queryWrapperCustom);
        if (null == findCustom) {
            return Result.error(Constants.CODE_404, "顾客不存在");
        }
        diagnosis.setCustomId(findCustom.getId());
        diagnosis.setCustomName(findCustom.getUsername());
        diagnosis.setCustomPhone(findCustom.getPhone());

        //查找医生信息
        QueryWrapper<User> queryWrapperDoctor = new QueryWrapper<>();
        queryWrapperDoctor.eq("username", diagnosis.getDoctorName());
        queryWrapperDoctor.eq("role", RoleEnum.ROLE_DOCTOR.toString());
        User findDoctor = userService.getOne(queryWrapperDoctor);
        if (null == findDoctor) {
            return Result.error(Constants.CODE_404, "医生不存在");
        }
        diagnosis.setDoctorId(findDoctor.getId());
        diagnosis.setDoctorName(findDoctor.getUsername());
        diagnosis.setDeptId(findDoctor.getDeptId());
        diagnosis.setDeptName(findDoctor.getDeptName());

        if (diagnosis.getPetName()  == null) {
            diagnosis.setPetName("");
        }

        if (diagnosis.getConsultations()  == null) {
            diagnosis.setConsultations("");
        }

        if (diagnosis.getOpinions()  == null) {
            diagnosis.setOpinions("");
        }

        if (diagnosis.getTreatments()  == null) {
            diagnosis.setTreatments("");
        }

        if (diagnosis.getRemark() == null) {
            diagnosis.setRemark("");
        }

        if (diagnosis.getAppointmentId() == null) {
            diagnosis.setAppointmentId("");
        }



        if (diagnosis.getId() == null) {
            //添加操作
            diagnosis.setCreateTime(new Date());
        }else {
            //更新操作
            Diagnosis oldDiagnosis = diagnosisService.getById(diagnosis.getId());
            if (null==oldDiagnosis) {
                return Result.error(Constants.CODE_404, "诊断记录不存在");
            }

            diagnosis.setCreateTime(oldDiagnosis.getCreateTime());
        }
        diagnosisService.saveOrUpdate(diagnosis);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        diagnosisService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        diagnosisService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping
    public Result findAll() {
        return Result.success(diagnosisService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(diagnosisService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String customName,
                           @RequestParam(defaultValue = "") String petName) {
        QueryWrapper<Diagnosis> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (!"".equals(customName)) {
            queryWrapper.like("custom_name", customName);
        }

        if (!"".equals(petName)) {
            queryWrapper.like("pet_name", petName);
        }


        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser.getRole().equals(RoleEnum.ROLE_USER.toString())) {
            //普通用户只能查看自己的诊断记录
            queryWrapper.eq("custom_id", currentUser.getId());
        }else if (currentUser.getRole().equals(RoleEnum.ROLE_DOCTOR.toString())) {
            //医生可以查看自己的诊断记录
            queryWrapper.eq("doctor_id", currentUser.getId());
        }
//        User currentUser = TokenUtils.getCurrentUser();
//        if (currentUser.getRole().equals("ROLE_USER")) {
//            queryWrapper.eq("user", currentUser.getUsername());
//        }
        return Result.success(diagnosisService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    /**
    * 导出接口
    */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        // 从数据库查询出所有的数据
        List<Diagnosis> list = diagnosisService.list();
        // 在内存操作，写出到浏览器
        ExcelWriter writer = ExcelUtil.getWriter(true);

        // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
        writer.write(list, true);

        // 设置浏览器响应的格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("Diagnosis信息表", "UTF-8");
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
        List<Diagnosis> list = reader.readAll(Diagnosis.class);

        diagnosisService.saveBatch(list);
        return Result.success();
    }

    private User getUser() {
        return TokenUtils.getCurrentUser();
    }

}

