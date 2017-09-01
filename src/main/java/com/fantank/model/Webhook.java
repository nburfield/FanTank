package com.fantank.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;

@Entity
public class Webhook {

	private String id;
	private Date created;
	
	public Webhook() {
		super();
	}

	public Webhook(String id) {
		super();
		setId(id);
	}

	@Id
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	@PrePersist
	protected void onCreate() {
		this.created = new Date();
	}
}
