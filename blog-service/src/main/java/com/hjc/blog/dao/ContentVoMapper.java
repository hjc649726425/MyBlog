package com.hjc.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hjc.blog.model.Bo.ArchiveBo;
import com.hjc.blog.model.Vo.ContentVo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentVoMapper extends BaseMapper<ContentVo> {

    List<ArchiveBo> findReturnArchiveBo();

    List<ContentVo> findByCatalog(Integer mid);
}