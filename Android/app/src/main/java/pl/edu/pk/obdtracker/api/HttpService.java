package pl.edu.pk.obdtracker.api;

import pl.edu.pk.obdtracker.api.model.InitResponse;
import pl.edu.pk.obdtracker.api.model.ObdData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Wojciech on 18.04.2017.
 */

public interface HttpService {

    @POST("data")
    Call<Void> storeData(@Body ObdData obdData);

    @GET("account")
    Call<InitResponse> initAccount();
}
