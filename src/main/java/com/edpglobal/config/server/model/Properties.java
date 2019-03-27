package com.edpglobal.config.server.model;

import com.gouuse.datahub.commons.beans.BaseEntity;

public class Properties extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/** 主键 **/
    private Long id;

    /** 标签(或Git分支) **/
    private String label;

    /** 环境标签 **/
    private String profile;

    /** 应用名称 **/
    private String application;

    /** 配置项键 **/
    private String key;

    /** 配置项说明 **/
    private String comment;

    /** 配置项值 **/
    private String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}