// Generated by WebFx

module webfx.platform.vertx.json.impl {

    // Direct dependencies modules
    requires java.base;
    requires vertx.core;
    requires webfx.platform.shared.json;
    requires webfx.platform.shared.util;

    // Exported packages
    exports dev.webfx.platform.vertx.services.json.spi.impl;

    // Provided services
    provides dev.webfx.platform.shared.services.json.spi.JsonProvider with dev.webfx.platform.vertx.services.json.spi.impl.VertxJsonObject;

}