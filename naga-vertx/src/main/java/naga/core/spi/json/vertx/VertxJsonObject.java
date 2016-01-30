package naga.core.spi.json.vertx;

import naga.core.spi.json.javaplat.listmap.MapBasedJsonObject;

import java.util.Map;

/**
 * @author Bruno Salmon
 */
final class VertxJsonObject extends MapBasedJsonObject {

    private io.vertx.core.json.JsonObject vertxObject;

    VertxJsonObject() { // super constructor will call recreateEmptyNativeObject() to initialize the map
    }

    VertxJsonObject(io.vertx.core.json.JsonObject vertxObject) {
        this.vertxObject = vertxObject;
    }

    @Override
    protected Object wrap(Object value) {
        if (value instanceof io.vertx.core.json.JsonObject)
            return new VertxJsonObject((io.vertx.core.json.JsonObject) value);
        return super.wrap(value);
    }

    @Override
    public Map<String, Object> getMap() {
        return vertxObject.getMap();
    }

    @Override
    public Object getNativeObject() {
        return vertxObject;
    }

    @Override
    protected void recreateEmptyNativeObject() {
        vertxObject = new io.vertx.core.json.JsonObject();
    }

    @Override
    protected void deepCopyNativeObject() {
        vertxObject = vertxObject.copy();
    }

    @Override
    public String toJsonString() {
        return vertxObject.encode();
    }
}

