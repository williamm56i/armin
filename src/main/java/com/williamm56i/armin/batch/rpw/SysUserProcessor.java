package com.williamm56i.armin.batch.rpw;

import com.williamm56i.armin.persistence.vo.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SysUserProcessor implements ItemProcessor<SysUser, SysUser> {

    @Override
    public SysUser process(SysUser item) throws Exception {
        // TODO
        return item;
    }
}
