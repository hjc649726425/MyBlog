package com.hjc.blog.model.Vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 
 */
@Data
@TableName(value = "t_options")
public class OptionVo extends BaseVo implements Serializable {
    /**
     * 配置名称
     */
    @TableId(value = "name",type = IdType.AUTO)//指定自增策略
    private String name;

    /**
     * 配置值
     */
    private String value;

    private String description;

    private static final long serialVersionUID = 1L;

}