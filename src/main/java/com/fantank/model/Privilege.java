package com.fantank.model;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class Privilege {
	
    private Long id;
    private String name;
    private Collection<Role> roles;
    
    public Privilege() {
        super();
    }

    public Privilege(final String name) {
        super();
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
    @ManyToMany(mappedBy = "privileges")
	public Collection<Role> getRoles() {
		return roles;
	}
    
	public void setRoles(Collection<Role> roles) {
		this.roles = roles;
	}
}
