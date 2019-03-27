package com.edpglobal.config.server.service;

import java.util.List;

import com.edpglobal.config.server.model.Properties;

public interface PropertiesService extends BaseService<Properties, Long> {

	List<String> getLabels();

	List<String> getProfiles(String label);

	List<String> getApplications(String label, String profile);
	
}
