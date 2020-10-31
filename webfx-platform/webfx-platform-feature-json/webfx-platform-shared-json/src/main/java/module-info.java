// Generated by WebFx

module webfx.platform.shared.json {

    // Direct dependencies modules
    requires java.base;
    requires webfx.lib.javacupruntime;
    requires webfx.platform.shared.util;

    // Exported packages
    exports webfx.platform.shared.services.json;
    exports webfx.platform.shared.services.json.parser;
    exports webfx.platform.shared.services.json.parser.javacup;
    exports webfx.platform.shared.services.json.parser.jflex;
    exports webfx.platform.shared.services.json.spi;
    exports webfx.platform.shared.services.json.spi.impl.listmap;

    // Used services
    uses webfx.platform.shared.services.json.spi.JsonProvider;

}