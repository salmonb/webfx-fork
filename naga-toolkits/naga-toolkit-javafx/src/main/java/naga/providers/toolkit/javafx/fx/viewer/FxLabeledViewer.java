package naga.providers.toolkit.javafx.fx.viewer;

import naga.providers.toolkit.javafx.util.FxFonts;
import naga.toolkit.fx.geometry.Pos;
import naga.toolkit.fx.scene.Node;
import naga.toolkit.fx.scene.control.Labeled;
import naga.toolkit.fx.scene.text.Font;
import naga.toolkit.fx.scene.text.TextAlignment;
import naga.toolkit.fx.spi.viewer.base.LabeledViewerBase;
import naga.toolkit.fx.spi.viewer.base.LabeledViewerMixin;

/**
 * @author Bruno Salmon
 */
abstract class FxLabeledViewer
        <FxN extends javafx.scene.control.Labeled, N extends Labeled, NB extends LabeledViewerBase<N, NB, NM>, NM extends LabeledViewerMixin<N, NB, NM>>

        extends FxControlViewer<FxN, N, NB, NM>
        implements LabeledViewerMixin<N, NB, NM> {

    FxLabeledViewer(NB base) {
        super(base);
    }

    @Override
    public void updateText(String text) {
        getFxNode().setText(text);
    }

    @Override
    public void updateGraphic(Node graphic) {
        getFxNode().setGraphic(toFxNode(graphic));
    }

    @Override
    public void updateFont(Font font) {
        if (font != null)
            getFxNode().setFont(FxFonts.toFxFont(font));
    }

    @Override
    public void updateTextAlignment(TextAlignment textAlignment) {
        getFxNode().setTextAlignment(toFxTextAlignment(textAlignment));
    }

    private static javafx.geometry.Pos toFxPos(Pos pos) {
        if (pos != null)
            switch (pos) {
                case TOP_LEFT: return javafx.geometry.Pos.TOP_LEFT;
                case TOP_CENTER: return javafx.geometry.Pos.TOP_CENTER;
                case TOP_RIGHT: return javafx.geometry.Pos.TOP_RIGHT;
                case CENTER_LEFT: return javafx.geometry.Pos.CENTER_RIGHT;
                case CENTER: return javafx.geometry.Pos.CENTER;
                case CENTER_RIGHT: return javafx.geometry.Pos.CENTER_RIGHT;
                case BASELINE_LEFT: return javafx.geometry.Pos.BASELINE_LEFT;
                case BASELINE_CENTER: return javafx.geometry.Pos.BASELINE_CENTER;
                case BASELINE_RIGHT: return javafx.geometry.Pos.BASELINE_RIGHT;
                case BOTTOM_LEFT: return javafx.geometry.Pos.BOTTOM_LEFT;
                case BOTTOM_CENTER: return javafx.geometry.Pos.BOTTOM_CENTER;
                case BOTTOM_RIGHT: return javafx.geometry.Pos.BOTTOM_RIGHT;
            }
        return null;
    }

    private static javafx.scene.text.TextAlignment toFxTextAlignment(TextAlignment textAlignment) {
        if (textAlignment != null)
            switch (textAlignment) {
                case LEFT: return javafx.scene.text.TextAlignment.LEFT;
                case CENTER: return javafx.scene.text.TextAlignment.CENTER;
                case RIGHT: return javafx.scene.text.TextAlignment.RIGHT;
                case JUSTIFY: return javafx.scene.text.TextAlignment.JUSTIFY;
            }
        return null;
    }

}