package org.rhok.microdonaciones.resultDelegate;

import java.io.Serializable;

import org.rhok.microdonaciones.MainActivity;

import com.paypal.android.MECL.PayPalListener;

/*
 * Our PayPalListener class to get the device reference token from MECL
 */
public class ResultDelegate implements PayPalListener, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public void couldNotFetchDeviceReferenceToken() {
		//Initialization failed and we didn't get a token
		MainActivity._deviceReferenceToken = null;
	}

	@Override
	public void receivedDeviceReferenceToken(String token) {
		//Initialization was successful
		MainActivity._deviceReferenceToken = token;
	}

}
