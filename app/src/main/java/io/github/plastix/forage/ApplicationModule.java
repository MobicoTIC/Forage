package io.github.plastix.forage;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module that provides (singleton) application wide dependencies.
 */
@Module
public class ApplicationModule {

    @NonNull
    private final ForageApplication app;

    public ApplicationModule(@NonNull ForageApplication app) {
        this.app = app;
    }

    @NonNull
    @Provides
    @Singleton
    public Application provideApplication() {
        return app;
    }

    /**
     * Allow the application context to be injected but require that it be annotated with
     * {@link ApplicationScope @Annotation} to explicitly differentiate it from an activity context.
     */
    @NonNull
    @Provides
    @Singleton
    @ApplicationScope
    public Context provideApplicationContext() {
        return app.getApplicationContext();
    }

    @NonNull
    @Provides
    @Singleton
    public Resources provideResources(@NonNull @ApplicationScope Context context) {
        return context.getResources();
    }
}
