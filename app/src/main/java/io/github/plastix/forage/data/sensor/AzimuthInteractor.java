package io.github.plastix.forage.data.sensor;

import android.hardware.SensorManager;
import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Provider;

import rx.Observable;


/**
 * A Reactive wrapper around the Android compass sensor.
 */
public class AzimuthInteractor {

    private Provider<AzimuthObserver> azimuthProvider;

    @Inject
    public AzimuthInteractor(@NonNull Provider<AzimuthObserver> azimuthProvider) {
        this.azimuthProvider = azimuthProvider;
    }

    /**
     * Returns an Observable that emits the Azimuth of the Android compass.
     *
     * @return Observable
     */
    @NonNull
    public Observable<Float> getAzimuthObservable() {
        AzimuthObserver observer = azimuthProvider.get();
        observer.setSensorDelay(SensorManager.SENSOR_DELAY_UI);
        return Observable.create(observer);
    }
}
