package com.fantank.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserInvestmentsDto {
	@NotNull
    private long userId;
     
    @NotNull
    @Size(min = 1, max=255)
    private String investmentId;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getInvestmentId() {
		return investmentId;
	}

	public void setInvestmentId(String investmentId) {
		this.investmentId = investmentId;
	}
}
