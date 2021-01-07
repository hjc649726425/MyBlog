package com.hjc.blog.model.Bo;

import com.hjc.blog.model.Vo.ContentVo;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
public class ArchiveBo implements Serializable {

    private String date;
    private String count;
    private List<ContentVo> articles;
}
