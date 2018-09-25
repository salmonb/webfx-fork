package webfx.fxkit.gwt.mapper.html.peer;

import elemental2.dom.HTMLElement;
import emul.javafx.scene.control.DatePicker;
import webfx.fxkits.core.mapper.spi.impl.peer.TextFieldPeerBase;
import webfx.fxkits.core.mapper.spi.impl.peer.TextFieldPeerMixin;

/**
 * @author Bruno Salmon
 */
public class HtmlDatePickerPeer
        <N extends DatePicker, NB extends TextFieldPeerBase<N, NB, NM>, NM extends TextFieldPeerMixin<N, NB, NM>>

        extends HtmlTextFieldPeer<N, NB, NM> {

    public HtmlDatePickerPeer() {
        super();
    }

    public HtmlDatePickerPeer(NB base, HTMLElement element) {
        super(base, element);
    }

}