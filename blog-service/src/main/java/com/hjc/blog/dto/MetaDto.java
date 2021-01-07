package com.hjc.blog.dto;

import com.hjc.blog.model.Vo.MetaVo;
import lombok.Data;

@Data
public class MetaDto extends MetaVo {

    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
