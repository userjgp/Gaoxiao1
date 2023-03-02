package com.atguigu.gulimall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: nb纪广鹏
 **/

@Data
public class FareVo {

    private MemberAddressVo address;

    private BigDecimal fare;

}


