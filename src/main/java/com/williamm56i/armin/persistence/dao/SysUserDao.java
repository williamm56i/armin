package com.williamm56i.armin.persistence.dao;

import com.williamm56i.armin.persistence.vo.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysUserDao {

    List<SysUser> selectAll();

    SysUser selectByPrimaryKey(@Param("account") String account);

    List<SysUser> selectByConditions(@Param("account") String account, @Param("userName") String userName, @Param("email") String email);

    int insertSelective(SysUser sysUser);

    int deleteByPrimaryKey(@Param("account") String account);

    int updateByPrimaryKeySelective(SysUser sysUser);
}
