package webfx.fxkits.extra.cell.collator;

import emul.javafx.beans.value.ObservableValue;
import emul.javafx.collections.ListChangeListener;
import emul.javafx.collections.ObservableList;
import emul.javafx.geometry.HPos;
import emul.javafx.geometry.Insets;
import emul.javafx.geometry.VPos;
import emul.javafx.scene.Cursor;
import emul.javafx.scene.Node;
import emul.javafx.scene.Scene;
import emul.javafx.scene.control.SkinBase;
import emul.javafx.scene.effect.BlendMode;
import emul.javafx.scene.effect.Effect;
import emul.javafx.scene.layout.Background;
import emul.javafx.scene.layout.Border;
import emul.javafx.scene.layout.BorderPane;
import emul.javafx.scene.transform.Transform;
import webfx.fxkits.core.mapper.spi.NodePeer;
import webfx.fxkits.core.mapper.spi.SceneRequester;
import webfx.fxkits.core.util.properties.ObservableLists;
import webfx.fxkits.extra.cell.renderer.ArrayRenderer;
import webfx.fxkits.extra.cell.renderer.ValueRenderer;
import webfx.fxkits.extra.control.DataGrid;
import webfx.fxkits.extra.displaydata.DisplayColumn;
import webfx.fxkits.extra.displaydata.DisplayResult;
import webfx.fxkits.extra.displaydata.DisplaySelection;
import webfx.fxkits.extra.displaydata.SelectionMode;
import webfx.fxkits.extra.mapper.spi.peer.impl.DataGridPeerBase;
import webfx.fxkits.extra.mapper.spi.peer.impl.DataGridPeerMixin;

import java.util.List;

/**
 * @author Bruno Salmon
 */
public class GridCollator extends DataGrid {

    private final NodeCollator columnCollator;
    private final NodeCollator rowCollator;
    private final BorderPane container;

    public GridCollator(String columnCollator, String rowCollator) {
        this(NodeCollatorRegistry.getCollator(columnCollator), NodeCollatorRegistry.getCollator(rowCollator));
    }

    public GridCollator(NodeCollator columnCollator, NodeCollator rowCollator) {
        this.columnCollator = columnCollator;
        this.rowCollator = rowCollator;
        container = new BorderPane();
        setMaxWidth(Double.MAX_VALUE);
        ObservableLists.setAllNonNulls(getChildren(), container);
        setSkin(new SkinBase(this) {}); // So the peer displays (skin) children
    }

    @Override
    public NodePeer getNodePeer() {
        NodePeer nodePeer = super.getNodePeer();
        if (nodePeer == null) {
            Scene scene = getScene();
            container.setScene(scene);
            NodePeer containerPeer = container.getOrCreateAndBindNodePeer();
            setNodePeer(nodePeer = containerPeer);
            gridCollatorPeer = new GridCollatorPeer();
            gridCollatorPeer.bind(this, new SceneRequester() {

                @Override
                public void requestNodePeerPropertyUpdate(Node node, ObservableValue changedProperty) {
                    gridCollatorPeer.updateProperty(changedProperty);
                }

                @Override
                public void requestNodePeerListUpdate(Node node, ObservableList changedList, ListChangeListener.Change change) {
                    gridCollatorPeer.updateList(changedList, change);
                }
            });
        }
        return nodePeer;
    }

    @Override
    protected void layoutChildren() {
        layoutInArea(container, getLayoutX(), getLayoutY(), getWidth(), getHeight(), 0, HPos.LEFT, VPos.TOP);
        container.layout();
    }

    private GridCollatorPeer gridCollatorPeer;

    private class GridCollatorPeer
            <N extends DataGrid, NB extends DataGridPeerBase<GridCollator, N, NB, NM>, NM extends DataGridPeerMixin<GridCollator, N, NB, NM>>

            extends DataGridPeerBase<GridCollator, N, NB, NM>
            implements DataGridPeerMixin<GridCollator, N, NB, NM> {

        private ValueRenderer[] renderers;
        private int[] rsColumnIndexes;

        {
            setMixin((NM) this);
        }

        @Override
        public void requestFocus() {
        }

        @Override
        public void updateSelectionMode(SelectionMode mode) {
        }

        @Override
        public void updateDisplaySelection(DisplaySelection selection) {
        }

        @Override
        public void updateHeaderVisible(boolean headerVisible) {
        }

        @Override
        public void updateFullHeight(boolean fullHeight) {
        }

        @Override
        public void updateBackground(Background background) {
            container.setBackground(background);
        }

        @Override
        public void updateBorder(Border border) {
            container.setBorder(border);
        }

        @Override
        public void updatePadding(Insets padding) {
            container.setPadding(padding);
        }

        @Override
        public void updateCursor(Cursor cursor) {
        }

        @Override
        public void updateResult(DisplayResult rs) {
            if (rs == null)
                return;
            int columnCount = rs.getColumnCount();
            renderers = new ValueRenderer[columnCount];
            rsColumnIndexes = new int[columnCount];
            fillGrid(rs);
            int rowCount = rs.getRowCount();
            Node[] rowNodes = new Node[rowCount];
            Object[] columnValues = new Object[getGridColumnCount()];
            for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                for (int gridColumnIndex = 0; gridColumnIndex < getGridColumnCount(); gridColumnIndex++)
                    columnValues[gridColumnIndex] = rs.getValue(rowIndex, rsColumnIndexes[gridColumnIndex]);
                rowNodes[rowIndex] = ArrayRenderer.renderValue(columnValues, renderers, columnCollator);
            }
            Node finalNode = rowCollator.collateNodes(rowNodes);
            container.setTop(finalNode);
            layoutChildren();
        }

        @Override
        public void setUpGridColumn(int gridColumnIndex, int rsColumnIndex, DisplayColumn displayColumn) {
            renderers[gridColumnIndex] = displayColumn.getValueRenderer();
            rsColumnIndexes[gridColumnIndex] = rsColumnIndex;
        }

        @Override
        public void setCellContent(GridCollator cell, Node content, DisplayColumn displayColumn) {
            // not called
        }

        @Override
        public void updateWidth(Number width) {
        }

        @Override
        public void updateHeight(Number height) {
        }

        @Override
        public NB getNodePeerBase() {
            return (NB) this;
        }

        @Override
        public void updateMouseTransparent(Boolean mouseTransparent) {
            container.setMouseTransparent(mouseTransparent);
        }

        @Override
        public void updateVisible(Boolean visible) {
            container.setVisible(visible);
        }

        @Override
        public void updateOpacity(Double opacity) {
            container.setOpacity(opacity);
        }

        @Override
        public void updateDisabled(Boolean disabled) {
        }

        @Override
        public void updateClip(Node clip) {
            container.setClip(clip);
        }

        @Override
        public void updateBlendMode(BlendMode blendMode) {
            container.setBlendMode(blendMode);
        }

        @Override
        public void updateEffect(Effect effect) {
            container.setEffect(effect);
        }

        @Override
        public void updateLayoutX(Number layoutX) {
        }

        @Override
        public void updateLayoutY(Number layoutY) {
        }

        @Override
        public void updateTransforms(List<Transform> transforms, ListChangeListener.Change<Transform> change) {
            container.getTransforms().setAll(transforms);
        }

        @Override
        public void updateLocalToParentTransforms(List<Transform> localToParentTransforms) {
        }

        @Override
        public void updateStyleClass(List<String> styleClass, ListChangeListener.Change<String> change) {
            container.getStyleClass().setAll(styleClass);
        }
    }
}
