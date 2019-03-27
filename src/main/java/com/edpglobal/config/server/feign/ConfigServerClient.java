package com.edpglobal.config.server.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(name = ConfigServerClient.serviceId)
public interface ConfigServerClient {

    String serviceId = "edpglobal-config-server";

    @RequestMapping(method = RequestMethod.POST, value = "/actuator/bus-refresh")
    @ResponseBody
    Object refreshByBus();
    
    @RequestMapping(method = RequestMethod.POST, value = "/actuator/bus-refresh/{destination}")
    @ResponseBody
    Object refreshByBus(@PathVariable("destination")String destination);
    
    @RequestMapping(method = RequestMethod.GET, value = "/config/server/profile")
    @ResponseBody
    Object getActiveProfile();
    
    @RequestMapping(method = RequestMethod.GET, value = "/{label}/{application}-{profile}.yml")
    String getYaml(@PathVariable("label")String label,
    		@PathVariable("profile")String profile,
    		@PathVariable("application")String application);
    
    @RequestMapping(method = RequestMethod.GET, value = "/{label}/{application}-{profile}.json")
    String getJson(@PathVariable("label")String label,
    		@PathVariable("profile")String profile,
    		@PathVariable("application")String application);
    
    @RequestMapping(method = RequestMethod.GET, value = "/{label}/{application}-{profile}.properties")
    String getProperties(@PathVariable("label")String label,
    		@PathVariable("profile")String profile,
    		@PathVariable("application")String application);
}
