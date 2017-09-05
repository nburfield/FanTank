package com.fantank.dto;

import java.util.Set;

import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FundamericaWebhookDto {
	
	@Size(min = 1, max=255)
	private String object;
	
	@Size(min = 1, max=255)
	private String id;
	
	@Size(min = 1, max=255)
	private String url;
	
	@Size(min = 1, max=255)
	private String webhook_id;
	
	@Size(min = 1, max=255)
	private String signature;
	
	@Size(min = 1, max=255)
	private String action;
	
	private Object data;
	
	private Set<String> changes;
	
	public String getObject() {
		return object;
	}
	
	public void setObject(String object) {
		this.object = object;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getWebhook_id() {
		return webhook_id;
	}
	
	public void setWebhook_id(String webhook_id) {
		this.webhook_id = webhook_id;
	}
	
	public String getSignature() {
		return signature;
	}
	
	public void setSignature(String signature) {
		this.signature = signature;
	}
	
	public String getAction() {
		return action;
	}
	
	public void setAction(String action) {
		this.action = action;
	}
	
	public Set<String> getChanges() {
		return changes;
	}
	
	public void setChanges(Set<String> changes) {
		this.changes = changes;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
