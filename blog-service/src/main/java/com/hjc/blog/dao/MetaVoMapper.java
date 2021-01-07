package com.hjc.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hjc.blog.dto.MetaDto;
import com.hjc.blog.model.Vo.MetaVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface MetaVoMapper extends BaseMapper<MetaVo> {

    MetaDto selectDtoByNameAndType(@Param("name") String name, @Param("type") String type);

    Integer countWithSql(Integer mid);

    List<MetaDto> selectFromSql(Map<String,Object> paraMap);
}