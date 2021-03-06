package io.github.plastix.forage.ui.map;

import dagger.Module;
import io.github.plastix.forage.ui.base.BaseModule;

/**
 * Dagger module that provides dependencies for {@link MapActivity}.
 * This is used to inject the cache list presenter and view into each other.
 */
@Module
public class MapModule extends BaseModule<MapActivityView> {
    public MapModule(MapActivityView view) {
        super(view);
    }
}
