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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();

	}
	
	@PostMapping("/investments/webhook")
	@ResponseBody
	private GenericResponse fundamericaWebhook(HttpServletRequest request) {
		Logger logger = LoggerFactory.getLogger(FundamericaWebhookController.class);

		
		FundamericaWebhookDto fundamericaWebhook;
		try {
			String result = getStringFromInputStream(request.getInputStream());
			String dataJson = StringUtils.substringAfter(result, "=");
			ObjectMapper mapper = new ObjectMapper();
			fundamericaWebhook = mapper.readValue(dataJson, FundamericaWebhookDto.class);
			logger.error("What i got \n" + result);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
		
		System.out.println(fundamericaWebhook.getData());
		logger.error(fundamericaWebhook.getSignature());
		logger.error(fundamericaWebhook.getWebhook_id());
		logger.error(fundamericaWebhook.getObject());
		logger.error(environment.getProperty("fantank.webhook.key"));
		
		// Prevent duplicate webhook runs
		if(webhookRepository.findById(fundamericaWebhook.getWebhook_id()) != null) {
			throw new RuntimeException("Webhook ID already exists");
		}
		
		// Validate webhook
		String md5Hex = DigestUtils.md5Hex(fundamericaWebhook.getWebhook_id() + environment.getProperty("fantank.webhook.key"));
		logger.error(md5Hex);

		if(!md5Hex.equals(fundamericaWebhook.getSignature())) {
			throw new RuntimeException("Webhook Signature not correct");
		}

		if(fundamericaWebhook.getObject().equals("investment") && fundamericaWebhook.getAction().equals("create")) {
			InvestmentDto investmentData = fundamericaApiService.getInvestmentData(fundamericaWebhook.getId());

			if(investmentData.getData().getClientData() == null) {
				throw new RuntimeException("No investment Data on user is available.");
			}
			
			User userLoggedIn = userService.findByEmail(investmentData.getData().getClientData().getEmail());
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
