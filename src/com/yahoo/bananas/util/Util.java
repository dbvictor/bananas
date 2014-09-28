package com.yahoo.bananas.util;

import java.math.BigInteger;
import java.security.MessageDigest;

public class Util {
	/** Get a URL to a profile image generated from the hash value obtained from userId using gravatar. */
	public static String getProfileUrl(final String userId) {
		String hex = "";
		try {
			final MessageDigest digest = MessageDigest.getInstance("MD5");
			final byte[] hash = digest.digest(userId.getBytes());
			final BigInteger bigInt = new BigInteger(hash);
			hex = bigInt.toString(16);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "http://www.gravatar.com/avatar/" + hex + "?d=identicon";
	}
}
