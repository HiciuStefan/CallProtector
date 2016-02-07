package com.transapps.callprotector.Requests;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Hiciu on 2/7/2016.
 */

public interface GetRequests {
    @GET("number/{id}")
    public Call<String> getNumber(@Path("id") String id);

}

