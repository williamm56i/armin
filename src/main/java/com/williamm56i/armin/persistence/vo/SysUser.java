package com.williamm56i.armin.persistence.vo;

import lombok.Data;

import java.util.Date;

@Data
public class SysUser {

    String account;

    String userName;

    String email;

    String createId;

    Date createDate;

    String updateId;

    Date updateDate;
}
