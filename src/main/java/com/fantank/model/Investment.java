package com.fantank.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Investment {
	
	private long id;
    private User user;
    private String investmentId;
    private Offering offering;
    
    public Investment() {
    }

	public Investment(String investmentId, User userLoggedIn) {
		this.setInvestmentId(investmentId);
		this.setUser(userLoggedIn);
	}
	
	public Investment(String investmentId, User userLoggedIn, Offering offering) {
		this.setInvestmentId(investmentId);
		this.setUser(userLoggedIn);
		this.setOffering(offering);
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}
    
	public void setId(long id) {
		this.id = id;
	}
	
	@ManyToOne
    @JoinColumn(name = "user_id")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getInvestmentId() {
		return investmentId;
	}
	
	public void setInvestmentId(String investmentId) {
		this.investmentId = investmentId;
	}

	@ManyToOne
    @JoinColumn(name = "offering_id")
	public Offering getOffering() {
		return offering;
	}

	public void setOffering(Offering offering) {
		this.offering = offering;
	}
}
