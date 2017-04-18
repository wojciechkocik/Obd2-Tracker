package pl.edu.pk.obdtracker;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import pl.edu.pk.obdtracker.api.HttpService;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by Wojciech on 18.04.2017.
 */

@Module
public class HttpServiceModule {

    private Retrofit retrofit(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.87:8080/")
                .addConverterFactory(JacksonConverterFactory.create(new ObjectMapper()))
                .build();
        return retrofit;
    }

    @Provides
    @Singleton
    HttpService providesHttpService() {
        return retrofit().create(HttpService.class);
    }

}
