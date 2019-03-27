package com.edpglobal.config.server.service;

import java.io.Serializable;
import java.util.List;

import com.edpglobal.config.server.config.I18nCodes;
import com.gouuse.datahub.commons.beans.BaseEntity;
import com.gouuse.datahub.commons.beans.Page;
import com.gouuse.datahub.commons.beans.Result.ResultCode;

public interface BaseService<T extends BaseEntity, Id extends Serializable> extends I18nCodes{
	
	int SUCESS = ResultCode.SUCESS;
	int FAIL   = ResultCode.FAIL;
	int NOR    = 0;
	int DEL    = 1;
	int TMP    = -1;
	
	int deleteById(Id id);
	
	int deleteByIds(Long[] ids);

	int insert(T entity);

	T findById(Id id);
	
	List<T> findByIds(List<Long> ids);

	int updateById(T entity);
	
	long findListCount(T where);
	
	List<T> findList(T where);

	Page<T> findPage(T where, int pageNo, int pageSize);
	
	Page<T> findPage(T where, String tenantIds, int pageNo, int pageSize);
	
	Page<T> findPage(T where, String tenantIds, String keyword, int pageNo, int pageSize);
	
	Page<T> findPageByRoles(T where, String roleIds, int pageNo, int pageSize);
	
}
