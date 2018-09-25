package webfx.fxkits.extra.mapper.spi.peer.impl;

import webfx.fxkits.extra.chart.PieChart;

/**
 * @author Bruno Salmon
 */
public final class PieChartPeerBase
        <C, N extends PieChart, NB extends PieChartPeerBase<C, N, NB, NM>, NM extends PieChartPeerMixin<C, N, NB, NM>>

        extends ChartPeerBase<C, N, NB, NM> {
}
