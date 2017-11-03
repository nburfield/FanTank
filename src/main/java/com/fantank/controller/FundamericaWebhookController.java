package com.fantank.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriUtils;

import com.fantank.config.Routes;
import com.fantank.dto.DataDto;
import com.fantank.dto.FundamericaWebhookDto;
import com.fantank.dto.InvestmentDto;
import com.fantank.model.Investment;
import com.fantank.model.Offering;
import com.fantank.model.User;
import com.fantank.model.Webhook;
import com.fantank.repository.InvestmentRepository;
import com.fantank.repository.OfferingRepository;
import com.fantank.repository.WebhookRepository;
import com.fantank.service.GenericResponse;
import com.fantank.service.IFundamericaApiService;
import com.fantank.service.IUserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class FundamericaWebhookController {
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private InvestmentRepository userInvestmentsRepository;

	@Autowired
	private IFundamericaApiService fundamericaApiService;
	
	@Autowired
	private OfferingRepository offeringRepository;
	
	@Autowired 
	private Environment environment;
	
	@Autowired
	private WebhookRepository webhookRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(FundamericaWebhookController.class);
	
	// convert InputStream to String
	private static String getStringFromInputStream(InputStream is) {

		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			logger.error("IOException: " + e.getMessage());
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					logger.error("IOException: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}

		return sb.toString();
	}
	
	@RequestMapping(value = Routes.WEBHOOK, method = RequestMethod.POST, consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
	public @ResponseBody GenericResponse fundamericaWebhook( DataDto data) {

//		FundamericaWebhookDto fundamericaWebhook;
//		try {
//			String hookData = getStringFromInputStream(request.getInputStream());
//			logger.error("Recieved Webhook: " + hookData);
//			String result = UriUtils.decode(hookData, "UTF-8");
//			String dataJson = StringUtils.substringAfter(result, "=");
//			ObjectMapper mapper = new ObjectMapper();
//			logger.error("Recieved Webhook Decode: " + result);
//			logger.error("Recieved Webhook JSON: " + dataJson);
//			fundamericaWebhook = mapper.readValue(dataJson, FundamericaWebhookDto.class);
//		} catch (IOException e) {
//			throw new RuntimeException(e.getMessage());
//		}
		
		FundamericaWebhookDto fundamericaWebhook = data.getData();
		logger.error("Data: " + data);
		logger.error("Recieved Webhook: " + fundamericaWebhook.getId());
		logger.error("Recieved Webhook Decode: " + fundamericaWebhook.getWebhook_id());
		logger.error("Recieved Webhook JSON: " + fundamericaWebhook.getAction());
		
		
		// Prevent duplicate webhook runs
		if(webhookRepository.findById(fundamericaWebhook.getWebhook_id()) != null) {
			throw new RuntimeException("Webhook ID already exists");
		}
		
		// Validate webhook
		String md5Hex = DigestUtils.md5Hex(fundamericaWebhook.getWebhook_id() + environment.getProperty("fantank.webhook.key"));
		if(!md5Hex.equals(fundamericaWebhook.getSignature())) {
			throw new RuntimeException("Webhook Signature not correct");
		}

		if(fundamericaWebhook.getObject().equals("investment") && fundamericaWebhook.getAction().equals("create")) {
			InvestmentDto investmentData = fundamericaApiService.getInvestmentData(fundamericaWebhook.getId());
			
			if(investmentData.getData().getClient_data() == null) {
				throw new RuntimeException("No investment Data on user is available.");
			}
			
			User userLoggedIn = userService.findByEmail(investmentData.getData().getClient_data().getEmail());
			Offering offering = offeringRepository.findByOfferingId(
					fundamericaApiService.getOfferingDataByUrl(investmentData.getOffering_url()).getId());
			Investment userInvestments = new Investment(investmentData.getId(), userLoggedIn, offering);
			userInvestmentsRepository.save(userInvestments);
		}
		
		Webhook webhook = new Webhook(fundamericaWebhook.getWebhook_id());
		webhookRepository.save(webhook);
		return new GenericResponse("success");
	}
}
