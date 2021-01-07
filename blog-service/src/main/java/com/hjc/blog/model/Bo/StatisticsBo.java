package com.hjc.blog.model.Bo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 后台统计对象
 */
@Data
@ToString
public class StatisticsBo implements Serializable {

    private Long articles;
    private Long comments;
    private Long links;
    private Long attachs;
}
