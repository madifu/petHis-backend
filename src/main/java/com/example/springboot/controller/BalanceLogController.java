package com.example.springboot.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelWriter;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.Constants;
import com.example.springboot.common.RoleEnum;
import com.example.springboot.entity.OrderBase;
import com.example.springboot.service.IOrderBaseService;
import com.example.springboot.service.IUserService;
import org.apache.commons.lang3.StringUtils;
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

import com.example.springboot.service.IBalanceLogService;
import com.example.springboot.entity.BalanceLog;

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
@RequestMapping("/balanceLog")
public class BalanceLogController {

    @Resource
    private IBalanceLogService balanceLogService;

    @Resource
    private IUserService userService;

    @Resource
    private IOrderBaseService orderBaseService ;

    private final String now = DateUtil.now();

//    // 新增或者更新
//    @PostMapping
//    public Result save(@RequestBody BalanceLog balanceLog) {
//        if (balanceLog.getId() == null) {
//            //balanceLog.setTime(DateUtil.now());
//            //balanceLog.setUser(TokenUtils.getCurrentUser().getUsername());
//        }
//        balanceLogService.saveOrUpdate(balanceLog);
//        return Result.success();
//    }

    // 充值
    @PostMapping("/recharge")
    public Result recharge(@RequestBody BalanceLog balanceLog) {
        if (balanceLog.getId() == null) {
            //balanceLog.setTime(DateUtil.now());
            //balanceLog.setUser(TokenUtils.getCurrentUser().getUsername());
        }else {
            return Result.error(Constants.CODE_400, "参数错误:系统不提供充值记录更新功能");
        }

        if (balanceLog.getOperationAmount() == null) {
            return Result.error(Constants.CODE_400, "请输入充值金额");
        }


        if (balanceLog.getOperationAmount().doubleValue() < 0 ) {
            return Result.error(Constants.CODE_400, "参数错误:充值金额必须大于0");
        }

        if (balanceLog.getBusinessType() == null) {
            //return Result.error(Constants.CODE_400, "参数错误:请提供业务类型");
            balanceLog.setBusinessType(BalanceLog.BusinessType.RECHARGE_BY_CASH);  //现金充值
        }

        if (balanceLog.getRemark() == null) {
            balanceLog.setRemark("");
        }

        //判断用户是否存在
        QueryWrapper<User> queryWrapperCustom = new QueryWrapper<>();
        queryWrapperCustom.eq("username", balanceLog.getUserName());
        queryWrapperCustom.eq("role", RoleEnum.ROLE_USER.toString());
        User findUser = userService.getOne(queryWrapperCustom);
        if (null == findUser) {
            return Result.error(Constants.CODE_404, "用户不存在");
        }
        balanceLog.setUserId(findUser.getId());

        BigDecimal beforeBalance = findUser.getBalance();
        BigDecimal afterBalance = beforeBalance.add(balanceLog.getOperationAmount());
        balanceLog.setBeforeBalance(beforeBalance);
        balanceLog.setAfterBalance(afterBalance);

        balanceLog.setTranMode(BalanceLog.TranMode.RECHARGE);

        User currentUser = TokenUtils.getCurrentUser();
        balanceLog.setCreateName(currentUser.getUsername());
        balanceLog.setCreateBy(String.valueOf(currentUser.getId()));
        balanceLog.setCreateRole(currentUser.getRole());
        balanceLog.setCreateTime(new Date());

        if (balanceLogService.saveOrUpdate(balanceLog)) {
            //余额日志保存成功后，更新该用户的余额
            if (userService.incBalance(balanceLog.getUserId(), balanceLog.getOperationAmount())>0) {
                return Result.success();
            }else {
                return Result.error(Constants.CODE_600, "更新用户的余额失败");
            }

        }else {
            return Result.error(Constants.CODE_600, "保存余额日志失败");
        }

    }

    // 订单支付
    @PostMapping("/orderPay")
    public Result orderPay(@RequestBody BalanceLog balanceLog) {
        if (balanceLog.getId() == null) {
            //balanceLog.setTime(DateUtil.now());
            //balanceLog.setUser(TokenUtils.getCurrentUser().getUsername());
        }else {
            return Result.error(Constants.CODE_400, "参数错误:系统不提供充值记录更新功能");
        }

        if (balanceLog.getOperationAmount() == null) {
            return Result.error(Constants.CODE_400, "请输入订单支付金额");
        }


        if (balanceLog.getOperationAmount().doubleValue() < 0 ) {
            return Result.error(Constants.CODE_400, "参数错误:订单支付金额必须大于0");
        }

        if (StringUtils.isEmpty(balanceLog.getBusinessNo())) {
            return Result.error(Constants.CODE_400, "参数错误:缺少业务单号");
        }

        if (StringUtils.isEmpty(balanceLog.getUserName())) {
            return Result.error(Constants.CODE_400, "参数错误:缺少用户姓名");
        }

        //判断 订单是否存在
        Long orderId = Long.parseLong(balanceLog.getBusinessNo());
        OrderBase orderBase =  orderBaseService.getById(orderId);
        if (orderBase == null) {
            return Result.error(Constants.CODE_404, "订单不存在. 订单id=" + orderId);
        }
        balanceLog.setOperationAmount(orderBase.getAmount());


        balanceLog.setTranMode(BalanceLog.TranMode.DEDUCT);
        balanceLog.setBusinessType(BalanceLog.BusinessType.ORDER_PAY);  //订单支付
//        if (balanceLog.getBusinessType() == null) {
//            //return Result.error(Constants.CODE_400, "参数错误:请提供业务类型");
//            balanceLog.setBusinessType(BalanceLog.BusinessType.ORDER_PAY);  //订单支付
//        }

        if (balanceLog.getRemark() == null) {
            balanceLog.setRemark("");
        }

        //判断用户是否存在
        QueryWrapper<User> queryWrapperCustom = new QueryWrapper<>();
        queryWrapperCustom.eq("username", balanceLog.getUserName());
        queryWrapperCustom.eq("role", RoleEnum.ROLE_USER.toString());
        User findUser = userService.getOne(queryWrapperCustom);
        if (null == findUser) {
            return Result.error(Constants.CODE_404, "用户不存在. 用户名为:" + balanceLog.getUserName());
        }
        balanceLog.setUserId(findUser.getId());

        BigDecimal beforeBalance = findUser.getBalance();
        BigDecimal afterBalance = beforeBalance.subtract(balanceLog.getOperationAmount());

        if (afterBalance.doubleValue()<0) {
            return Result.error(Constants.CODE_600, "余额不足,无法完成订单支付,请及时充值. 当前余额为：" + beforeBalance.doubleValue());
        }
        balanceLog.setBeforeBalance(beforeBalance);
        balanceLog.setAfterBalance(afterBalance);
        balanceLog.setOperationAmount( new BigDecimal(0.0).subtract(balanceLog.getOperationAmount()) );

        User currentUser = TokenUtils.getCurrentUser();
        balanceLog.setCreateName(currentUser.getUsername());
        balanceLog.setCreateBy(String.valueOf(currentUser.getId()));
        balanceLog.setCreateRole(currentUser.getRole());
        balanceLog.setCreateTime(new Date());


        if (balanceLogService.saveOrUpdate(balanceLog)) {
            //余额日志保存成功后，更新该用户的余额
            if (userService.decBalance(balanceLog.getUserId(), balanceLog.getOperationAmount())>0) {
                //支付成功，更新订单的状态和支付时间,支付方式，支付凭证号
                if (orderBaseService.updatePayInfo(orderId, OrderBase.OrderStatus.PAYED, new Date(), OrderBase.PayMode.BALANCE, "")>0) {
                    return Result.success();
                }else {
                    return Result.error(Constants.CODE_600, "更新订单的支付信息失败");
                }

            }else {
                return Result.error(Constants.CODE_600, "更新用户的余额失败");
            }

        }else {
            return Result.error(Constants.CODE_600, "保存余额日志失败");
        }
    }

//    @DeleteMapping("/{id}")
//    public Result delete(@PathVariable Integer id) {
//        balanceLogService.removeById(id);
//        return Result.success();
//    }
//
//    @PostMapping("/del/batch")
//    public Result deleteBatch(@RequestBody List<Integer> ids) {
//        balanceLogService.removeByIds(ids);
//        return Result.success();
//    }

//    @GetMapping
//    public Result findAll() {
//        return Result.success(balanceLogService.list());
//    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(balanceLogService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "") String userName,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        QueryWrapper<BalanceLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (!"".equals(userName)) {
            queryWrapper.like("user_name", userName);
        }

        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser.getRole().equals(RoleEnum.ROLE_USER.toString())) {
            //普通用户只能查看自己的记录
            queryWrapper.eq("user_id", currentUser.getId());
        }else if (currentUser.getRole().equals(RoleEnum.ROLE_DOCTOR.toString())) {
            //医生可以查看自己的记录
            queryWrapper.eq("user_id", currentUser.getId());
        }

        return Result.success(balanceLogService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    /**
    * 导出接口
    */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        // 从数据库查询出所有的数据
        List<BalanceLog> list = balanceLogService.list();
        // 在内存操作，写出到浏览器
        ExcelWriter writer = ExcelUtil.getWriter(true);

        // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
        writer.write(list, true);

        // 设置浏览器响应的格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("BalanceLog信息表", "UTF-8");
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
        List<BalanceLog> list = reader.readAll(BalanceLog.class);

        balanceLogService.saveBatch(list);
        return Result.success();
    }

    private User getUser() {
        return TokenUtils.getCurrentUser();
    }

}

