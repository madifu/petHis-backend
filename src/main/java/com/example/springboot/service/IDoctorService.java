package com.example.springboot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.springboot.entity.Drug;
import com.example.springboot.entity.User;
import org.springframework.stereotype.Component;

/**
 * <p>
 *  医生服务类
 * </p>
 *
 * @author 
 * @since 2023-04-21
 */
@Component
public interface IDoctorService extends IService<User> {

}
