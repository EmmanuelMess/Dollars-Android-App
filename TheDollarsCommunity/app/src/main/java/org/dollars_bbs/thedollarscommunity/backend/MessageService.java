package org.dollars_bbs.thedollarscommunity.backend;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * @author Emmanuel
 *         on 13/4/2017, at 14:01.
 */

public interface MessageService {
	@GET("message")
	Call<List<Message>> all();

	@GET("message/{id}")
	Call<Message> getId(@Path("id") int id);

	@POST("message/new")
	Call<Message> create(@Body Message message);
}