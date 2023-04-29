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
import com.example.springboot.controller.dto.OrderDTO;
import com.example.springboot.entity.Drug;
import com.example.springboot.entity.OrderItem;
import com.example.springboot.service.IDrugService;
import com.example.springboot.service.IOrderItemService;
import com.example.springboot.service.IUserService;
import com.example.springboot.utils.OrderIdGenerator;
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

import com.example.springboot.service.IOrderBaseService;
import com.example.springboot.entity.OrderBase;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  订单管理控制器
 * </p>
 *
 * @author 
 * @since 2023-04-24
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Resource
    private IOrderBaseService orderBaseService;

    @Resource
    private IOrderItemService orderItemService;

    @Resource
    private IDrugService drugService;

    @Resource
    private IUserService userService;

    private final String now = DateUtil.now();

    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody OrderDTO orderDTO) {
        if (orderDTO.getItems() == null || orderDTO.getItems().size()<1) {
            return Result.error(Constants.CODE_400, "参数错误.订单缺少项目信息");
        }

        if (StringUtils.isEmpty(orderDTO.getReceiverPhone())) {
            return Result.error(Constants.CODE_400, "参数错误.请提供收货人电话");
        }

        if (StringUtils.isEmpty(orderDTO.getReceiverAddress())) {
            return Result.error(Constants.CODE_400, "参数错误.请提供收货人地址");
        }

        //判断用户是否存在
        QueryWrapper<User> queryWrapperCustom = new QueryWrapper<>();
        queryWrapperCustom.eq("username", orderDTO.getUserName());
        queryWrapperCustom.eq("role", RoleEnum.ROLE_USER.toString());
        User findUser = userService.getOne(queryWrapperCustom);
        if (null == findUser) {
            return Result.error(Constants.CODE_404, "用户不存在");
        }

        if (orderDTO.getRemark() == null) {
            orderDTO.setRemark("");
        }

        BigDecimal totalAmount = new BigDecimal(0);

        OrderBase orderBase = new OrderBase();

        orderBase.setStatus(OrderBase.OrderStatus.UN_PAY);

        orderBase.setPayMode(OrderBase.PayMode.UNKNOWN);
        orderBase.setPayVoucherNo("");

        orderBase.setUserId(findUser.getId());
        orderBase.setUserName(findUser.getUsername());
        orderBase.setReceiverPhone(orderDTO.getReceiverPhone());
        orderBase.setReceiverAddress(orderDTO.getReceiverAddress());
        orderBase.setRemark(orderDTO.getRemark());

        long oid = 0;
        if (orderDTO.getId() == null) {
            //添加操作
            OrderIdGenerator orderIdGenerator = new OrderIdGenerator();
            oid = orderIdGenerator.nextId(this);
            orderDTO.setId(oid);

            User currentUser = TokenUtils.getCurrentUser();
            orderBase.setCreateName(currentUser.getUsername());
            orderBase.setCreateBy(String.valueOf(currentUser.getId()));
            orderBase.setCreateRole(currentUser.getRole());
            orderBase.setCreateTime(new Date());

        }else {
            //更新操作
            oid = orderDTO.getId();

            //找出原订单信息
            OrderBase oldOrder = orderBaseService.getById(oid);
            if (null == oldOrder) {
                return Result.error(Constants.CODE_404, "订单记录不存在.id=" + oid);
            }

            //只有待支付状态的订单才 可以 修改
            if (oldOrder.getStatus() != OrderBase.OrderStatus.UN_PAY) {
                return Result.error(Constants.CODE_600, "只有待支付状态的订单才可以修改 .id=" + oid);
            }

            orderBase.setCreateName(oldOrder.getCreateName());
            orderBase.setCreateBy(oldOrder.getCreateBy());
            orderBase.setCreateRole(oldOrder.getCreateRole());
            orderBase.setCreateTime(oldOrder.getCreateTime());

            //删除原来项目 表的内容
            orderItemService.delByOrderId(oid);
        }

        //先保存项目表
        int seq = 0;
        String orderOverview = "" ; // 订单项目简要
        for (OrderItem orderItem: orderDTO.getItems() ) {
            orderItem.setOrderId(oid);

            //处理 序号
            seq++;
            orderItem.setSeq(seq);

            if (orderItem.getItemId() == null ) {
                return Result.error(Constants.CODE_400, "参数错误. 订单项目id为Null");
            };

            if (orderItem.getQty() == null ) {
                return Result.error(Constants.CODE_400, "参数错误. 订单项目数量为Null");
            }

            if (orderItem.getQty() <= 0 ) {
                return Result.error(Constants.CODE_400, "参数错误. 订单项目数量必须大于0");
            }

            //数据库中查找商品是否存在
            Drug drug = drugService.getById(orderItem.getItemId());
            if (null == drug) {
                return Result.error(Constants.CODE_400, "参数错误. 商品不存在. 项目id=" + orderItem.getItemId());
            }
            orderItem.setPrice(drug.getRetailPrice());
            orderItem.setName(drug.getName() + " " + drug.getProductName());
            orderItem.setUnit(drug.getUnit());

            orderOverview += "|";
            orderOverview += orderItem.getName();

            //计算合计
            totalAmount = totalAmount.add(orderItem.getPrice().multiply(new BigDecimal(orderItem.getQty())));

            if (!orderItemService.save(orderItem)) {
                return Result.error(Constants.CODE_600, "业务异常.无法保存某订单的项目.项目id=" + orderItem.getItemId());
            };
        }

        orderOverview += "|";
        if (orderOverview.length()>128) {
            orderOverview = orderOverview.substring(0,127);
        }
        orderBase.setId(oid);
        orderBase.setOverview(orderOverview);
        orderBase.setAmount(totalAmount);

        //再保存主表的
        if (orderBaseService.saveOrUpdate(orderBase)) {
            return Result.success();
        }else {
            return Result.error(Constants.CODE_600, "业务异常.保存订单主表失败" );
        }


    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {

        OrderBase orderBase = orderBaseService.getById(id);
        if (null == orderBase ){
            return Result.error(Constants.CODE_404, "订单不存在. 订单id=" + id );
        }

        //只能删除未支付或已签收的订单
        if (orderBase.getStatus()==OrderBase.OrderStatus.UN_PAY ||
                orderBase.getStatus()==OrderBase.OrderStatus.SIGN ) {
            ;
        }else {
            return Result.error(Constants.CODE_600, "无效订单状态.只能删除未支付或已签收的订单");
        }


        //先删除项目表的记录
        orderItemService.delByOrderId(id);
        orderBaseService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Long> ids) {
        //先删除项目表的记录
        for (Long id: ids) {
            orderItemService.delByOrderId(id);
        }
        orderBaseService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping
    public Result findAll() {
        return Result.success(orderBaseService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Long id) {
        return Result.success(orderBaseService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "") String userName,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        QueryWrapper<OrderBase> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        if (!"".equals(userName)) {
            queryWrapper.like("user_name", userName);
        }

        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser.getRole().equals(RoleEnum.ROLE_USER.toString())) {
            //普通用户只能查看自己的预约记录
            queryWrapper.eq("user_id", currentUser.getId());
        }

        Object object = orderBaseService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return Result.success(object);
        //return Result.success(orderBaseService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    @GetMapping("/items/{id}")
    public Result findOrderItems(@PathVariable Long id) {
        //查找订单项目列表
        QueryWrapper<OrderItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("seq");
        queryWrapper.eq("order_id", id);

        return Result.success(orderItemService.list(queryWrapper));
    }

    /**
    * 导出接口
    */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        // 从数据库查询出所有的数据
        List<OrderBase> list = orderBaseService.list();
        // 在内存操作，写出到浏览器
        ExcelWriter writer = ExcelUtil.getWriter(true);

        // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
        writer.write(list, true);

        // 设置浏览器响应的格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("OrderBase信息表", "UTF-8");
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
        List<OrderBase> list = reader.readAll(OrderBase.class);

        orderBaseService.saveBatch(list);
        return Result.success();
    }

    @PostMapping("/delivery/{id}")
    public Result delivery(@PathVariable Long id) {
        //发货

        //判断订单是否存在
        OrderBase orderBase = orderBaseService.getById(id);
        if (null == orderBase) {
            return Result.error(Constants.CODE_404, "订单不存在.订单id=" + id);
        }

        //判断订单是否已经支付
        if (orderBase.getStatus() == OrderBase.OrderStatus.PAYED){

        }else {
            return Result.error(Constants.CODE_600, "订单不是已支付状态, 不允许发货.");
        }

        if (orderBaseService.updateDeliveryInfo(id, OrderBase.OrderStatus.DELIVERED, new Date()) >0) {
            return Result.success(orderBaseService.getById(id));
        }else {
            return Result.error(Constants.CODE_600, "业务异常.更新订单发货信息失败" );
        }

    }

    @PostMapping("/sign/{id}")
    public Result sign(@PathVariable Long id) {
        //签收，
        //判断订单是否存在
        OrderBase orderBase = orderBaseService.getById(id);
        if (null == orderBase) {
            return Result.error(Constants.CODE_404, "订单不存在.订单id=" + id);
        }

        //判断订单是否已经支付
        if (orderBase.getStatus() == OrderBase.OrderStatus.DELIVERED){
            ;
        }else {
            return Result.error(Constants.CODE_600, "订单不是已发货状态, 不允许签收.");
        }

        if (orderBaseService.updateSignInfo(id, OrderBase.OrderStatus.SIGN, new Date())>0) {
            return Result.success(orderBaseService.getById(id));
        }else {
            return Result.error(Constants.CODE_600, "业务异常.更新订单签收信息失败" );
        }

    }

    private User getUser() {
        return TokenUtils.getCurrentUser();
    }

}

