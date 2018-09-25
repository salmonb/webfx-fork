package webfx.fxkits.core.launcher.spi;

import javafx.application.Application;
import javafx.stage.Screen;
import javafx.stage.Stage;
import webfx.platforms.core.services.uischeduler.UiScheduler;
import webfx.platforms.core.util.function.Factory;

/**
 * @author Bruno Salmon
 */
public interface FxKitLauncherProvider {

    String getUserAgent();

    default Screen getPrimaryScreen() {
        return Screen.getPrimary();
    }

    Stage getPrimaryStage();

    void launchApplication(Factory<Application> applicationFactory, String... args);

    default boolean isReady() {
        return true;
    }

    default void onReady(Runnable runnable) {
        UiScheduler.runInUiThread(runnable);
    }

}