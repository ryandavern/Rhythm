package com.beatplaylist.utilities.web3;

public enum PartneredContract {

	RHYTHM("RHYTHM", "0xE4318F2aCf2b9c3f518A3a03B5412F4999970Ddb", "https://rhythm.cash/", true), //
	BNB("BNB", "", "https://binance.com/", false), //
	SAFEMOON("SFM", "0x42981d0bfbAf196529376EE702F2a9Eb9092fcB5", "https://safemoon.net/", false), //
	BABYSWAP("BABYSWAP", "0x53E562b9B7E5E94b81f10e96Ee70Ad06df3D2657", "https://home.babyswap.finance/", false), //
	HODL("HODL", "0x5788105375ecF7F675C29e822FD85fCd84d4cd86", "https://hodltoken.net/", false), //
	WSPP("WSPP", "0x46d502fac9aea7c5bc7b13c8ec9d02378c33d36f", "https://wolfsafepoorpeople.com/", false);

	private String name, contractAddress, website;
	private boolean hasWalletPerks;

	PartneredContract(String name, String contractAddress, String website, boolean hasWalletPerks) {
		this.name = name;
		this.contractAddress = contractAddress;
		this.website = website;
		this.hasWalletPerks = hasWalletPerks;
	}

	public String getName() {
		return this.name;
	}

	public String getContractAddress() {
		return this.contractAddress;
	}

	public String getWebsite() {
		return this.website;
	}

	public boolean hasWalletPerks() {
		return this.hasWalletPerks;
	}
}