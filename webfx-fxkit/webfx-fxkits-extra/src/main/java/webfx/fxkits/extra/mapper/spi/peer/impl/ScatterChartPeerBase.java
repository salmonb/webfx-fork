package webfx.fxkits.extra.mapper.spi.peer.impl;

import webfx.fxkits.extra.chart.ScatterChart;

/**
 * @author Bruno Salmon
 */
public final class ScatterChartPeerBase
        <C, N extends ScatterChart, NB extends ScatterChartPeerBase<C, N, NB, NM>, NM extends ScatterChartPeerMixin<C, N, NB, NM>>

        extends ChartPeerBase<C, N, NB, NM> {
}
