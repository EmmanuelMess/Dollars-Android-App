package org.dollars_bbs.thedollarscommunity.backend;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Emmanuel
 *         on 14/4/2017, at 17:48.
 */

public class RetrofitLoad {
	public static Retrofit loadRetrofit() {
		return new Retrofit.Builder()
				.baseUrl("https://dollarscommunity.herokuapp.com")
				.addConverterFactory(GsonConverterFactory.create())
				.build();
	}
}
