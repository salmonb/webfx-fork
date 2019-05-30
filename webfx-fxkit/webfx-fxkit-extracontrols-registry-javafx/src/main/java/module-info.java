// Generated by WebFx

module webfx.fxkit.extracontrols.registry.javafx {

    // Direct dependencies modules
    requires javafx.controls;
    requires javafx.web;
    requires webfx.fxkit.extracontrols;
    requires webfx.fxkit.extracontrols.mapper.base;
    requires webfx.fxkit.extracontrols.mapper.javafx;
    requires webfx.fxkit.extracontrols.registry;
    requires webfx.fxkit.javafxgraphics.mapper;
    requires webfx.platform.shared.util;

    // Exported packages
    exports webfx.fxkit.extra.controls.registry.spi.impl.javafx;

    // Provided services
    provides webfx.fxkit.extra.controls.registry.spi.ExtraControlsRegistryProvider with webfx.fxkit.extra.controls.registry.spi.impl.javafx.JavaFxExtraControlsRegistryProvider;

}