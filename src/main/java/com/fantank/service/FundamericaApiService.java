package com.fantank.service;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fantank.dto.InvestmentDto;
import com.fantank.dto.OfferingDto;

@Service
public class FundamericaApiService implements IFundamericaApiService {
	
	@Autowired 
	Environment environment;
	
	private final String baseUrl = "https://sandbox.fundamerica.com/api/";

	@Override
	public OfferingDto getOfferingData(String offeringId) {
		String url = baseUrl + "offerings/" + offeringId;
		return callRestApi().getForObject(url, OfferingDto.class);
	}
	
	@Override
	public OfferingDto getOfferingDataByUrl(String url) {
		return callRestApi().getForObject(url, OfferingDto.class);
	}
	
	@Override
	public InvestmentDto getInvestmentData(String investmentId) {
		String url = baseUrl + "investments/" + investmentId;
		return callRestApi().getForObject(url, InvestmentDto.class);
	}
	
	private RestTemplate callRestApi() {	    
	    //1. Set credentials
	    Credentials credentials = new UsernamePasswordCredentials(environment.getProperty("fantank.api.key"), "");

	    CredentialsProvider credsProvider = new BasicCredentialsProvider();
	    credsProvider.setCredentials(AuthScope.ANY, credentials);

	    //2. Bind credentialsProvider to httpClient
	    HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
	    httpClientBuilder.setDefaultCredentialsProvider(credsProvider);
	    CloseableHttpClient httpClient = httpClientBuilder.build();

	    HttpComponentsClientHttpRequestFactory factory = new  
	            HttpComponentsClientHttpRequestFactory(httpClient);

	    //3. create restTemplate
	    RestTemplate restTemplate = new RestTemplate();
	    restTemplate.setRequestFactory(factory);
	    return restTemplate;
	    
	    //4. restTemplate execute
		//return restTemplate.getForObject(url, object.class);
	}
}
