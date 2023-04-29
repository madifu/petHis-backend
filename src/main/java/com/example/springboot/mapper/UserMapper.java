package com.example.springboot.mapper;

import com.example.springboot.controller.dto.UserPasswordDTO;
import com.example.springboot.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author
 */
@Component
public interface UserMapper extends BaseMapper<User> {

    @Update("update sys_user set password = #{newPassword} where id = #{id} and username = #{username} and password = #{password}")
    int updatePassword(UserPasswordDTO userPasswordDTO);

    /**
     * 增加用户的余额
     * @param userId
     * @param amount
     */
    @Update("update sys_user set balance = balance + #{amount} where id = #{userId} ")
    int incBalance(int userId, BigDecimal amount);

    /**
     * 减少用户的余额
     * @param userId
     * @param amount
     */
    @Update("update sys_user set balance = balance - #{amount} where id = #{userId} ")
    int decBalance(int userId, BigDecimal amount);
}
