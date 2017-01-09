package naga.fx.spi.javafx.peer;

import naga.fxdata.chart.ScatterChart;
import naga.fxdata.spi.peer.base.ScatterChartPeerBase;
import naga.fxdata.spi.peer.base.ScatterChartPeerMixin;

/**
 * @author Bruno Salmon
 */
public class FxScatterChartPeer
        <FxN extends javafx.scene.chart.ScatterChart, N extends ScatterChart, NB extends ScatterChartPeerBase<FxN, N, NB, NM>, NM extends ScatterChartPeerMixin<FxN, N, NB, NM>>

        extends FxXYChartPeer<FxN, N, NB, NM>
        implements ScatterChartPeerMixin<FxN, N, NB, NM> {

    public FxScatterChartPeer() {
        super((NB) new ScatterChartPeerBase());
    }

    @Override
    protected FxN createFxNode() {
        return (FxN) new javafx.scene.chart.ScatterChart(createNumberAxis(), createNumberAxis());
    }
}
