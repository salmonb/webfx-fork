// Generated by WebFx

module webfx.platform.client.windowlocation {

    // Direct dependencies modules
    requires java.base;
    requires webfx.platform.shared.json;
    requires webfx.platform.shared.util;

    // Exported packages
    exports dev.webfx.platform.client.services.windowlocation;
    exports dev.webfx.platform.client.services.windowlocation.spi;
    exports dev.webfx.platform.client.services.windowlocation.spi.impl;

    // Used services
    uses dev.webfx.platform.client.services.windowlocation.spi.WindowLocationProvider;

}