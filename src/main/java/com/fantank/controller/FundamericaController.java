package com.fantank.controller;

import java.util.ArrayList;
import java.util.Collection;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fantank.dto.InvestmentDto;
import com.fantank.dto.UserInvestmentsDto;
import com.fantank.model.User;
import com.fantank.model.Investment;
import com.fantank.model.Offering;
import com.fantank.repository.InvestmentRepository;
import com.fantank.repository.OfferingRepository;
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
		
	@GetMapping("/investments")
	public String getInvestments() {
		return "angular/investments";
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
}
