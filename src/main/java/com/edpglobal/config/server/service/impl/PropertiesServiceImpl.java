package com.edpglobal.config.server.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edpglobal.config.server.mapper.BaseMapper;
import com.edpglobal.config.server.mapper.PropertiesMapper;
import com.edpglobal.config.server.model.Properties;
import com.edpglobal.config.server.service.PropertiesService;

@Service
@Transactional
public class PropertiesServiceImpl extends BaseServiceImpl<Properties, Long> 
	implements PropertiesService {

	@Autowired
	private PropertiesMapper mapper;
	
	@Override
	public BaseMapper<Properties, Long> getMapper() {
		return mapper;
	}

	@Override
	public List<String> getLabels() {
		return mapper.getLabels();
	}

	@Override
	public List<String> getProfiles(String label) {
		return mapper.getProfiles(label);
	}

	@Override
	public List<String> getApplications(String label, String profile) {
		return mapper.getApplications(label, profile);
	}
	
}
