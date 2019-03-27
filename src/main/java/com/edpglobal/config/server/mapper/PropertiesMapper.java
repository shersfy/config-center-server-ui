package com.edpglobal.config.server.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.edpglobal.config.server.model.Properties;

public interface PropertiesMapper extends BaseMapper<Properties, Long>{

	List<String> getLabels();

	List<String> getProfiles(@Param("label")String label);

	List<String> getApplications(@Param("label")String label, @Param("profile")String profile);
	
}