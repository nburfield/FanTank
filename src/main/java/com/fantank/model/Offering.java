package com.fantank.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Offering {

	private String id;
	private String offeringId;
	private Set<Investment> investments;
	
	@Id
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getOfferingId() {
		return offeringId;
	}

	public void setOfferingId(String offeringId) {
		this.offeringId = offeringId;
	}

	@OneToMany(mappedBy = "offering", cascade = CascadeType.ALL)
	public Set<Investment> getInvestments() {
		return investments;
	}
	
	public void setInvestments(Set<Investment> investments) {
		this.investments = investments;
	}
}
