package io.mariachi.bastoniotbeacon;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Tony on 26/11/2016.
 */

public interface ApiService {

    @POST ("/api/v2/baston/")
    Call<ResponseBody> getCall(@Body RequestBody body);
}
