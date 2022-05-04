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

import java.util.ArrayList;
import java.util.List;

public class AdvertManager {

	private static AdvertManager instance = new AdvertManager();

	public static AdvertManager getInstance() {
		return instance;
	}

	private List<AdvertTerm> advert_terms = new ArrayList<>();

	public void addAdvertTerm(AdvertTerm term) {
		this.advert_terms.add(term);
	}

	public void removeAdvertTerm(AdvertTerm term) {
		this.advert_terms.remove(term);
	}

	public List<AdvertTerm> getAdvertTerms() {
		return this.advert_terms;
	}
}