package com.williamm56i.armin.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.williamm56i.armin.persistence.dao.SysUserDao;
import com.williamm56i.armin.persistence.vo.SysUser;
import com.williamm56i.armin.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    SysUserDao sysUserDao;

    @Override
    public List<Map<String, Object>> getSysUser(String json) throws JsonProcessingException {
        SysUser vo = objectMapper.readValue(json, SysUser.class);
        List<SysUser> sysUserList = sysUserDao.selectByConditions(vo.getAccount(), vo.getUserName(), vo.getEmail()); // from json
        List<Map<String, Object>> list = new ArrayList<>();
        sysUserList.forEach( sysUser -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("account", sysUser.getAccount());
            map.put("userName", sysUser.getUserName());
            map.put("email", sysUser.getEmail());
            map.put("createId", sysUser.getCreateId());
            map.put("createDate", sysUser.getCreateDate());
            map.put("updateId", sysUser.getUpdateId());
            map.put("updateDate", sysUser.getUpdateDate());
            list.add(map);
        });
        return list;
    }
}
