package naga.fxdata.spi.peer.base;

import naga.fxdata.chart.PieChart;

/**
 * @author Bruno Salmon
 */
public class PieChartPeerBase
        <C, N extends PieChart, NB extends PieChartPeerBase<C, N, NB, NM>, NM extends PieChartPeerMixin<C, N, NB, NM>>

        extends ChartPeerBase<C, N, NB, NM> {
}
