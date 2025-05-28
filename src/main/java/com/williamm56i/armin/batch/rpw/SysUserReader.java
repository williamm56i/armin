package com.williamm56i.armin.batch.rpw;

import com.williamm56i.armin.persistence.dao.SysUserDao;
import com.williamm56i.armin.persistence.vo.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class SysUserReader {

    @Autowired
    SysUserDao sysUserDao;

    public ItemReader<SysUser> read() {
        List<SysUser> list = sysUserDao.selectAll();
        return new ListItemReader<>(list);
    }
}
