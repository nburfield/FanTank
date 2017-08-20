package com.fantank.dto;

import com.fantank.validation.PasswordMatches;
import com.fantank.validation.ValidPassword;

@PasswordMatches
public class PasswordDto {
	
    private String oldPassword;

    @ValidPassword
    private String password;
    private String passwordConfirm;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String newPassword) {
        this.password = newPassword;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }
}
