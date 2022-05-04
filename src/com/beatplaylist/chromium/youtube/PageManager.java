package com.beatplaylist.chromium.youtube;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PageManager {

	// The following code was externally sourced and edited with some minor changes.
	// Source is not being used for monetary gain, instead used to help benefit users adding songs to playlist's via a URL, in order to fetch the name of the song.

	private static PageManager instance = new PageManager();

	public static PageManager getInstance() {
		return instance;
	}

	public String getPageTitle(String url) {
		if (url.isEmpty())
			return "";
		try {
			URL u = new URL(url.split("&")[0]);
			URLConnection conn = u.openConnection();

			ContentType contentType = getContentTypeHeader(conn);
			if (!contentType.contentType.equals("text/html"))
				return "";
			else {
				Charset charset = getCharset(contentType);
				if (charset == null)
					charset = Charset.defaultCharset();

				InputStream in = conn.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(in, charset));
				int n = 0;
				char[] chars = new char[1024];
				StringBuilder content = new StringBuilder();

				while ((n = reader.read(chars, 0, chars.length)) != -1) {
					content.append(chars, 0, n);
				}
				reader.close();
				Matcher matcher = Pattern.compile("\\<title>(.*)\\</title>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(content);
				if (matcher.find()) {
					return matcher.group(1).replaceAll("[\\s\\<>]+", " ").replace("- YouTube", "").trim();
				} else
					return "";
			}
		} catch (Exception e) {
			System.out.println("Could not connect to an internet connection.");
			System.out.println(url);
			// e.printStackTrace();
			return "";
		}
	}

	private ContentType getContentTypeHeader(URLConnection conn) {
		int i = 0;
		boolean moreHeaders = true;
		do {
			String headerName = conn.getHeaderFieldKey(i);
			String headerValue = conn.getHeaderField(i);
			if (headerName != null && headerName.equals("Content-Type"))
				return new ContentType(headerValue);
			i++;
			moreHeaders = headerName != null || headerValue != null;
		} while (moreHeaders);
		return null;
	}

	private Charset getCharset(ContentType contentType) {
		if (contentType != null && contentType.charsetName != null && Charset.isSupported(contentType.charsetName))
			return Charset.forName(contentType.charsetName);
		else
			return null;
	}

	private class ContentType {

		private String contentType;
		private String charsetName;

		private ContentType(String headerValue) {
			if (headerValue == null)
				throw new IllegalArgumentException("ContentType must be constructed with a not-null headerValue");
			int n = headerValue.indexOf(";");
			if (n != -1) {
				this.contentType = headerValue.substring(0, n);
				Matcher matcher = Pattern.compile("charset=([-_a-zA-Z0-9]+)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(headerValue);
				if (matcher.find())
					this.charsetName = matcher.group(1);
			} else
				this.contentType = headerValue;
		}
	}
}