package webfx.fxkit.javafx.mapper.peer;

import webfx.fxkits.extra.chart.AreaChart;
import webfx.fxkits.extra.mapper.spi.peer.impl.AreaChartPeerMixin;
import webfx.fxkits.extra.mapper.spi.peer.impl.AreaChartPeerBase;

/**
 * @author Bruno Salmon
 */
public final class FxAreaChartPeer
        <FxN extends javafx.scene.chart.AreaChart, N extends AreaChart, NB extends AreaChartPeerBase<FxN, N, NB, NM>, NM extends AreaChartPeerMixin<FxN, N, NB, NM>>

        extends FxXYChartPeer<FxN, N, NB, NM>
        implements AreaChartPeerMixin<FxN, N, NB, NM> {

    public FxAreaChartPeer() {
        super((NB) new AreaChartPeerBase());
    }

    @Override
    protected FxN createFxNode() {
        javafx.scene.chart.AreaChart<Number, Number> areaChart = new javafx.scene.chart.AreaChart<>(createNumberAxis(), createNumberAxis());
        areaChart.setCreateSymbols(false);
        return (FxN) areaChart;
    }
}
