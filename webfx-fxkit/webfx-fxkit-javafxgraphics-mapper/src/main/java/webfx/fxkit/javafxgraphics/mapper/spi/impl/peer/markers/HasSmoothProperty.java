package webfx.fxkit.javafxgraphics.mapper.spi.impl.peer.markers;


import javafx.beans.property.Property;

/**
 * @author Bruno Salmon
 */
public interface HasSmoothProperty {

    Property<Boolean> smoothProperty();
    default void setSmooth(Boolean smooth) { smoothProperty().setValue(smooth); }
    default Boolean isSmooth() { return smoothProperty().getValue(); }

}