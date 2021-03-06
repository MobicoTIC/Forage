package io.github.plastix.forage.data.api;


import android.support.annotation.NonNull;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.github.plastix.forage.BuildConfig;
import io.github.plastix.forage.data.api.auth.OAuthSigningInterceptor;
import io.github.plastix.forage.data.api.gson.HtmlAdapter;
import io.github.plastix.forage.data.api.gson.ListTypeAdapterFactory;
import io.github.plastix.forage.data.api.gson.StringCapitalizerAdapter;
import io.realm.RealmObject;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthProvider;

/**
 * Dagger module that provides dependencies for {@link OkApiService} and {@link OkApiInteractor}.
 */
@Module
public class OkApiModule {

    @NonNull
    @Provides
    @Singleton
    @Named("BASE_ENDPOINT")
    public String provideBaseURL() {
        return ApiConstants.BASE_ENDPOINT;
    }

    @NonNull
    @Provides
    @Singleton
    public OkHttpOAuthProvider provideOkHttpOAuthProvider(OkHttpClient okHttpClient) {
        return new OkHttpOAuthProvider(
                ApiConstants.REQUEST_TOKEN_ENDPOINT,
                ApiConstants.ACCESS_TOKEN_ENDPOINT,
                ApiConstants.AUTHORIZATION_WEBSITE_URL,
                okHttpClient
        );
    }


    @NonNull
    @Provides
    @Singleton
    public OkHttpOAuthConsumer provideOkHttpOAuthConsumer() {
        return new OkHttpOAuthConsumer(BuildConfig.OKAPI_US_CONSUMER_KEY, BuildConfig.OKAPI_US_CONSUMER_SECRET);
    }

    @NonNull
    @Provides
    @Singleton
    public HostSelectionInterceptor provideUrlInterceptor() {
        return new HostSelectionInterceptor();
    }

    @NonNull
    @Singleton
    @Provides
    public OkHttpClient provideOkHttp(HostSelectionInterceptor host, OAuthSigningInterceptor signingInterceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(host)
                .addInterceptor(signingInterceptor)
                .build();
    }

    @NonNull
    @Provides
    @Singleton
    public Retrofit provideRetrofit(@NonNull @Named("BASE_ENDPOINT") String baseUrl,
                                    @NonNull GsonConverterFactory gsonConverter,
                                    @NonNull RxJavaCallAdapterFactory rxAdapter,
                                    @NonNull OkHttpClient client) {

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(gsonConverter)
                .addCallAdapterFactory(rxAdapter)
                .build();
    }

    @NonNull
    @Provides
    @Singleton
    public GsonConverterFactory provideGsonConverterFactory(@NonNull Gson gson) {
        return GsonConverterFactory.create(gson);
    }

    /**
     * Custom Gson to make Retrofit Gson adapter work with Realm objects
     */
    @NonNull
    @Provides
    @Singleton
    public Gson provideGson(@NonNull ListTypeAdapterFactory jsonArrayTypeAdapterFactory,
                            @NonNull HtmlAdapter htmlAdapter,
                            @NonNull StringCapitalizerAdapter stringCapitalizerAdapter) {

        return new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .registerTypeAdapterFactory(jsonArrayTypeAdapterFactory)
                .registerTypeAdapter(String.class, htmlAdapter)
                .registerTypeAdapter(String.class, stringCapitalizerAdapter)
                .create();
    }

    @NonNull
    @Provides
    @Singleton
    public RxJavaCallAdapterFactory providesRxAdapter() {
        return RxJavaCallAdapterFactory.create();
    }

    @NonNull
    @Provides
    @Singleton
    public OkApiService provideOkApiService(@NonNull Retrofit retrofit) {
        return retrofit.create(OkApiService.class);
    }

}
