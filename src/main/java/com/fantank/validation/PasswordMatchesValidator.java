package com.fantank.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.fantank.dto.PasswordDto;
import com.fantank.dto.UserDto;


public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

	@Override
	public void initialize(PasswordMatches arg0) {
		//
	}

	@Override
	public boolean isValid(Object obj, ConstraintValidatorContext arg1) {
		if(obj instanceof UserDto) {
			final UserDto user = (UserDto) obj;
			return user.getPassword().equals(user.getPasswordConfirm());
		}
		else {
			final PasswordDto user = (PasswordDto) obj;
			return user.getPassword().equals(user.getPasswordConfirm());
		}
	}
}
