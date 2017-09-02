package com.fantank.service;

import com.fantank.dto.InvestmentDto;
import com.fantank.dto.OfferingDto;

public interface IFundamericaApiService {
	OfferingDto getOfferingData(String offeringId);
	OfferingDto getOfferingDataByUrl(String url);
	InvestmentDto getInvestmentData(String investmentId);
}
