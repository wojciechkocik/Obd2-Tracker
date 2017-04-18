package pl.edu.pk.obdtracker;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import pl.edu.pk.obdtracker.api.DataStorageHttpService;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by Wojciech on 18.04.2017.
 */

@Module
public class DataStorageHttpServiceModule {

    @Provides
    @Singleton
    DataStorageHttpService providesDataStorageHttpService() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.87:8080/")
                .addConverterFactory(JacksonConverterFactory.create(new ObjectMapper()))
                .client(builder.build())
                .build();

        return retrofit.create(DataStorageHttpService.class);
    }

}
