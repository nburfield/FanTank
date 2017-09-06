package com.fantank.dto;

public class InvestmentDataDto {

	private String url;
	private ClientDataDto client_data;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public ClientDataDto getClient_data() {
		return client_data;
	}

	public void setClient_data(ClientDataDto client_data) {
		this.client_data = client_data;
	}
}
