package webfx.fxkit.gwt.mapper.html.peer;

import com.google.gwt.core.client.JavaScriptObject;
import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;
import webfx.fxkits.core.mapper.spi.SceneRequester;
import webfx.platforms.core.services.log.Logger;
import webfx.platforms.core.util.Objects;
import webfx.platforms.core.util.Strings;
import webfx.fxkit.gwt.mapper.util.HtmlUtil;
import webfx.fxkits.extra.control.HtmlTextEditor;
import webfx.fxkits.extra.mapper.spi.peer.impl.HtmlTextEditorPeerBase;
import webfx.fxkits.extra.mapper.spi.peer.impl.HtmlTextEditorPeerMixin;

/**
 * @author Bruno Salmon
 */
public final class HtmlHtmlTextEditorPeer
        <N extends HtmlTextEditor, NB extends HtmlTextEditorPeerBase<N, NB, NM>, NM extends HtmlTextEditorPeerMixin<N, NB, NM>>
        extends HtmlRegionPeer<N, NB, NM>
        implements HtmlTextEditorPeerMixin<N, NB, NM>, HtmlLayoutMeasurable {

    private static final String ckEditorUrl = "https://cdn.ckeditor.com/4.7.2/full/ckeditor.js";

    private final HTMLDivElement div = HtmlUtil.createDivElement();
    private JavaScriptObject ckEditor;

    public HtmlHtmlTextEditorPeer() {
        this((NB) new HtmlTextEditorPeerBase());
    }

    HtmlHtmlTextEditorPeer(NB base) {
        super(base, HtmlUtil.createDivElement());
        HtmlUtil.setChild(getElement(), div);
    }

    @Override
    public void bind(N node, SceneRequester sceneRequester) {
        super.bind(node, sceneRequester);
        HtmlUtil.loadScript(ckEditorUrl).setHandler(ar -> recreateCKEditorIfRequired());
    }

    @Override
    public void updateText(String text) {
        if (ckEditor != null && !recreateCKEditorIfRequired() && !Objects.areEquals(text, callCKEditorGetData(ckEditor)))
            callCKEditorSetData(ckEditor, Strings.toSafeString(text));
    }

    @Override
    public void updateWidth(Number width) {
        super.updateWidth(width);
        if (ckEditor != null && !recreateCKEditorIfRequired())
            callCKEditorResize(ckEditor, width.doubleValue(), getNode().getHeight());
    }

    @Override
    public void updateHeight(Number height) {
        super.updateHeight(height);
        if (ckEditor != null && !recreateCKEditorIfRequired())
            callCKEditorResize(ckEditor, getNode().getWidth(), height.doubleValue());
    }

    private boolean recreateCKEditorIfRequired() {
        if (ckEditor != null && !Strings.isEmpty(getCKEditorInnerHTML(ckEditor)))
            return false;
        Logger.log("Recreating CKEditor");
        if (ckEditor != null)
            callCKEditorDestroy(ckEditor);
        else
            HtmlUtil.onNodeInsertedIntoDocument(getElement(), this::recreateCKEditorIfRequired);
        N node = getNode();
        ckEditor = callCKEditorReplace(div, node.getWidth(), node.getHeight(), this);
        resyncEditorFromNodeText();
        return true;
    }

    private static native String getCKEditorInnerHTML(JavaScriptObject ckEditor) /*-{
        var container = ckEditor.container;
        if (container) {
            var contentDocument = container.getElementsByTag('iframe').$[0].contentDocument;
            if (contentDocument)
                return contentDocument.body.innerHTML;
        }
        return null;
    }-*/;

    private static native JavaScriptObject callCKEditorReplace(Element textArea, double width, double height, HtmlHtmlTextEditorPeer javaPeer) /*-{
        return $wnd.CKEDITOR.replace(textArea, {resize_enabled: false, on: {'instanceReady': function(e) {e.editor.resize(width, height); javaPeer.@HtmlHtmlTextEditorPeer::resyncEditorFromNodeText()(); e.editor.on('change', javaPeer.@HtmlHtmlTextEditorPeer::resyncNodeTextFromEditor().bind(javaPeer));}}});
    }-*/;

    private static native void callCKEditorSetData(JavaScriptObject ckEditor, String data) /*-{
        //$wnd.console.log('Calling setData() with data = ' + data);
        ckEditor.setData(data);
    }-*/;

    private static native String callCKEditorGetData(JavaScriptObject ckEditor) /*-{
        return ckEditor.getData();
    }-*/;

    private static native void callCKEditorResize(JavaScriptObject ckEditor, double width, double height) /*-{
        //$wnd.console.log('Calling resize() with width = ' + width + ', height = ' + height);
        ckEditor.resize(width, height);
    }-*/;

    private static native void callCKEditorDestroy(JavaScriptObject ckEditor) /*-{
        ckEditor.destroy();
    }-*/;

    private void resyncNodeTextFromEditor() {
        getNode().setText(callCKEditorGetData(ckEditor));
    }

    private void resyncEditorFromNodeText() {
        callCKEditorSetData(ckEditor, getNode().getText());
    }


}