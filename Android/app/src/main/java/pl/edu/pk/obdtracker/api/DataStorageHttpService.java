package pl.edu.pk.obdtracker.api;

import pl.edu.pk.obdtracker.api.model.ObdData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Wojciech on 18.04.2017.
 */

public interface DataStorageHttpService {

    @POST("data")
    Call<Void> storeData(@Body ObdData obdData);
}
