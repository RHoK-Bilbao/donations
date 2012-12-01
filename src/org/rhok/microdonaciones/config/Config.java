package org.rhok.microdonaciones.config;

import com.paypal.android.MECL.PayPal;

public class Config {
	
	public static final String CACHE_DIR = "/storage/sdcard0/MD/image_cache/";
	
	public static final String BASE_URL = "http://microdonaciones.hazloposible.org";
	
	public static final String PROJECTS_URL= BASE_URL + "/proyectos";
	
	public static final String CLOSED_PROJECTS_URL= PROJECTS_URL + "/default.aspx?precon=2";
	
	// The PayPal server to be used - can also be ENV_NONE and ENV_LIVE
	public static final int server = PayPal.ENV_SANDBOX;
		
	// The ID of your application that you received from PayPal
	public static final String appID = "APP-80W284485P519543T";
	
	public static final String APP_SERVER= "http://tagmin.eu/base/setcheckout/";
}
