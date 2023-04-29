package com.example.springboot.controller;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.Constants;
import com.example.springboot.common.Result;
import com.example.springboot.common.RoleEnum;
import com.example.springboot.entity.User;
import com.example.springboot.service.IDoctorService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 医生管理前端控制器
 * </p>
 *
 * @author
 * @since 2022-04-23
 */
@RestController
@RequestMapping("/doctor")
public class DoctorController {

    @Value("${files.upload.path}")
    private String filesUploadPath;

    @Resource
    private IDoctorService doctorService;



    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody User user) {
        String username = user.getUsername();
        if (StrUtil.isBlank(username)) {
            return Result.error(Constants.CODE_400, "参数错误,请输入医生姓名");
        }

        if (user.getBrief()==null) {
            user.setBrief("");
        }

        if (user.getTitle()==null) {
            user.setTitle("");
        }

        if (user.getGender()==null) {
            user.setGender(User.Gender.NONE);
        }

        if (user.getId() != null) {
            //更新操作
            user.setPassword(null);

            //不更新余额字段,  取出原来的 余额
            User  oldUser = doctorService.getById(user.getId());
            if (null==oldUser) {
                return Result.error(Constants.CODE_404, "医生不存在");
            }

            user.setBalance(oldUser.getBalance());

        } else {
            //添加操作
            user.setCreateTime(new Date());
            user.setNickname(user.getUsername());
            if (user.getPassword() == null) {
                user.setPassword("123");
            }
        }

        user.setRole(RoleEnum.ROLE_DOCTOR.toString());
        return Result.success(doctorService.saveOrUpdate(user));
    }





    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(doctorService.removeById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(doctorService.removeByIds(ids));
    }

    @GetMapping
    public Result findAll() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role", RoleEnum.ROLE_DOCTOR.toString());

        return Result.success(doctorService.list(queryWrapper));
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(doctorService.getById(id));
    }


    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String username,
                           @RequestParam(defaultValue = "") String phone) {

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (!"".equals(username)) {
            queryWrapper.like("username", username);
        }
        if (!"".equals(phone)) {
            queryWrapper.like("phone", phone);
        }


        queryWrapper.eq("role", RoleEnum.ROLE_DOCTOR.toString());

        return Result.success(doctorService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    /**
     * 导出接口
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        // 从数据库查询出所有的数据
        List<User> list = doctorService.list();
        // 通过工具类创建writer 写出到磁盘路径
//        ExcelWriter writer = ExcelUtil.getWriter(filesUploadPath + "/用户信息.xlsx");
        // 在内存操作，写出到浏览器
        ExcelWriter writer = ExcelUtil.getWriter(true);
        //自定义标题别名
        writer.addHeaderAlias("username", "医生姓名");
        writer.addHeaderAlias("password", "密码");
        writer.addHeaderAlias("nickname", "昵称");

        writer.addHeaderAlias("email", "邮箱");
        writer.addHeaderAlias("phone", "电话");
        writer.addHeaderAlias("address", "地址");
        writer.addHeaderAlias("createTime", "创建时间");
        writer.addHeaderAlias("avatarUrl", "头像");

        writer.addHeaderAlias("deptId", "科室Id");
        writer.addHeaderAlias("deptName", "科室名称");
        writer.addHeaderAlias("brief", "简介");

        // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
        writer.write(list, true);

        // 设置浏览器响应的格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("医生信息", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        out.close();
        writer.close();

    }

    /**
     * excel 导入
     *
     * @param file
     * @throws Exception
     */
    @PostMapping("/import")
    public Result imp(MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        // 方式1：(推荐) 通过 javabean的方式读取Excel内的对象，但是要求表头必须是英文，跟javabean的属性要对应起来
//        List<User> list = reader.readAll(User.class);

        // 方式2：忽略表头的中文，直接读取表的内容
        List<List<Object>> list = reader.read(1);
        List<User> users = CollUtil.newArrayList();
        for (List<Object> row : list) {
            User user = new User();
            user.setUsername(row.get(0).toString());
            user.setPassword(row.get(1).toString());
            user.setNickname(row.get(2).toString());
            user.setEmail(row.get(3).toString());
            user.setPhone(row.get(4).toString());
            user.setAddress(row.get(5).toString());
            user.setAvatarUrl(row.get(6).toString());

            user.setRole(RoleEnum.ROLE_DOCTOR.toString());
            users.add(user);
        }

        doctorService.saveBatch(users);
        return Result.success(true);
    }

}

