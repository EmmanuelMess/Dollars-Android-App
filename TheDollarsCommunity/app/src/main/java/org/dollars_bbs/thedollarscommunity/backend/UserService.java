package org.dollars_bbs.thedollarscommunity.backend;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * @author Emmanuel
 *         on 13/4/2017, at 13:59.
 */
public interface UserService {
	@GET("user/{nick}")
	Call<User> getNick(@Path("nick") String nick);

	@POST("user/new")
	Call<User> create(@Body User user);
}
