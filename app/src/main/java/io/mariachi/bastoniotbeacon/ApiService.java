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

    @Headers("X-Auth-Token: jQcfiKL7fh0R5VuqO2p3fbXzpUJ6Uq")
    @POST ("/api/v1.6/variables/58411e7b7625422167f95667/values")
    Call<ResponseBody> setCorazon(@Body RequestBody body);

    @Headers("X-Auth-Token: jQcfiKL7fh0R5VuqO2p3fbXzpUJ6Uq")
    @POST ("/api/v1.6/variables/58411ed376254223ad7cd2fb/values")
    Call<ResponseBody> setGiro(@Body RequestBody body);

    @Headers("X-Auth-Token: jQcfiKL7fh0R5VuqO2p3fbXzpUJ6Uq")
    @POST ("/api/v1.6/variables/58411ee8762542247ca34f20/values")
    Call<ResponseBody> setCabeceo(@Body RequestBody body);
}
