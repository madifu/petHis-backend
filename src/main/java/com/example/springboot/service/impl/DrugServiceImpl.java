package com.example.springboot.service.impl;

import com.example.springboot.entity.Drug;
import com.example.springboot.mapper.DrugMapper;
import com.example.springboot.service.IDrugService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 
 * @since 2023-04-21
 */
@Service
public class DrugServiceImpl extends ServiceImpl<DrugMapper, Drug> implements IDrugService {

}
