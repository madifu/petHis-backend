package com.example.springboot.service.impl;

import com.example.springboot.entity.BalanceLog;
import com.example.springboot.mapper.BalanceLogMapper;
import com.example.springboot.mapper.UserMapper;
import com.example.springboot.service.IBalanceLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 
 * @since 2023-04-24
 */
@Service
public class BalanceLogServiceImpl extends ServiceImpl<BalanceLogMapper, BalanceLog> implements IBalanceLogService {


}
