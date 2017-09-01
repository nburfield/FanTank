package com.fantank.controller;

import java.util.ArrayList;
import java.util.Collection;

import javax.validation.Valid;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fantank.dto.FundamericaWebhookDto;
import com.fantank.dto.InvestmentDto;
import com.fantank.dto.UserInvestmentsDto;
import com.fantank.model.User;
import com.fantank.model.Webhook;
import com.fantank.model.Investment;
import com.fantank.model.Offering;
import com.fantank.repository.InvestmentRepository;
import com.fantank.repository.OfferingRepository;
import com.fantank.repository.WebhookRepository;
import com.fantank.service.GenericResponse;
import com.fantank.service.IFundamericaApiService;
import com.fantank.service.ISecurityService;
import com.fantank.service.IUserService;

@Controller
public class FundamericaController {

	@Autowired
	private IUserService userService;
	
	@Autowired
	private ISecurityService securityService;
	
	@Autowired
	private InvestmentRepository userInvestmentsRepository;

	@Autowired
	private IFundamericaApiService fundamericaApiService;
	
	@Autowired
	private OfferingRepository offeringRepository;
	
	@Autowired
	private WebhookRepository webhookRepository;
	
	@Autowired 
	Environment environment;
		
	@GetMapping("/investments")
	public String getInvestments() {
		return "investments";
	}
		
	@GetMapping("/user/funding")
	@ResponseBody
	public Collection<InvestmentDto> getFunding() {
		User userLoggedIn = userService.findByEmail(securityService.findLoggedInUsername());
		Collection<InvestmentDto> investments = new ArrayList<InvestmentDto>();
		for(Investment investment : userLoggedIn.getInvestments()) {
			investments.add(fundamericaApiService.getInvestmentData(investment.getInvestmentId()));
		}
		return investments;
	}
	
	@PostMapping("/user/investment")
	@ResponseBody
	private GenericResponse setInvestmentOnUser(@Valid UserInvestmentsDto userInvestmentDto) {
		User userLoggedIn = userService.findByEmail(securityService.findLoggedInUsername());
		
		System.out.println(userInvestmentDto.getInvestmentId() + " " + userInvestmentDto.getUserId());
		
		if(userLoggedIn.getId() != userInvestmentDto.getUserId()) {
			throw new RuntimeException("User data not equal");
		}
		
		Investment userInvestments = new Investment(userInvestmentDto.getInvestmentId(), userLoggedIn);
		userInvestmentsRepository.save(userInvestments);
		
		return new GenericResponse("success");
	}
	
	@GetMapping("/offerings/{offeringId}")
	@ResponseBody
	private Object getOfferingData(@PathVariable("offeringId") String offeringId) {
		Offering offering = offeringRepository.findById(offeringId.replace("-", ""));
		if(offering == null) {
			throw new RuntimeException("No offering found");
		}
		return fundamericaApiService.getOfferingData(offering.getOfferingId());
	}
	
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

		if(fundamericaWebhook.getObject() == "investment") {
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
