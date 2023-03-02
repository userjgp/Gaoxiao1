package com.atguigu.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: nb纪广鹏
 **/

@Data
public class MergeVo {

    private Long purchaseId;

    private List<Long> items;

}
