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

package com.beatplaylist.chromium.adblock;

public class AdvertTerm {

	private String term;
	private TermType term_type;
	// Do we check if the url starts with the term or if the url contains the term?
	private boolean url_starts_with_term;

	public AdvertTerm(String term, TermType term_type, boolean url_starts_with_term) {
		this.term = term;
		this.term_type = term_type;
		this.url_starts_with_term = url_starts_with_term;
	}

	public String getTerm() {
		return this.term;
	}

	public TermType getTermType() {
		return this.term_type;
	}

	public boolean doesURLStartWithTerm() {
		return this.url_starts_with_term;
	}
}