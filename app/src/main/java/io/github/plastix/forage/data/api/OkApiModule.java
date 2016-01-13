package io.github.plastix.forage.data.api;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;

@Module
public class OkApiModule {

    public static final String BASE_ENDPOINT = "http://www.opencaching.us";

    @Provides
    @Singleton
    @Named("BASE_ENDPOINT")
    public String provideBaseURL() {
        return BASE_ENDPOINT;
    }

    @Provides
    @Singleton
    public Retrofit provideRetrofit(@Named("BASE_ENDPOINT") String baseUrl, GsonConverterFactory gsonConverter, RxJavaCallAdapterFactory rxAdapter) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(gsonConverter)
                .addCallAdapterFactory(rxAdapter)
                .build();
    }

    @Provides
    @Singleton
    public GsonConverterFactory provideGsonConverterFactory() {
        return GsonConverterFactory.create();
    }

    @Provides
    @Singleton
    public RxJavaCallAdapterFactory providesRxAdapter() {
        return RxJavaCallAdapterFactory.create();
    }

    @Provides
    @Singleton
    public OkApiService provideOkApiService(Retrofit retrofit) {
        return retrofit.create(OkApiService.class);
    }

}