package com.edpglobal.config.server.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.edpglobal.config.server.feign.ConfigServerClient;
import com.edpglobal.config.server.model.Properties;
import com.edpglobal.config.server.service.PropertiesService;
import com.gouuse.datahub.commons.beans.Result;
import com.gouuse.datahub.commons.constant.Const;
import com.gouuse.datahub.commons.exception.DatahubException;
import com.gouuse.datahub.i18n.I18nModel;

@RestController
@RequestMapping("/config/server")
public class ConfigServerController extends BaseController{
	
	@Autowired
	private ConfigServerClient client;
	
	@Autowired
	private PropertiesService service;

	@GetMapping("/repo")
	public Object getActiveProfile() {
		try {
			return client.getActiveProfile();
		} catch (Exception e) {
			LOGGER.error("", e);
			String msg = DatahubException.getRootCauseMsg(e);
			Result res = new Result();
			res.setCode(FAIL);
			res.setMsg(msg);
			return res;
		}
	}
	
	@GetMapping("/labels")
	public Result getLabels() {
		Result res = new Result();
		res.setModel(service.getLabels());
		return res;
	}
	
	@GetMapping("/profiles")
	public Result getProfiles(@RequestParam(required=true)String label) {
		Result res = new Result();
		res.setModel(service.getProfiles(label));
		return res;
	}
	
	@GetMapping("/applications")
	public Result getApplications(@RequestParam(required=true)String label,
			@RequestParam(required=true)String profile) {
		Result res = new Result();
		res.setModel(service.getApplications(label, profile));
		return res;
	}
	
	@GetMapping("/properties")
	public Result getProperties(@RequestParam(required=true)String label,
			@RequestParam(required=true)String profile,
			@RequestParam(required=true)String application, String keyword) {
		Result res = new Result();
		
		String repo = null;
		try {
			String text = JSON.toJSONString(client.getActiveProfile());
			Result rres = JSON.parseObject(text, Result.class);
			if (rres.getCode()!=SUCESS) {
				return res;
			}
			JSONObject json = JSON.parseObject(rres.getModel().toString());
			repo = json.getJSONArray("active").get(0).toString();
		} catch (Exception e) {
			LOGGER.error("", e);
			res.setCode(FAIL);
			res.setMsg(DatahubException.getRootCauseMsg(e));
			return res;
		}
		
		if (StringUtils.isBlank(repo)) {
			res.setCode(FAIL);
			res.setMsg("server error: 'spring.profiles.active' is blank, "+ConfigServerClient.serviceId);
			return res;
		}
		res.setMsg(repo.toLowerCase());
		if ("jdbc".equalsIgnoreCase(repo)) {
			Properties where = new Properties();
			where.setLabel(label);
			where.setProfile(profile);
			where.setApplication(application);
			
			List<Properties> data = service.findList(where);
			data.removeIf(e->{
				if (StringUtils.isNotBlank(keyword)) {
					return !StringUtils.containsIgnoreCase(e.getKey(), keyword) 
							&& !StringUtils.containsIgnoreCase(e.getValue(), keyword)
							&& !StringUtils.containsIgnoreCase(e.getComment(), keyword);
				}
				return false;
			});
			res.setModel(data);
			return res;
		}
		
		
		Reader input = null;
		try {
			
			String data = client.getProperties(label, profile, application);
			data = data==null?"":data;
			input = new StringReader(data);
			java.util.Properties prop = new java.util.Properties();
			prop.load(input);
			
			List<Properties> list = new ArrayList<>();
			prop.forEach((key, value)->{
				Properties info = new Properties();
				info.setLabel(label);
				info.setProfile(profile);
				info.setApplication(application);
				info.setKey(key.toString());
				info.setValue(value.toString());
				info.setCreateTime(new Date(Const.MAX_DATE));
				info.setUpdateTime(info.getCreateTime());
				list.add(info);
			});
			list.sort((o1, o2)->{
				return o1.getKey().compareTo(o2.getKey());
			});
			res.setModel(list);
		} catch (Exception e) {
			LOGGER.error("", e);
			res.setCode(FAIL);
			res.setMsg(DatahubException.getRootCauseMsg(e));
			return res;
		} finally {
			IOUtils.closeQuietly(input);
		}
		
		return res;
	}
	
	@GetMapping("/download")
	public void download(@RequestParam(required=true)String label,
			@RequestParam(required=true)String profile,
			@RequestParam(required=true)String application) {
		OutputStream output = null;
		try {
			HttpServletResponse response = getResponse();
			output = response.getOutputStream();
			
			String data = client.getYaml(label, profile, application);
			
			String filename = String.format("application-%s.yml", profile);
			response.setHeader("content-disposition", "attachment;filename="+filename);
			IOUtils.write(data, output);
		} catch (Exception e) {
			LOGGER.error("", e);
			try {
				IOUtils.write(DatahubException.getRootCauseMsg(e), output);
			} catch (Exception e2) {
			}
		} finally {
			IOUtils.closeQuietly(output);
		}
	}
	
	@GetMapping("/{label}/{application}-{profile}.{ext}")
	public void webscan(@PathVariable(required=true)String label,
			@PathVariable(required=true)String application,
			@PathVariable(required=true)String profile,
			@PathVariable(required=true)String ext) {
		OutputStream output = null;
		try {
			HttpServletResponse response = getResponse();
			response.setCharacterEncoding("UTF-8");
			response.setContentType(ContentType.TEXT_PLAIN.getMimeType());
			output = response.getOutputStream();
			
			String data = "";
			switch (ext.toLowerCase()) {
			case "yml":
				data = client.getYaml(label, profile, application);
				break;
			case "json":
				response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
				data = client.getJson(label, profile, application);
				break;
			case "properties":
				data = client.getProperties(label, profile, application);
				break;
			default:
				break;
			}
			IOUtils.write(data, output);
		} catch (Exception e) {
			LOGGER.error("", e);
			try {
				IOUtils.write(DatahubException.getRootCauseMsg(e), output);
			} catch (Exception e2) {
			}
		} finally {
			IOUtils.closeQuietly(output);
		}
	}
	
	@PostMapping("/upload")
	public Result upload(@RequestParam(required=true)String label,
			@RequestParam(required=true)String profile,
			@RequestParam(required=true)String application,
			@RequestParam(required=true)MultipartFile file) {
		Result res = new Result();
		
		String data = "[]";
		try {
			
			String ext = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();
			if (!ext.equals("yml") && !ext.equals("properties")) {
				res.setCode(FAIL);
				res.setModel(new I18nModel(MSGC000010, "file", "*.yml | *.properties"));
				return res;
			}
			
			switch (ext) {
			case "yml":
				data = uploadYaml(label, profile, application, file);
				break;
			case "properties":
				data = uploadProperties(label, profile, application, file);
				break;
			}
			
		} catch (Exception e) {
			res.setCode(FAIL);
			res.setModel(new I18nModel(MSGE000001));
			return res;
		}
		
		return save(data);
	}

	@SuppressWarnings("unchecked")
	protected String uploadYaml(String label, String profile, String application, 
			MultipartFile file) throws IOException {
		
		List<Properties> data = new ArrayList<>();
		Map<String, Object> kvs = new HashMap<>();
		
		InputStream input = null;
		try {
			input = file.getInputStream();
			YamlPropertySourceLoader yaml = new YamlPropertySourceLoader();
			Resource resource = new InputStreamResource(file.getInputStream(), null);
			List<PropertySource<?>> list = yaml.load(application, resource);
			if (!list.isEmpty()) {
				PropertySource<?> prop = list.get(0);
				if (prop.getSource() instanceof Map<?, ?>) {
					Map<?, ?> map = (Map<?, ?>) prop.getSource();
					map.forEach((k, v)->{
						String key = k.toString();
						String val = v.toString();
						if (key.contains("[") && key.contains("]")) {
							key = key.substring(0, key.indexOf("["));
						}
						
						Object oldVal = kvs.get(key);
						if (oldVal!=null) {
							List<String> values = null;
							if (oldVal instanceof List) {
								values = (List<String>)(oldVal);
							} else {
								values = new ArrayList<>();
								values.add(oldVal.toString());
							}
							values.add(val);
							kvs.put(key, values);
						} else {
							kvs.put(key, val);
						}
					});
				}
			}
			
			
			kvs.forEach((k, v)->{
				Properties info = new Properties();
				info.setLabel(label);
				info.setProfile(profile);
				info.setApplication(application);
				info.setKey(k);
				if (v instanceof List) {
					List<String> vals = (List<String>)(v);
					info.setValue(StringUtils.join(vals, ", "));
				} else {
					info.setValue(v.toString());
				}
				data.add(info);
			});
			
		} finally {
			IOUtils.closeQuietly(input);
		}
		
		return JSON.toJSONString(data);
	}
	
	protected String uploadProperties(String label, String profile, String application, 
			MultipartFile file) throws IOException {
		List<Properties> data = new ArrayList<>();
		Reader reader = null;
		try {
			java.util.Properties prop = new java.util.Properties();
			reader = new InputStreamReader(file.getInputStream(), "UTF-8");
			prop.load(reader);
			prop.forEach((key, value)->{
				Properties info = new Properties();
				info.setLabel(label);
				info.setProfile(profile);
				info.setApplication(application);
				info.setKey(key.toString());
				info.setValue(value.toString());
				data.add(info);
			});
			data.sort((o1, o2)->{
				return o1.getKey().compareTo(o2.getKey());
			});
			
		} finally {
			IOUtils.closeQuietly(reader);
		}
		
		return JSON.toJSONString(data);
	}

	@PostMapping("/add")
	public Result addApplication(@RequestParam(required=true)String label,
			@RequestParam(required=true)String profile,
			@RequestParam(required=true)String application) {
		
		Result res = new Result();
		Properties info = new Properties();
		info.setLabel(label);
		info.setProfile(profile);
		info.setApplication(application);
		if(service.findListCount(info)>0) {
			res.setCode(FAIL);
			res.setModel(new I18nModel(MSGC000017, 
					StringUtils.join(Arrays.asList(label, profile, application), ", ")));
			return res;
		}
		
		info.setKey("server.port");
		info.setValue("8080");
		
		res.setModel(service.insert(info)==1);
		return res;
	}
	
	@PostMapping("/save")
	public Result save(@RequestParam(required=true)String data) {
		Result res = new Result();
		int insert = 0;
		int update = 0;
		int error  = 0;
		List<String> errorMsg = new ArrayList<>();
		List<Properties> errorData = new ArrayList<>();
		Properties where = new Properties();
		try {
			List<Properties> list = JSON.parseArray(data, Properties.class);
			for (Properties info :list) {
				
				if (StringUtils.isBlank(info.getLabel())) {
					error++;
					errorMsg.add(getI18nMsg(MSGC000003, "label"));
					errorData.add(info);
					continue;
				}
				
				if (StringUtils.isBlank(info.getProfile())) {
					error++;
					errorMsg.add(getI18nMsg(MSGC000003, "profile"));
					errorData.add(info);
					continue;
				}
				
				if (StringUtils.isBlank(info.getApplication())) {
					error++;
					errorMsg.add(getI18nMsg(MSGC000003, "application"));
					errorData.add(info);
					continue;
				}
				
				if (StringUtils.isBlank(info.getKey())) {
					error++;
					errorMsg.add(getI18nMsg(MSGC000003, "key"));
					errorData.add(info);
					continue;
				}
				
				where.setLabel(info.getLabel());
				where.setProfile(info.getProfile());
				where.setApplication(info.getApplication());
				where.setKey(info.getKey());
				
				List<Properties> exist = service.findList(where);
				if (exist.isEmpty()) {
					update = info.getId()!=null? (update+service.updateById(info)):update;
					insert = info.getId()==null? (insert+service.insert(info)):insert;
					continue;
				}

				Properties old = exist.get(0);
				if (info.getId()!=null && !old.getId().equals(info.getId())) {
					// 已存在
					error++;
					errorMsg.add(getI18nMsg(MSGC000017, StringUtils.join(Arrays.asList(info.getLabel(), 
							info.getProfile(), info.getApplication()), ", ")));
					errorData.add(info);
					continue;
				}
				
				if (info.getId()==null) {
					info.setId(old.getId());
				}
				update += service.updateById(info);
				
			}
		} catch (Exception e) {
			res.setCode(FAIL);
			res.setModel(new I18nModel(MSGC000007, "data", data));
			return res;
		} finally {
			client.refreshByBus();
		}
		
		JSONObject model = new JSONObject();
		model.put("insert", insert);
		model.put("update", update);
		model.put("error", error);
		model.put("errorMsg", errorMsg);
		model.put("errorData", errorData);
		res.setModel(model);
		return res;
	}
	
	@PostMapping("/delete")
	public Result delete(@RequestParam(required=true)Long[] ids) {
		Result res = new Result();
		res.setModel(service.deleteByIds(ids));
		return res;
	}
	
	@PostMapping("/refresh")
	public Result refresh(String destination) {
		Result res = new Result();
		
		try {
			if (StringUtils.isBlank(destination)) {
				res.setModel(client.refreshByBus());
				return res;
			}
			
			res.setModel(client.refreshByBus(destination));
		} catch (Exception e) {
			LOGGER.error("", e);
			String msg = DatahubException.getRootCauseMsg(e);
			res.setCode(FAIL);
			res.setMsg(msg);
		}
		
		return res;
	}
}
