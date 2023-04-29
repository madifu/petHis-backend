package com.example.springboot.service.impl;

import com.example.springboot.entity.PetKind;
import com.example.springboot.mapper.PetKindMapper;
import com.example.springboot.service.IPetKindService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 
 * @since 2023-04-23
 */
@Service
public class PetKindServiceImpl extends ServiceImpl<PetKindMapper, PetKind> implements IPetKindService {

}
