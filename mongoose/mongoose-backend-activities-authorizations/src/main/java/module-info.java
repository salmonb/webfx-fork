// Generated by WebFx

module mongoose.backend.activities.authorizations {

    // Direct dependencies modules
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires mongoose.client.authn;
    requires webfx.extras.visual.controls.grid;
    requires webfx.framework.client.activity;
    requires webfx.framework.client.controls;
    requires webfx.framework.client.domain;
    requires webfx.framework.client.orm.dql;
    requires webfx.framework.client.orm.reactive.visual;
    requires webfx.framework.client.uirouter;
    requires webfx.framework.shared.operation;
    requires webfx.framework.shared.orm.entity;
    requires webfx.framework.shared.router;
    requires webfx.platform.client.windowhistory;
    requires webfx.platform.shared.util;

    // Exported packages
    exports mongoose.backend.activities.authorizations;
    exports mongoose.backend.activities.authorizations.routing;
    exports mongoose.backend.activities.operations.authorizations;

    // Provided services
    provides webfx.framework.client.operations.route.RouteRequestEmitter with mongoose.backend.activities.authorizations.RouteToAuthorizationsRequestEmitter;
    provides webfx.framework.client.ui.uirouter.UiRoute with mongoose.backend.activities.authorizations.AuthorizationsUiRoute;

}