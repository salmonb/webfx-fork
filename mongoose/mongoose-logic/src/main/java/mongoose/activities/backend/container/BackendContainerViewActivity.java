package mongoose.activities.backend.container;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import mongoose.activities.shared.container.SharedContainerViewActivity;
import mongoose.activities.shared.logic.ui.theme.DarkTheme;
import mongoose.activities.shared.logic.ui.theme.LightTheme;
import mongoose.activities.shared.logic.ui.theme.Theme;
import naga.framework.ui.i18n.I18n;

/**
 * @author Bruno Salmon
 */
public class BackendContainerViewActivity extends SharedContainerViewActivity {

    @Override
    public Node buildUi() {
        super.buildUi();

        I18n i18n = getI18n();
        Button organizationsButton = i18n.translateText(new Button(), "Organizations");
        Button eventsButton = i18n.translateText(new Button(), "Events");
        Button bookingsButton = i18n.translateText(new Button(), "Bookings");
        Button lettersButton = i18n.translateText(new Button(), "Letters");
        Button monitorButton = i18n.translateText(new Button(), "Monitor");
        Button testerButton = i18n.translateText(new Button(), "Tester");
        Button lightTheme = i18n.translateText(new Button(), "Light");
        Button darkTheme = i18n.translateText(new Button(), "Dark");

        borderPane.setTop(new FlowPane(backButton, forwardButton, organizationsButton, eventsButton, bookingsButton, lettersButton, monitorButton, testerButton, englishButton, frenchButton/* , lightTheme(), DarkTheme()*/));

        organizationsButton.setOnAction(event ->  getHistory().push("/organizations"));
        eventsButton.setOnAction(event ->  getHistory().push("/events"));
        bookingsButton.setOnAction(event -> getHistory().push("/event/" + getParameter("eventId") + "/bookings"));
        lettersButton.setOnAction(event -> getHistory().push("/event/" + getParameter("eventId") + "/letters"));
        monitorButton.setOnAction(event -> getHistory().push("/monitor"));
        testerButton.setOnAction(event -> getHistory().push("/tester"));
        lightTheme.setOnAction(e -> new LightTheme().apply());
        darkTheme.setOnAction(e -> new DarkTheme().apply());

        borderPane.backgroundProperty().bind(Theme.mainBackgroundProperty());
        backButton.textFillProperty().bind(Theme.mainTextFillProperty());
        forwardButton.textFillProperty().bind(Theme.mainTextFillProperty());
        organizationsButton.textFillProperty().bind(Theme.mainTextFillProperty());
        eventsButton.textFillProperty().bind(Theme.mainTextFillProperty());
        bookingsButton.textFillProperty().bind(Theme.mainTextFillProperty());
        lettersButton.textFillProperty().bind(Theme.mainTextFillProperty());
        monitorButton.textFillProperty().bind(Theme.mainTextFillProperty());
        englishButton.textFillProperty().bind(Theme.mainTextFillProperty());
        frenchButton.textFillProperty().bind(Theme.mainTextFillProperty());
        testerButton.textFillProperty().bind(Theme.mainTextFillProperty());
        lightTheme.textFillProperty().bind(Theme.mainTextFillProperty());
        darkTheme.textFillProperty().bind(Theme.mainTextFillProperty());

        return borderPane;
    }
}
