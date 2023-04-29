package com.example.springboot.service.impl;

import com.example.springboot.entity.Diagnosis;
import com.example.springboot.mapper.DiagnosisMapper;
import com.example.springboot.service.IDiagnosisService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 
 * @since 2023-04-24
 */
@Service
public class DiagnosisServiceImpl extends ServiceImpl<DiagnosisMapper, Diagnosis> implements IDiagnosisService {

}
