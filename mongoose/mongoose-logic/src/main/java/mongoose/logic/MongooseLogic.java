package mongoose.logic;

import mongoose.domainmodel.DomainModelSnapshotLoader;
import naga.core.ngui.routing.UiRouteHandler;
import naga.core.ngui.routing.UiRouter;
import naga.core.ngui.routing.UiState;
import naga.core.orm.filter.StringFilterBuilder;
import naga.core.rx.RxFilter;
import naga.core.spi.gui.GuiToolkit;
import naga.core.spi.gui.node.BorderPane;
import naga.core.spi.gui.node.SearchBox;
import naga.core.spi.gui.node.Table;
import naga.core.spi.gui.node.ToggleButton;

/**
 * @author Bruno Salmon
 */
public class MongooseLogic {

    public static UiRouteHandler organizationsUiRouterHandler = new UiRouteHandler()
            .setPresentationModelFactory(OrganizationsPresentationModel::new)
            .setUiBuilder(MongooseLogic::buildOrganizationsUi)
            .setPresentationModelLogicBinder(MongooseLogic::doOrganizationsPresentationModelLogicBinding);

    public static void runBackendApplication() {
        UiRouter uiRouter = UiRouter.get();
        uiRouter.route("/organizations").handler(organizationsUiRouterHandler);
        uiRouter.start();
    }

    private static void buildOrganizationsUi(UiState uiState) {
        // Building the UI components (involved in the reactive filter)
        GuiToolkit toolkit = GuiToolkit.get();
        SearchBox<?> searchBox = toolkit.createNode(SearchBox.class);
        Table table = toolkit.createNode(Table.class);
        ToggleButton<?> limitCheckBox = toolkit.createNode(ToggleButton.class);
        limitCheckBox.setText("Limit to 100");
        limitCheckBox.setSelected(true);

        // Displaying the UI (Showing the first window can take a few seconds)
        toolkit.displayRootNode(toolkit.createNode(BorderPane.class)
                .setTop(searchBox)
                .setCenter(table)
                .setBottom(limitCheckBox));
        // Requesting the focus on the search box
        searchBox.requestFocus();

        OrganizationsPresentationModel pm = (OrganizationsPresentationModel) uiState.presentationModel();
        pm.searchTextProperty().bind(searchBox.textProperty());
        pm.limitProperty().bind(limitCheckBox.selectedProperty());
        table.displayResultProperty().bind(pm.organizationDisplayResultProperty());
    }

    private static void doOrganizationsPresentationModelLogicBinding(OrganizationsPresentationModel pm) {
        // Loading the domain model and setting up the reactive filter
        new RxFilter()
                .setDomainModel(DomainModelSnapshotLoader.getOrLoadDomainModel())
                .setDataSourceId(3)
                // Base filter
                .combine(new StringFilterBuilder("Organization")
                        .setLogicFields("country.(code,continent.code)")
                        .setOrderBy("name"))
                // Condition
                .combine(new StringFilterBuilder()
                        .setCondition("!closed"))
                // Fields to display
                .combine(new StringFilterBuilder()
                        .setDisplayFields("name,type.name, country.(name + ' (' + continent.name + ')')"))
                // Search box condition
                .combine(pm.searchTextProperty(), s -> "lower(name) like '%" + s.toLowerCase() + "%'")
                // Limit condition
                .combine(pm.limitProperty(), new StringFilterBuilder()
                        .setLimit("100"))
                .displayResultInto(pm.organizationDisplayResultProperty());
    }

}
