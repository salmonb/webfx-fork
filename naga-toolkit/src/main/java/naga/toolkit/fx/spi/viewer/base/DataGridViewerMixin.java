package naga.toolkit.fx.spi.viewer.base;

import naga.toolkit.fx.ext.control.DataGrid;
import naga.toolkit.display.DisplayColumn;
import naga.toolkit.fx.scene.Node;
import naga.toolkit.fx.spi.viewer.DataGridViewer;

/**
 * @author Bruno Salmon
 */
public interface DataGridViewerMixin<C>
        extends DataGridViewer,
        SelectableDisplayResultSetControlViewerMixin<C, DataGrid, DataGridViewerBase<C>, DataGridViewerMixin<C>> {

    void setUpGridColumn(int gridColumnIndex, int rsColumnIndex, DisplayColumn displayColumn);

    void setCellContent(C cell, Node content, DisplayColumn displayColumn);

}