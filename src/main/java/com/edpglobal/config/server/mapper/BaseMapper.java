package com.edpglobal.config.server.mapper;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.gouuse.datahub.commons.beans.BaseEntity;

public interface BaseMapper<T extends BaseEntity, Id extends Serializable> {
	
	int insert(T entity);

	int deleteById(Id id);
	
	int deleteByIds(@Param("ids") String ids);

	int updateById(T entity);
	
	T findById(Id id);
	
	List<T> findByIds(@Param("ids") String ids);

	long findListCount(Map<String, Object> map);
	
	List<T> findList(Map<String, Object> map);
	
}
