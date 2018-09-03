package webfx.framework.operations.route;

import webfx.framework.operation.HasOperationExecutor;
import webfx.framework.router.auth.authz.RouteRequest;
import webfx.platforms.core.client.url.history.History;
import webfx.platforms.core.util.async.Future;

/**
 * @author Bruno Salmon
 */
public abstract class RouteRequestBase<THIS extends RouteRequestBase<THIS>>
        extends RouteRequest
        implements HasOperationExecutor<THIS, Void> {

    private History history;

    protected RouteRequestBase(History history) {
        this(null, history);
    }

    protected RouteRequestBase(String routePath, History history) {
        super(routePath);
        this.history = history;
    }

    public History getHistory() {
        return history;
    }

    public THIS setHistory(History history) {
        this.history = history;
        return (THIS) this;
    }

    /* Execute the request without checking at this stage if it is authorized or not, because this is actually the
     * router job to do this checking and to redirect to the login or unauthorized page if not authorized */
    public Future<Void> execute() {
        return getOperationExecutor().apply((THIS) this);
    }
}
