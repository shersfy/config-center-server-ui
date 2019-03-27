package com.edpglobal.config.server;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;

import com.gouuse.datahub.commons.constant.Const;
import com.gouuse.datahub.commons.utils.DateUtil;

public class Tests {
	
	@Test
	public void test01() {
		System.out.println(DateUtil.format(new Date(Const.MIN_DATE), Const.yyyyMMddHHmmssSSS));
	}
	
	@Test
	public void test02() throws IOException {
		Resource resource = ResourceUtil.getResource("classpath:/application.yml");
		YamlPropertySourceLoader yaml = new YamlPropertySourceLoader();
		List<PropertySource<?>> list = yaml.load("application.yml", resource);
		list.forEach(prop->{
			if (prop.getSource() instanceof Map<?, ?>) {
				Map<?, ?> map = (Map<?, ?>) prop.getSource();
				System.out.println(map.keySet());
			}
		});
	}

}
