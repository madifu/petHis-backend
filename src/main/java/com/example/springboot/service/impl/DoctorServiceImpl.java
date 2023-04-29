package com.example.springboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.entity.Drug;
import com.example.springboot.entity.User;
import com.example.springboot.mapper.DrugMapper;
import com.example.springboot.mapper.UserMapper;
import com.example.springboot.service.IDoctorService;
import com.example.springboot.service.IDrugService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  医生服务实现类
 * </p>
 *
 * @author 
 * @since 2023-04-21
 */
@Service
public class DoctorServiceImpl extends ServiceImpl<UserMapper, User> implements IDoctorService {

}
