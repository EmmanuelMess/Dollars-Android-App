package org.dollars_bbs.thedollarscommunity.backend;

import com.google.gson.annotations.SerializedName;

/**
 * @author Emmanuel
 *         on 13/4/2017, at 13:59.
 */

public class User {
	@SerializedName("nick")
	String nick;

	//@SerializedName("avatar")
	//ByteArray avatar;

	@SerializedName("birth")
	long birth;

	@SerializedName("desc")
	String desc;

	@SerializedName("gender")
	String gender;

	User(String nick, long birth, String desc, String gender) {
		this.nick = nick;
		this.birth = birth;
		this.desc = desc;
		this.gender = gender;
	}
}