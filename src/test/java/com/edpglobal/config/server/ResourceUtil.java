package com.edpglobal.config.server;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

public class ResourceUtil {
	
	private static ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
	
	public static Resource[] getResources(String locationPattern) throws IOException {
		return resolver.getResources(locationPattern);
	}
	
	public static Resource getResource(String location) throws IOException {
		return resolver.getResource(location);
	}

}
