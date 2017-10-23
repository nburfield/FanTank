package com.fantank.controller;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fantank.dto.InvestmentDto;
import com.fantank.error.UserNotFoundException;
import com.fantank.model.User;
import com.fantank.model.Investment;
import com.fantank.model.Offering;
import com.fantank.repository.OfferingRepository;
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
	private IFundamericaApiService fundamericaApiService;
	
	@Autowired
	private OfferingRepository offeringRepository;
		
	@GetMapping("/investments")
	public String getInvestments() {
		return "angular/investments";
	}
		
	@GetMapping("/user/funding")
	@ResponseBody
	public Collection<InvestmentDto> getFunding() {
		User userLoggedIn = userService.findByEmail(securityService.findLoggedInUsername());
		if(userLoggedIn == null) {
			throw new UserNotFoundException("User not logged in to access data");
		}
		Collection<InvestmentDto> investments = new ArrayList<InvestmentDto>();
		for(Investment investment : userLoggedIn.getInvestments()) {
			InvestmentDto userInvestmentData = fundamericaApiService.getInvestmentData(investment.getInvestmentId());
			userInvestmentData.setOffering_name(fundamericaApiService.getOfferingDataByUrl(userInvestmentData.getOffering_url()).getName());
			investments.add(userInvestmentData);
		}
		return investments;
	}
	
	@GetMapping("/offerings/{offeringId}")
	@ResponseBody
	private Object getOfferingData(@PathVariable("offeringId") String offeringId) {
		
		Offering offering = offeringRepository.findByOfferingId(offeringId);
		if(offering == null) {
			offering = offeringRepository.findById(offeringId.replace("-", ""));
		}
		
		if(offering == null) {
			throw new RuntimeException("No offering found");
		}
		return fundamericaApiService.getOfferingData(offering.getOfferingId());
	}
}
