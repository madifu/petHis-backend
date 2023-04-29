package com.example.springboot.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelWriter;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import java.net.URLEncoder;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

import com.example.springboot.service.IDrugService;
import com.example.springboot.entity.Drug;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 
 * @since 2023-04-21
 */
@RestController
@RequestMapping("/drug")
public class DrugController {

    @Resource
    private IDrugService drugService;

    private final String now = DateUtil.now();

    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody Drug drug) {
        if (drug.getId() == null) {
            //drug.setTime(DateUtil.now());
            //drug.setUser(TokenUtils.getCurrentUser().getUsername());

            return Result.error("-121001", "请输入药品的id(纯数字)");
        }

        if (drug.getName() == null) {
            return Result.error("-121002", "请输入药品的通用名称");
        }

        if (drug.getProductName() == null) {
            drug.setProductName("");
            //return Result.error("-121003", "请输入药品的商品名称");
        }

        if (drug.getProductRemark() == null) {
            drug.setProductRemark("");
        }

        if (drug.getImg() == null) {
            drug.setImg("");
        }

        if (drug.getDosageForm() == null) {
            drug.setDosageForm("");
        }

        if (drug.getSpecs() == null) {
            drug.setSpecs("");
        }

        if (drug.getUnit() == null) {
            drug.setUnit("");
        }

        if (drug.getPacking() == null) {
            drug.setPacking("");
        }

        if (drug.getPackingRemark() == null) {
            drug.setPackingRemark("");
        }

        if (drug.getManufacturer() == null) {
            drug.setManufacturer("");
        }

        if (drug.getPurchasePrice() == null) {
            return Result.error("-121006", "请输入药品的采购价格");
        }

        if (drug.getRetailPrice() == null) {
            return Result.error("-121007", "请输入药品的零售价格");
        }

        if (drug.getRemark() == null) {
            drug.setRemark("");
        }

        drug.setCreateTime(new Date());
        drug.setCreateName(TokenUtils.getCurrentUser().getUsername());
        drug.setCreateBy(String.valueOf(TokenUtils.getCurrentUser().getId()));


        drugService.saveOrUpdate(drug);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        drugService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        drugService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping
    public Result findAll() {
        return Result.success(drugService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(drugService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "") String name,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        QueryWrapper<Drug> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (!"".equals(name)) {
            queryWrapper.like("name", name);
        }
//        User currentUser = TokenUtils.getCurrentUser();
//        if (currentUser.getRole().equals("ROLE_USER")) {
//            queryWrapper.eq("user", currentUser.getUsername());
//        }

//        Page<Drug> drugPage = drugService.page(new Page<>(pageNum, pageSize), queryWrapper);

        return Result.success(drugService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    /**
    * 导出接口
    */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        // 从数据库查询出所有的数据
        List<Drug> list = drugService.list();
        // 在内存操作，写出到浏览器
        ExcelWriter writer = ExcelUtil.getWriter(true);

        // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
        writer.write(list, true);

        // 设置浏览器响应的格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("Drug信息表", "UTF-8");
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
        List<Drug> list = reader.readAll(Drug.class);

        drugService.saveBatch(list);
        return Result.success();
    }

    private User getUser() {
        return TokenUtils.getCurrentUser();
    }

}

