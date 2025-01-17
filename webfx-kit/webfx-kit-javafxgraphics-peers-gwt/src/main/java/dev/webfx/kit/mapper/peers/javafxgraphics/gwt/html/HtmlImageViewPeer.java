package dev.webfx.kit.mapper.peers.javafxgraphics.gwt.html;

import dev.webfx.kit.mapper.peers.javafxgraphics.base.ImageViewPeerBase;
import dev.webfx.kit.mapper.peers.javafxgraphics.base.ImageViewPeerMixin;
import dev.webfx.kit.mapper.peers.javafxgraphics.emul_coupling.HasSizeChangedCallback;
import dev.webfx.kit.mapper.peers.javafxgraphics.gwt.util.HtmlPaints;
import dev.webfx.kit.mapper.peers.javafxgraphics.gwt.util.HtmlUtil;
import dev.webfx.kit.mapper.peers.javafxgraphics.markers.HasTextFillProperty;
import dev.webfx.platform.resource.Resource;
import dev.webfx.platform.util.Numbers;
import dev.webfx.platform.util.Strings;
import elemental2.dom.*;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * @author Bruno Salmon
 */
public final class HtmlImageViewPeer
        <N extends ImageView, NB extends ImageViewPeerBase<N, NB, NM>, NM extends ImageViewPeerMixin<N, NB, NM>>

        extends HtmlNodePeer<N, NB, NM>
        implements ImageViewPeerMixin<N, NB, NM>, HasSizeChangedCallback {

    private final static GraphicsContext BACKGROUND_LOADING_CONTEXT = new Canvas().getGraphicsContext2D();

    //private Double loadedWidth, loadedHeight;

    public HtmlImageViewPeer() {
        this((NB) new ImageViewPeerBase(), HtmlUtil.createImageElement());
    }

    public HtmlImageViewPeer(NB base, HTMLElement element) {
        super(base, element);
        HTMLElement container = HtmlUtil.createElement("fx-image");
        HtmlUtil.setChild(container, element);
        setContainer(container);
        makeContainerInvisible();
        HTMLImageElement imageElement = (HTMLImageElement) getElement();
        imageElement.onload = evt -> {
            onLoad();
            return null;
        };
    }

    private Runnable sizeChangedCallback;

    @Override
    public void setSizeChangedCallback(Runnable sizeChangedCallback) {
        this.sizeChangedCallback = sizeChangedCallback;
    }

    @Override
    public void updateImage(Image image) {
        // Trying to inline svg images when possible to allow css rules such as svg {fill: currentColor} which is useful
        // to have the same color for the image and the text (in a button for example).
        //loadedWidth = loadedHeight = null;
        String imageUrl = image == null ? null : image.getUrl();
        if (tryInlineSvg(imageUrl))
            onLoad();
        else {
            setElementAttribute("src", imageUrl);
            // Temporary filling alt with imageUrl to avoid downgrade in Lighthouse TODO: map this to accessible text
            setElementAttribute("alt", imageUrl);
            // But removing the alt text and hiding the image if the link is broken (to align with JavaFX behaviour which doesn't display such things)
            setElementAttribute("onerror", "this.style.display='none'; this.alt=''");
            // Special case of a canvas image (ex: the WebFX WritableImage emulation code stored the image in a canvas)
            HTMLCanvasElement canvasElement = HtmlCanvasPeer.getImageCanvasElement(image);
            if (canvasElement != null) {
                // We will replace the image with a canvas. First getting the canvas peer and element
                // If the canvas has already been inserted into the DOM (this can happen because the same image can be used in different ImageView)
                if (canvasElement.parentNode != null) // In that case, we need to make a copy of the canvas
                    canvasElement = HtmlCanvasPeer.copyCanvas(canvasElement);
                // We finally replace the node with the canvas element
                HtmlUtil.setChild(getContainer(), canvasElement);
            } else if (image != null && image.isBackgroundLoading())
                BACKGROUND_LOADING_CONTEXT.drawImage(image, 0, 0); // This forces the browser to load the image immediately in the background
        }
    }

    private void onLoad() {
        N node = getNode();
        Image image = node.getImage();
        if (image != null) {
            HTMLElement element = getElement();
            if (element instanceof HTMLImageElement) {
                onHTMLImageLoaded((HTMLImageElement) element, image);
                // Reestablishing the image width & height changed by the previous call when a fitWidth/fitHeight is set
                if (node.getFitWidth() > 0)
                    image.setWidth(node.getFitWidth());
                if (node.getFitHeight() > 0)
                    image.setHeight(node.getFitHeight());
            }
        }
        if (sizeChangedCallback != null) // && loadedWidth == null && loadedHeight == null && Numbers.doubleValue(node.getFitWidth()) == 0 && Numbers.doubleValue(node.getFitHeight()) == 0)
            sizeChangedCallback.run();
    }

    public static void onHTMLImageLoaded(HTMLImageElement imageElement, Image image) {
        double requestedWidth = image.getRequestedWidth();
        image.setWidth(requestedWidth  > 0 ? requestedWidth  : (double) imageElement.naturalWidth);
        double requestedHeight = image.getRequestedHeight();
        image.setHeight(requestedHeight > 0 ? requestedHeight : (double) imageElement.naturalHeight);
        image.setProgress(1);
    }

    @Override
    public void updateFitWidth(Double fitWidth) {
        setElementAttribute("width", Numbers.doubleValue(fitWidth) <= 0 ? null : toPx(fitWidth));
    }

    @Override
    public void updateFitHeight(Double fitHeight) {
        setElementAttribute("height", Numbers.doubleValue(fitHeight) <= 0 ? null : toPx(fitHeight));
    }

    @Override
    public void updatePreserveRatio(Boolean preserveRatio) {
        setElementStyleAttribute("object-fit", Boolean.TRUE.equals(preserveRatio) ? "contain" : null);
    }

    @Override
    public void updateX(Double x) {
        setElementStyleAttribute("left", toPx(x));
    }

    @Override
    public void updateY(Double y) {
        setElementStyleAttribute("top", toPx(y));
    }

    boolean tryInlineSvg(String url) {
        // First checking the extension is svg
        if (Strings.endsWith(url, ".svg")) {
            // We do inline svg only for images that have been included in the resources
            String svgFile = Resource.getText(url);
            if (svgFile != null) { // Yes the images is in the resources so we have the content already
                // Removing all what is before the svg tag (ex: <?xml ...?>)
                int svgTagIndex = svgFile.indexOf("<svg");
                if (svgTagIndex != -1)
                    svgFile = svgFile.substring(svgTagIndex);
                // Creating the svg element from the file content
                Element svgNode = HtmlUtil.createNodeFromHtml(svgFile);
                // Setting width and height if defined
                ImageView imageView = getNode();
                double fitWidth = imageView.getFitWidth();
                if (fitWidth > 0)
                    svgNode.setAttribute("width", fitWidth);
                double fitHeight = imageView.getFitHeight();
                if (fitHeight > 0)
                    svgNode.setAttribute("height", fitHeight);
                // Applying the same color (in case svg is monochrome) as the text if in a label
                Parent parent = getNode().getParent();
                if (parent instanceof HasTextFillProperty) // Ex: Labeled (removed instanceof Labeled because it was introducing a dependency with javafx-controls)
                    applyTextFillToSvg(svgNode, HtmlPaints.toHtmlCssPaint(((HasTextFillProperty) parent).getTextFill()));
                // Switching the node from image to svg
                HtmlUtil.setChild(getContainer(), svgNode);
                return true;
            }
        }
        return false;
    }

    public static void applyTextFillToSvg(Element svgNode, String fill) {
        if ("SVG".equalsIgnoreCase(svgNode.tagName))
            setElementAttribute(svgNode, "fill", fill);
    }

    @Override
    public void updateEffect(Effect effect) {
        if (effect instanceof DropShadow)
            setElementStyleAttribute("filter", toFilter(effect));
        else
            super.updateEffect(effect);
    }

    // Overriding HtmlLayoutMeasurer for the inline svg case -> 2 problems in this case:
    // 1) the element to measure is the container (svg node) and not the element (empty img)
    // 2) there is no SvgLayoutMeasurer at the moment (should be based on getBBox)
    // For now, we run with the following code that at least works when fitWith and fitHeight are set

/*
    @Override
    public Bounds getLayoutBounds() {
        return new BoundingBox(0, 0, 0, prefWidth(-1), prefHeight(-1), 0);
    }

    @Override
    public double minWidth(double height) {
        return prefWidth(height);
    }

    @Override
    public double maxWidth(double height) {
        return prefWidth(height);
    }

    @Override
    public double minHeight(double width) {
        return prefHeight(width);
    }

    @Override
    public double maxHeight(double width) {
        return prefHeight(width);
    }

    @Override
    public double prefWidth(double height) {
        double fitWidth = getNode().getFitWidth();
        return fitWidth > 0 ? fitWidth : measureWidth(height);
    }

    @Override
    public double prefHeight(double width) {
        double fitHeight = getNode().getFitHeight();
        return fitHeight > 0 ? fitHeight : measureHeight(width);
    }

    @Override
    public double measureWidth(double height) {
        if (loadedWidth != null)
            return loadedWidth;
        HTMLElement element = getElement();
        double width;
        if (element instanceof HTMLImageElement)
            width = ((HTMLImageElement) element).naturalWidth;
        else
            width = sizeAndMeasure(height, true);
        if (width > 0) {
            loadedWidth = width;
            if (sizeChangedCallback != null && loadedHeight == null)
                sizeChangedCallback.run();
        }
        return width;
    }

    @Override
    public double measureHeight(double width) {
        if (loadedHeight != null)
            return loadedHeight;
        HTMLElement element = getElement();
        double height;
        if (element instanceof HTMLImageElement)
            height = ((HTMLImageElement) element).naturalHeight;
        else
            height = sizeAndMeasure(width, false);
        if (height > 0) {
            loadedHeight = height;
            if (sizeChangedCallback != null && loadedWidth == null)
                sizeChangedCallback.run();
        }
        return height;
    }
*/
}
