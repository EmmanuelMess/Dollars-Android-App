package org.dollars_bbs.thedollarscommunity.backend;

import com.google.gson.annotations.SerializedName;

/**
 * @author Emmanuel
 *         on 13/4/2017, at 14:00.
 */

public class Message {
	@SerializedName("nick")
	public
	String nick;

	@SerializedName("isimage")
	boolean isimage;

	@SerializedName("msg")
	public
	String msg;

	//@SerializedName("img")
	//ByteArray img;

	public Message(String nick, boolean isimage, String msg) {
		this.nick = nick;
		this.isimage = isimage;
		this.msg = msg;
	}
}