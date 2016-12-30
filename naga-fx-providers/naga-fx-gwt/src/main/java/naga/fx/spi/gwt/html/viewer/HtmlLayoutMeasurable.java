package naga.fx.spi.gwt.html.viewer;

import elemental2.CSSStyleDeclaration;
import elemental2.HTMLElement;
import naga.fx.geometry.BoundingBox;
import naga.fx.geometry.Bounds;
import naga.fx.scene.LayoutMeasurable;

/**
 * @author Bruno Salmon
 */
public interface HtmlLayoutMeasurable extends LayoutMeasurable {

    HTMLElement getElement();

    default Bounds getLayoutBounds() {
        HTMLElement e = getElement();
        return new BoundingBox(0, 0, 0, measure(e, true), measure(e, false), 0);
    }

    default double minWidth(double height) {
        return measureWidth(height);
    }

    default double maxWidth(double height) {
        return measureWidth(height);
    }

    default double minHeight(double width) {
        return measureHeight(width);
    }

    default double maxHeight(double width) {
        return measureHeight(width);
    }

    default double prefWidth(double height) {
        return measureWidth(height);
    }

    default double prefHeight(double width) {
        return measureHeight(width);
    }

    default double measureWidth(double height) {
        return sizeAndMeasure(height, true);
    }

    default double measureHeight(double width) {
        return sizeAndMeasure(width, false);
    }

    default double sizeAndMeasure(double value, boolean width) {
        HTMLElement e = getElement();
        CSSStyleDeclaration style = e.style;
        Object styleWidth = style.width;
        Object styleHeight = style.height;
        if (width) {
            style.width = null;
            style.height = value >= 0 ? HtmlNodeViewer.toPx(value) : null;
        } else {
            style.width = value >= 0 ? HtmlNodeViewer.toPx(value) : null;
            style.height = null;
        }
        double result = measure(e, width);
        style.width = styleWidth;
        style.height = styleHeight;
        return result;
    }

    default double measure(HTMLElement e, boolean width) {
        return width ? e.offsetWidth : e.offsetHeight;
    }
}