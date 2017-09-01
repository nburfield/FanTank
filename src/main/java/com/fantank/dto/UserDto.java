package com.fantank.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fantank.validation.PasswordMatches;
import com.fantank.validation.ValidEmail;
import com.fantank.validation.ValidPassword;


@PasswordMatches
public class UserDto {
	@NotNull
    @Size(min = 1, max=255)
    private String firstName;
     
    @NotNull
    @Size(min = 1, max=255)
    private String lastName;
     
    @NotNull
    @ValidPassword
    private String password;
    private String passwordConfirm;
     
    @ValidEmail
    @NotNull
    @Size(min = 1, max=255)
    private String email;
    
    private long id;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordConfirm() {
		return passwordConfirm;
	}

	public void setPasswordConfirm(String matchingPassword) {
		this.passwordConfirm = matchingPassword;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
