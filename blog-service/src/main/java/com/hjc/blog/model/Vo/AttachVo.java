package com.hjc.blog.model.Vo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 
 */
@Data
@TableName(value = "t_attach")
public class AttachVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)//指定自增策略
    private Integer id;

    private String fname;

    //更新策略
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    private String ftype;

    private String fkey;

    private Integer authorId;

}