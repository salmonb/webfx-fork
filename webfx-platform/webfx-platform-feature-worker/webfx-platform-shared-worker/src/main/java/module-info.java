// Generated by WebFx

module webfx.platform.shared.worker {

    // Direct dependencies modules
    requires java.base;
    requires webfx.platform.shared.util;

    // Exported packages
    exports webfx.platform.shared.services.worker;
    exports webfx.platform.shared.services.worker.spi;
    exports webfx.platform.shared.services.worker.spi.abstrimpl;

    // Used services
    uses webfx.platform.shared.services.worker.spi.WorkerServiceProvider;

}