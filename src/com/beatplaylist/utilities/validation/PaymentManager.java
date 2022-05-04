/**
 *  Disclaimer
 *  This project was created by Ryan Davern.
 *  Start Date: 30/03/2016.
 *  
 *  Copyright (C) 2017 Ryan Davern - All Rights Reserved.
 *  You may not use, distribute, monetize or modify this code under the terms of the Copyright Act 1994.
 *  You may use the compiled program, which can be downloaded at https://www.beatplaylist.com/. Any modified versions or versions uploaded to a different website is against TOS (https://www.beatplaylist.com/terms).
 *  
 *  For more information on the Copyright Act 1994, please visit http://www.legislation.govt.nz/act/public/1994/0143/latest/DLM345634.html.
 */

package com.beatplaylist.utilities.validation;

import java.util.HashMap;
import java.util.Map;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Token;

public class PaymentManager {

	private static PaymentManager instance = new PaymentManager();

	public static PaymentManager getInstance() {
		return instance;
	}

	public String stripe_publishable_key = "";

	public Token getToken(String cardNumber, String expiry_month, String expiry_year, String cvc) {
		Stripe.apiKey = stripe_publishable_key;

		Map<String, Object> tokenParams = new HashMap<>(), cardParams = new HashMap<>();

		cardParams.put("number", cardNumber);
		cardParams.put("exp_month", expiry_month);
		cardParams.put("exp_year", expiry_year);
		cardParams.put("cvc", cvc);

		tokenParams.put("card", cardParams);

		// Create token from credit card information.
		Token token = null;
		try {
			token = Token.create(tokenParams);
		} catch (StripeException e) {
			e.printStackTrace();
		}
		return token;
	}
}