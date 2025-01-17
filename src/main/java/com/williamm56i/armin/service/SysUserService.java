package com.williamm56i.armin.service;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;
import java.util.Map;

public interface SysUserService {

    List<Map<String, Object>> getSysUser(String json) throws JsonProcessingException;
}
