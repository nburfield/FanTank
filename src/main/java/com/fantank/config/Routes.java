package com.fantank.config;

public class Routes {
	public static final String INDEX ="/";
	public static final String ROOT = "/";
	public static final String REGISTER = INDEX + "register";
	public static final String INVEST = INDEX + "invest";
	public static final String INVESTMENTS = INDEX + "invest/*";
	public static final String CONTACT = INDEX + "contact";
	public static final String DISCLAIMER = INDEX +"disclaimer";
	public static final String LOGIN = INDEX + "login";
	public static final String ABOUT = INDEX + "about";
	public static final String PRIVACY = INDEX + "privacy";
	public static final String HOWITWORKS = INDEX + "howitworks";
	public static final String TERMS = INDEX + "terms";
	public static final String JOBS = INDEX + "jobs";
	public static final String PROJECTS = INDEX + "projects/**";
	public static final String CSS = INDEX + "css/**/*";
	public static final String IMAGES = INDEX + "images/**/*";
	public static final String JS = INDEX + "js/**/*";
	public static final String FONTS = INDEX + "fonts/**/*";
	public static final String CONFIRM = INDEX + "registrationConfirm*";
	public static final String RESET = INDEX + "user/reset*";
	public static final String TOKEN = INDEX + "user/token*";
	public static final String SOCIALERROR = INDEX + "signin/**";
	public static final String WEBHOOK = INDEX + "investments/webhook";
	public static final String ROBOTS = INDEX + "robots.txt";
	public static final String USERDATA = INDEX + "user/data";
	public static final String CHANGEPASSWORD = INDEX + "user/changePassword*";
	public static final String CSRF = INDEX + "csrf";
}
