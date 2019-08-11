// Generated by WebFx

module mongoose.backend.activities.users {

    // Direct dependencies modules
    requires javafx.base;
    requires javafx.graphics;
    requires mongoose.backend.masterslave;
    requires mongoose.client.activity;
    requires mongoose.client.presentationmodel;
    requires mongoose.shared.domain;
    requires mongoose.shared.entities;
    requires webfx.framework.client.activity;
    requires webfx.framework.client.domain;
    requires webfx.framework.client.uifilter;
    requires webfx.framework.client.uirouter;
    requires webfx.framework.shared.expression;
    requires webfx.framework.shared.operation;
    requires webfx.framework.shared.router;
    requires webfx.fxkit.extracontrols;
    requires webfx.platform.client.windowhistory;
    requires webfx.platform.shared.util;

    // Exported packages
    exports mongoose.backend.activities.users;
    exports mongoose.backend.activities.users.routing;
    exports mongoose.backend.operations.routes.users;

    // Provided services
    provides webfx.framework.client.operations.route.RouteRequestEmitter with mongoose.backend.activities.users.RouteToUsersRequestEmitter;
    provides webfx.framework.client.ui.uirouter.UiRoute with mongoose.backend.activities.users.UsersUiRoute;

}