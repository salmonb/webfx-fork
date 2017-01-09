package naga.fx.spi.peer.base;

import naga.fx.scene.control.Control;

/**
 * @author Bruno Salmon
 */
public interface ControlPeerMixin
        <N extends Control, NB extends ControlPeerBase<N, NB, NM>, NM extends ControlPeerMixin<N, NB, NM>>

        extends RegionPeerMixin<N, NB, NM> {
}
