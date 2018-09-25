package webfx.platforms.core.services.windowhistory.spi.impl;

import webfx.platforms.core.services.windowhistory.spi.BrowsingHistoryEvent;
import webfx.platforms.core.services.windowhistory.spi.BrowsingHistoryLocation;
import webfx.platforms.core.services.windowlocation.spi.PathStateLocation;
import webfx.platforms.core.services.windowlocation.spi.impl.PathStateLocationImpl;

/**
 * @author Bruno Salmon
 */
public final class BrowsingHistoryLocationImpl extends PathStateLocationImpl implements BrowsingHistoryLocation {

    private BrowsingHistoryEvent event;
    private final String key;

    public BrowsingHistoryLocationImpl(PathStateLocation location, BrowsingHistoryEvent event, String key) {
        super(location);
        this.event = event;
        this.key = key;
    }

    public void setEvent(BrowsingHistoryEvent event) {
        this.event = event;
    }

    @Override
    public BrowsingHistoryEvent getEvent() {
        return event;
    }

    @Override
    public String getLocationKey() {
        return key;
    }
}
