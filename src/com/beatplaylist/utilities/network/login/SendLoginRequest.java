package com.beatplaylist.utilities.network.login;

import java.util.Base64;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.beatplaylist.enums.GenreType;
import com.beatplaylist.gui.LoginGUIManager;
import com.beatplaylist.settings.Settings;
import com.beatplaylist.utilities.Utilities;
import com.beatplaylist.utilities.currency.CurrencyType;
import com.beatplaylist.utilities.events.CompleteEvent;
import com.beatplaylist.utilities.events.SocketReceiveEvent;
import com.beatplaylist.utilities.network.netty.NETTYClient;
import com.beatplaylist.utilities.network.netty.security.SecurityManager;
import com.beatplaylist.utilities.network.serialized.JSONWrapper;
import com.beatplaylist.utilities.network.serialized.PacketType;
import com.beatplaylist.utilities.network.serialized.Post;
import com.beatplaylist.utilities.user.AccountType;
import com.beatplaylist.utilities.user.UserManager;

import javafx.application.Platform;

public class SendLoginRequest {

	public SendLoginRequest() {
	}

	public static void send(CompleteEvent event) {
		if (Utilities.getInstance().getLoginAttempts() >= 5) {
			event.onFail("LOGIN_ATTEMPT_EXCEEDED");
			Utilities.getInstance().startLoginTimer();
			return;
		}

		Post post = new Post();
		post.setPacketType(PacketType.LOGIN);

		String password = JSONValue.escape(UserManager.getInstance().user.password);
		String encryptedPassword = Base64.getEncoder().encodeToString(SecurityManager.getInstance().encrypt(password, SecurityManager.getInstance().publicKey));

		JSONObject object = new JSONObject();
		object.put("email", JSONValue.escape(UserManager.getInstance().user.email));
		object.put("password", encryptedPassword);
		object.put("access_key", JSONValue.escape(Settings.getInstance().getAccessToken()));
		object.put("jreVersion", System.getProperty("java.version"));
		object.put("country", Utilities.getInstance().getCountry());

		post.setJSONArray(object);

		Utilities.getInstance().incrementLoginAttempt();

		NETTYClient.getInstance().send(post, new SocketReceiveEvent() {

			@Override
			public void onSuccess(Post post) {
				JSONWrapper response = new JSONWrapper(post.getJSONMessage());

				String account_key = response.getJSONString("account_key");
				int id = response.getJSONInteger("user_id");
				String username = response.getJSONString("username");
				String display_name = response.getJSONString("display_name");
				String profile_image = response.getJSONString("profile_image");
				double balance = Double.valueOf(response.getJSONString("balance"));
				String genre = response.getJSONString("top_genre");
				String location = response.getJSONString("location");
				String birth_year = response.getJSONString("birth_year");
				String birth_month = response.getJSONString("birth_month");
				String birth_day = response.getJSONString("birth_day");
				String bio = response.getJSONString("bio");
				boolean premium = response.getJSONBoolean("isPremium");
				String premium_expiry_date = response.getJSONString("premium_expiry_date");
				String referral_code = response.getJSONString("referral_code");
				int referral_uses = response.getJSONInteger("referral_uses");
				int referral_bracket = response.getJSONInteger("referral_bracket");
				String account_type = response.getJSONString("account_type");
				String currency = response.getJSONString("currency");
				boolean used_referral = response.getJSONBoolean("hasUsedReferral");
				boolean verified = response.getJSONBoolean("verified");
				boolean has_two_step = response.getJSONBoolean("hasTwoStep");
				String registrationDate = response.getJSONString("registrationDate");
				String walletAddress = response.getJSONString("wallet_address");
				double claimableRhythm = Double.valueOf(response.getJSONString("claimableRhythm"));

				JSONWrapper subscription = new JSONWrapper((JSONObject) response.getJSONObject().get("subscription"));

				Settings.getInstance().setLastAccountKey(account_key, true);

				UserManager.getInstance().getUser().userID = id;
				UserManager.getInstance().getUser().accessToken = account_key;
				UserManager.getInstance().getUser().profileImageURL = profile_image;
				UserManager.getInstance().getUser().username = username;
				UserManager.getInstance().getUser().displayName = display_name;
				UserManager.getInstance().getUser().email = UserManager.getInstance().user.email;
				UserManager.getInstance().getUser().password = UserManager.getInstance().user.password;
				UserManager.getInstance().getUser().balance = balance;
				UserManager.getInstance().getUser().favouriteGenre = GenreType.getName(genre);
				UserManager.getInstance().getUser().location = location;
				UserManager.getInstance().getUser().birthDay = Integer.valueOf(birth_day);
				UserManager.getInstance().getUser().birthMonth = Integer.valueOf(birth_month);
				UserManager.getInstance().getUser().birthYear = Integer.valueOf(birth_year);
				UserManager.getInstance().getUser().bio = bio;
				UserManager.getInstance().getUser().premiumExpiry = premium_expiry_date;
				UserManager.getInstance().getUser().referralCode = referral_code;
				UserManager.getInstance().getUser().referralTaxBracket = referral_bracket;
				UserManager.getInstance().getUser().referralUseCount = referral_uses;
				UserManager.getInstance().getUser().currency = CurrencyType.getCurrency(currency);
				UserManager.getInstance().getUser().usedReferral = used_referral;
				UserManager.getInstance().getUser().has2FA = has_two_step;
				UserManager.getInstance().getUser().accountType = AccountType.getName(account_type);
				UserManager.getInstance().getUser().registrationDate = registrationDate;
				UserManager.getInstance().getUser().walletAddress = walletAddress;
				UserManager.getInstance().getUser().claimableRhythm = claimableRhythm;

				if (verified && UserManager.getInstance().getUser().accountType.isSmaller(AccountType.VERIFIED))
					UserManager.getInstance().getUser().accountType = AccountType.VERIFIED;
				else if (premium && UserManager.getInstance().getUser().accountType.isSmaller(AccountType.PREMIUM))
					UserManager.getInstance().getUser().accountType = AccountType.PREMIUM;

				if (subscription.getJSONObject().size() != 0)
					UserManager.getInstance().user.setSubscription(subscription.getJSONString("state"), subscription.getJSONString("start_date"), subscription.getJSONString("next_charge_date"), subscription.getJSONString("last_four"), subscription.getJSONString("expiry_month"), subscription.getJSONString("expiry_year"), subscription.getJSONString("card_type"));

				Platform.runLater(() -> {
					Settings.getInstance().setAutoLogin(true, true);
					Settings.getInstance().setAccountCredentials(UserManager.getInstance().user.email, UserManager.getInstance().user.password);

					LoginGUIManager.getInstance().handleLoginToBrowserTransition();

					Utilities.getInstance().resetLoginAttempt();
				});
			}

			@Override
			public void onError(String error) {
				event.onFail(error);
			}
		});
	}
}