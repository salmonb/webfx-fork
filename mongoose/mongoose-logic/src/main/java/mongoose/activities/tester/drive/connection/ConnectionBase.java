package mongoose.activities.tester.drive.connection;


import mongoose.activities.tester.listener.ConnectionEvent;
import mongoose.activities.tester.listener.EventListener;
import mongoose.activities.tester.listener.EventListenerImpl;
import naga.core.spi.platform.Platform;

import java.util.ArrayList;
import java.util.List;

import static mongoose.activities.tester.drive.connection.ConnectionState.*;

/**
 * @author Jean-Pierre Alonso.
 */
public abstract class ConnectionBase implements Connection {
    int id;
    private ConnectionState state = NOT_CONNECTED;
    protected List<ConnectionEvent> uncommitedEventList = new ArrayList<>();    // List of events on this specific connection
    EventListener listener = EventListenerImpl.getInstance();

    // Parse all kind of ConnectionEvent
    @Override
    public void applyEvent(ConnectionEvent event) {
//        switch (event.getType()) {
        switch (event.getType()) {
            case CONNECTING:
                state = CONNECTING;
                break;
            case CONNECTED:
                state = CONNECTED;
                break;
            case UNCONNECTING:
                state = UNCONNECTING;
                break;
            case NOT_CONNECTED:
                state = NOT_CONNECTED;
                break;
            default:
                state = null;
                Platform.log("Connection state undefined !");
        }
        if (listener != null) {
            listener.onEvent(event);
            System.out.println("CnxApply "+event.getType()+" ");
        }
    }

    protected void recordEvent(ConnectionEvent event) {
        uncommitedEventList.add(event);
    }

    @Override
    public List<ConnectionEvent> getUncommitedEvents() {
        return uncommitedEventList;
    }

    @Override
    public ConnectionState getState() {
        return state;
    }
}
