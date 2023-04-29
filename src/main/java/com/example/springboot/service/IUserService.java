package com.example.springboot.service;

import com.example.springboot.controller.dto.UserDTO;
import com.example.springboot.controller.dto.UserPasswordDTO;
import com.example.springboot.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author
 */
public interface IUserService extends IService<User> {

    UserDTO login(UserDTO userDTO);

    User register(UserDTO userDTO);

    void updatePassword(UserPasswordDTO userPasswordDTO);

    /**
     * 增加用户的余额
     * @param userId
     * @param amount
     */
    int incBalance(int userId, BigDecimal amount);

    /**
     * 减少用户的余额
     * @param userId
     * @param amount
     */
    int decBalance(int userId, BigDecimal amount);
}
