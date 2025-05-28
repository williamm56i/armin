package com.williamm56i.armin.batch.rpw;

import com.williamm56i.armin.persistence.dao.SysUserDao;
import com.williamm56i.armin.persistence.vo.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SysUserWriter implements ItemWriter<SysUser> {

    @Autowired
    SysUserDao sysUserDao;

    @Override
    public void write(Chunk<? extends SysUser> chunk) throws Exception {
        for (SysUser sysUser: chunk) {
            sysUserDao.updateByPrimaryKeySelective(sysUser);
        }
        log.info("完成 {} 筆", chunk.size());
    }
}
