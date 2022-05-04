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

package com.beatplaylist.utilities.user;

import java.util.HashMap;
import java.util.Map;

import com.beatplaylist.utilities.currency.CurrencyType;
import com.beatplaylist.utilities.web3.PartneredContract;

public class LoginUser extends BaseUser {

	public int userID = 0;
	public String accessToken = "";
	public boolean has2FA = false;

	// Referral and Balance
	public String referralCode = "";
	public double balance = 0;
	public boolean usedReferral = false;

	// Logged in users referral use count and how much percentage per $1 the user gets when another user makes a purchase using their referral code.
	public int referralUseCount = 0, referralTaxBracket = 0;

	public CurrencyType currency = CurrencyType.USD;
	
	// Crypto Related
	public String bnbBalance = "";
	public Map<PartneredContract, String> contractBalances = new HashMap<>();
	public String walletAddress = "";
	public double claimableRhythm = 0;

	// Subscription
	public String subscriptionState = "", subscriptionStartDate = "", subscriptionNextCharge = "", subscriptionLastFour = "", subscriptionCardExpiryMonth = "", subscriptionCardExpiryYear = "", subscriptionCardType = "";

	public void setContractBalance(PartneredContract contract, String balance) {
		this.contractBalances.put(contract, balance);
	}
	
	public String getContractBalance(PartneredContract contract) {
		return this.contractBalances.get(contract);
	}
	
	public void clearBalances() {
		this.contractBalances.clear();
	}
	
	public void setSubscription(String state, String start_date, String next_charge, String last_four, String expiry_month, String expiry_year, String cardType) {
		this.subscriptionState = state;
		this.subscriptionStartDate = start_date;
		this.subscriptionNextCharge = next_charge;
		this.subscriptionLastFour = last_four;
		this.subscriptionCardExpiryMonth = expiry_month;
		this.subscriptionCardExpiryYear = expiry_year;
		this.subscriptionCardType = cardType;
	}
}