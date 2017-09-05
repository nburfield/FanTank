package com.fantank.controller;

import javax.validation.Valid;

import org.apache.commons.codec.digest.DigestUtils;
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
	
	@PostMapping("/investments/webhook")
	@ResponseBody
	private GenericResponse fundamericaWebhook(@Valid FundamericaWebhookDto fundamericaWebhook) {
		// Prevent duplicate webhook runs
		if(webhookRepository.findById(fundamericaWebhook.getId()) != null) {
			throw new RuntimeException("Webhook ID already exists");
		}
		
		// Validate webhook
		String md5Hex = DigestUtils.md5Hex(fundamericaWebhook.getWebhook_id() + environment.getProperty("fantank.webhook.key"));
		if(!md5Hex.equals(fundamericaWebhook.getSignature())) {
			throw new RuntimeException("Webhook Signature not correct");
		}

		if(fundamericaWebhook.getObject() == "investment" && fundamericaWebhook.getAction() == "create") {
			InvestmentDto investmentData = fundamericaApiService.getInvestmentData(fundamericaWebhook.getId());
			User userLoggedIn = userService.findByEmail(investmentData.getData().getClientData().getEmail());
			Offering offering = offeringRepository.findByOfferingId(
					fundamericaApiService.getOfferingDataByUrl(investmentData.getOffering_url()).getId());
			Investment userInvestments = new Investment(investmentData.getId(), userLoggedIn, offering);
			userInvestmentsRepository.save(userInvestments);
		}
		
		Webhook webhook = new Webhook(fundamericaWebhook.getId());
		webhookRepository.save(webhook);
		return new GenericResponse("success");
	}
}
