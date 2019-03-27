package com.edpglobal.config.server.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.edpglobal.config.server.mapper.BaseMapper;
import com.edpglobal.config.server.service.BaseService;
import com.gouuse.datahub.commons.beans.BaseEntity;
import com.gouuse.datahub.commons.beans.Page;

public abstract class BaseServiceImpl<T extends BaseEntity, Id extends Serializable> 
	implements BaseService<T, Id> {
	
	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	public abstract BaseMapper<T, Id> getMapper();
	
	@Override
	public int deleteById(Id id) {
		return getMapper().deleteById(id);
	}
	
	@Override
	public int deleteByIds(Long[] ids) {
		String strIds = "0";
		if (ids!=null && ids.length!=0) {
			strIds = StringUtils.join(ids, ", ");
		}
		int cnt = getMapper().deleteByIds(strIds);
		LOGGER.info("delete {} records, ids '{}'", cnt, strIds);
		return cnt;
	}

	@Override
	public int insert(T entity) {
		if(entity.getCreateTime()==null) {
			entity.setCreateTime(new Date());
		}
		if(entity.getUpdateTime()==null) {
			entity.setUpdateTime(entity.getCreateTime());
		}
		return getMapper().insert(entity);
	}

	@Override
	public T findById(Id id) {
		return getMapper().findById(id);
	}
	
	@Override
	public List<T> findByIds(List<Long> ids) {
		if (ids==null||ids.isEmpty()) {
			ids = new ArrayList<>();
			ids.add(0L);
		}
		return getMapper().findByIds(StringUtils.join(ids, ", "));
	}

	@Override
	public int updateById(T entity) {
		if(entity.getUpdateTime()==null) {
			entity.setUpdateTime(new Date());
		}
		return getMapper().updateById(entity);
	}

	@Override
	public long findListCount(T where) {
		return getMapper().findListCount(parseMap(where));
	}
	
	@Override
	public List<T> findList(T where) {
		return getMapper().findList(parseMap(where));
	}

	@Override
	public Page<T> findPage(T where, int pageNo, int pageSize) {
		Map<String, Object> map = parseMap(where);
		Page<T> page = new Page<>(pageNo, pageSize);
		
		map.put("startIndex", page.getStartIndex());
		map.put("pageSize", page.getPageSize());
		
    	
    	long count   = getMapper().findListCount(map);
		List<T> list = getMapper().findList(map);
		
		page.setSumRow(count);
		page.setData(list);
    	return page;
    	
	}
	
	@Override
	public Page<T> findPage(T where, String tenantIds, int pageNo, int pageSize) {
		Map<String, Object> map = parseMap(where);
		Page<T> page = new Page<>(pageNo, pageSize);
		
		map.put("startIndex", page.getStartIndex());
		map.put("pageSize", page.getPageSize());
		map.put("tenantIds", StringUtils.isBlank(tenantIds)?null:tenantIds);
		
		
		long count   = getMapper().findListCount(map);
		List<T> list = getMapper().findList(map);
		
		page.setSumRow(count);
		page.setData(list);
		return page;
		
	}
	
	@Override
	public Page<T> findPage(T where, String tenantIds, String keyword, int pageNo, int pageSize) {
		Map<String, Object> map = parseMap(where);
		Page<T> page = new Page<>(pageNo, pageSize);
		
		map.put("startIndex", page.getStartIndex());
		map.put("pageSize", page.getPageSize());
		map.put("tenantIds", StringUtils.isBlank(tenantIds)?null:tenantIds);
		map.put("keyword", StringUtils.isBlank(keyword)?null:keyword);
		
		long count   = getMapper().findListCount(map);
		List<T> list = getMapper().findList(map);
		
		page.setSumRow(count);
		page.setData(list);
		return page;
		
	}
	
	@Override
	public Page<T> findPageByRoles(T where, String roleIds, int pageNo, int pageSize) {
		Map<String, Object> map = parseMap(where);
		Page<T> page = new Page<>(pageNo, pageSize);
		
		map.put("startIndex", page.getStartIndex());
		map.put("pageSize", page.getPageSize());
		map.put("roleIds", StringUtils.isBlank(roleIds)?null:roleIds);
		
		
		long count   = getMapper().findListCount(map);
		List<T> list = getMapper().findList(map);
		
		page.setSumRow(count);
		page.setData(list);
		return page;
	}

	
	protected Map<String, Object> parseMap(T obj){
		Map<String, Object> map = new HashMap<>();
		map.putAll(JSON.parseObject(JSON.toJSONString(obj)));
		map.put("createTime", obj.getCreateTime());
		map.put("updateTime", obj.getUpdateTime());
		map.put("solrStartTime", obj.getSolrStartTime());
		map.put("solrEndTime", obj.getSolrEndTime());
		map.put("sort", obj.getSort());
		map.put("order", obj.getOrder());
		
		return map;
	}
	
	public <Vo extends T> Vo poToVo(T po, Class<Vo> vclass) {
		if (po==null || vclass==null) {
			return null;
		}
		
		Vo vo = null;
		try {
			vo = vclass.newInstance();
			BeanUtils.copyProperties(vo, po);
		} catch (Exception e) {
			LOGGER.error("", e);
		}
		
		return vo;
	}
	
}
