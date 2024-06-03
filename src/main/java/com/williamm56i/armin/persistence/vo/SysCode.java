package com.williamm56i.armin.persistence.vo;

import lombok.Data;

import java.util.Date;

@Data
public class SysCode {

    String sysGroup;

    String sysName;

    String sysValue;

    String memo;

    String createId;

    Date createDate;

    String updateId;

    Date updateDate;
}
