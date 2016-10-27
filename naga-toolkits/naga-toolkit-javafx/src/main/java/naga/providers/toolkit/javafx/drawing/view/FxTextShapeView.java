package naga.providers.toolkit.javafx.drawing.view;

import javafx.scene.text.Text;
import naga.providers.toolkit.javafx.util.FxFonts;
import naga.toolkit.drawing.shapes.TextShape;
import naga.toolkit.drawing.spi.DrawingNotifier;
import naga.toolkit.drawing.spi.view.TextShapeView;
import naga.toolkit.properties.conversion.ConvertedProperty;

/**
 * @author Bruno Salmon
 */
public class FxTextShapeView extends FxShapeViewImpl<TextShape, Text> implements TextShapeView {

    @Override
    public void bind(TextShape shape, DrawingNotifier drawingNotifier) {
        setAndBindCommonShapeProperties(shape, new Text());
        fxShape.xProperty().bind(shape.xProperty());
        fxShape.yProperty().bind(shape.yProperty());
        fxShape.textProperty().bind(shape.textProperty());
        fxShape.fontProperty().bind(new ConvertedProperty<>(shape.fontProperty(), FxFonts::toFxFont));
    }

}
