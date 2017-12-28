package naga.fx.spi.gwt.shared;

import elemental2.dom.Element;
import elemental2.dom.Event;
import elemental2.dom.HTMLElement;
import elemental2.dom.KeyboardEvent;
import emul.com.sun.javafx.event.EventUtil;
import emul.javafx.application.Platform;
import emul.javafx.collections.ListChangeListener;
import emul.javafx.event.EventType;
import emul.javafx.scene.Cursor;
import emul.javafx.scene.LayoutMeasurable;
import emul.javafx.scene.Node;
import emul.javafx.scene.Scene;
import emul.javafx.scene.effect.BlendMode;
import emul.javafx.scene.effect.Effect;
import emul.javafx.scene.input.KeyCode;
import emul.javafx.scene.input.KeyEvent;
import emul.javafx.scene.input.MouseButton;
import emul.javafx.scene.input.MouseEvent;
import emul.javafx.scene.text.Font;
import emul.javafx.scene.text.FontPosture;
import emul.javafx.scene.transform.Transform;
import naga.fx.scene.SceneRequester;
import naga.fx.spi.Toolkit;
import naga.fx.spi.gwt.svg.peer.SvgNodePeer;
import naga.fx.spi.gwt.util.DomType;
import naga.fx.spi.gwt.util.HtmlTransforms;
import naga.fx.spi.gwt.util.HtmlUtil;
import naga.fx.spi.gwt.util.SvgTransforms;
import naga.fx.spi.peer.base.NodePeerBase;
import naga.fx.spi.peer.base.NodePeerImpl;
import naga.fx.spi.peer.base.NodePeerMixin;
import naga.uischeduler.AnimationFramePass;
import naga.util.Booleans;
import naga.util.Strings;
import naga.util.collection.Collections;

import java.util.List;
import java.util.Objects;

/**
 * @author Bruno Salmon
 */
public abstract class HtmlSvgNodePeer
        <E extends Element, N extends Node, NB extends NodePeerBase<N, NB, NM>, NM extends NodePeerMixin<N, NB, NM>>
        extends NodePeerImpl<N, NB, NM> {

    private final E element;
    private Element container;
    private Element childrenContainer;
    protected DomType containerType;

    public HtmlSvgNodePeer(NB base, E element) {
        super(base);
        this.element = element;
        setContainer(element);
        setChildrenContainer(element);
    }

    public E getElement() {
        return element;
    }

    public void setContainer(Element container) {
        this.container = container;
        containerType = "SVG".equalsIgnoreCase(container.tagName) || this instanceof SvgNodePeer ? DomType.SVG : DomType.HTML;
    }

    public Element getContainer() {
        return container;
    }

    public Element getChildrenContainer() {
        return childrenContainer;
    }

    public void setChildrenContainer(Element childrenContainer) {
        this.childrenContainer = childrenContainer;
    }

    @Override
    public void bind(N node, SceneRequester sceneRequester) {
        super.bind(node, sceneRequester);
        installMouseListeners();
        installFocusListeners();
        installKeyboardListeners();
    }

    private void installMouseListeners() {
        registerMouseListener("mousedown");
        registerMouseListener("mouseup");
        //registerMouseListener("click"); // Not necessary as the JavaFx Scene already generates them based on the mouse pressed and released events
        registerMouseListener("mouseenter");
        registerMouseListener("mouseleave");
        registerMouseListener("mousemove");
    }

    private void registerMouseListener(String type) {
        element.addEventListener(type, e -> {
            boolean fxConsumed = passHtmlMouseEventOnToFx((elemental2.dom.MouseEvent) e, type);
            if (fxConsumed) {
                e.stopPropagation();
                e.preventDefault();
            }
        });
    }

    private boolean atLeastOneAnimationFrameOccurredSinceLastMousePressed = true;

    protected boolean passHtmlMouseEventOnToFx(elemental2.dom.MouseEvent e, String type) {
        MouseEvent fxMouseEvent = toFxMouseEvent(e, type);
        if (fxMouseEvent != null) {
            // We now need to call Scene.impl_processMouseEvent() to pass the event to the JavaFx stack
            Scene scene = getNode().getScene();
            // Also fixing a problem: mouse released and mouse pressed are sent very closely on mobiles and might be
            // treated in the same animation frame, which prevents the button pressed state (ex: a background bound to
            // the button pressedProperty) to appear before the action (which might be time consuming) is fired, so the
            // user doesn't know if the button has been successfully pressed or not during the action execution.
            if (fxMouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED && !atLeastOneAnimationFrameOccurredSinceLastMousePressed)
                Toolkit.get().scheduler().scheduleInFutureAnimationFrame(1, () -> scene.impl_processMouseEvent(fxMouseEvent), AnimationFramePass.UI_UPDATE_PASS);
            else {
                scene.impl_processMouseEvent(fxMouseEvent);
                if (fxMouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
                    atLeastOneAnimationFrameOccurredSinceLastMousePressed = false;
                    Toolkit.get().scheduler().scheduleInFutureAnimationFrame(1, () -> atLeastOneAnimationFrameOccurredSinceLastMousePressed = true, AnimationFramePass.UI_UPDATE_PASS);
                }
            }
        }
        return isFxEventConsumed(fxMouseEvent);
    }

    private boolean passOnToFx(emul.javafx.event.Event fxEvent) {
        return isFxEventConsumed(EventUtil.fireEvent(getNode(), fxEvent));
    }

    private boolean isFxEventConsumed(emul.javafx.event.Event fxEvent) {
        return fxEvent == null || fxEvent.isConsumed();
    }

        private void installFocusListeners() {
        element.onfocus = e -> {
            passHtmlFocusEventOnToFx(e);
            return null;
        };
        element.onblur = e -> {
            passHtmlBlurEventOnToFx(e);
            return null;
        };
    }

    protected void passHtmlFocusEventOnToFx(Event e) {
        N node = getNode();
        node.setFocused(true);
        node.getScene().focusOwnerProperty().setValue(node);
    }

    protected void passHtmlBlurEventOnToFx(Event e) {
        getNode().setFocused(false);
    }

    private void installKeyboardListeners() {
        registerKeyboardListener("keydown");
        registerKeyboardListener("keyup");
        registerKeyboardListener("keypress");
    }

    private void registerKeyboardListener(String type) {
        element.addEventListener(type, e -> {
            boolean fxConsumed = passHtmlKeyEventOnToFx((KeyboardEvent) e, type);
            if (fxConsumed) {
                e.stopPropagation();
                e.preventDefault();
            }
        });
    }

    protected boolean passHtmlKeyEventOnToFx(KeyboardEvent e, String type) {
        return passOnToFx(toFxKeyEvent(e, type));
    }

    @Override
    public void requestFocus() {
        // Postponing the request because as opposed to JavaFx it doesn't work if the element is not yet visible (ex: when the building ui)
        Platform.runLater(() -> getElement().focus());
    }

    @Override
    public boolean isTreeVisible() {
        if (container instanceof HTMLElement)
            return ((HTMLElement) container).offsetParent != null;
        return true;
    }

    protected boolean isStyleAttribute(String name) {
        if (containerType == DomType.HTML)
            switch (name) {
                case "pointer-events":
                case "visibility":
                case "opacity":
                case "clip-path":
                case "mix-blend-mode":
                case "filter":
                case "font-family":
                case "font-style":
                case "font-weight":
                case "font-size":
                case "transform":
                    return true;
            }
        return false;
    }

    protected void setElementStyleAttribute(String name, Object value) {
        HtmlUtil.setStyleAttribute(container, name, value);
    }

    @Override
    public void updateMouseTransparent(Boolean mouseTransparent) {
        setElementAttribute("pointer-events", mouseTransparent ? "none" : null);
    }

    @Override
    public void updateVisible(Boolean visible) {
        setElementAttribute("visibility", visible ? null : "hidden");
    }

    @Override
    public void updateOpacity(Double opacity) {
        setElementAttribute("opacity", opacity == 1d ? null : opacity);
    }

    @Override
    public void updateDisabled(Boolean disabled) {
        setElementAttribute(getElement(),"disabled", Booleans.isTrue(disabled) ? "disabled" : null);
    }

    @Override
    public void updateClip(Node clip) {
        setElementAttribute("clip-path", toClipPath(clip));
    }

    @Override
    public void updateCursor(Cursor cursor) {
        setElementStyleAttribute("cursor", toCssCursor(cursor));
    }

    protected abstract String toClipPath(Node clip);

    @Override
    public void updateBlendMode(BlendMode blendMode) {
        setElementStyleAttribute("mix-blend-mode", toSvgBlendMode(blendMode));
    }

    @Override
    public void updateEffect(Effect effect) {
        setElementAttribute("filter", effect == null ? null : toFilter(effect));
    }

    protected abstract String toFilter(Effect effect);

    @Override
    public void updateLocalToParentTransforms(List<Transform> localToParentTransforms) {
        boolean isSvg = containerType == DomType.SVG;
        setElementAttribute("transform", isSvg ? SvgTransforms.toSvgTransforms(localToParentTransforms) : HtmlTransforms.toHtmlTransforms(localToParentTransforms));
    }

    @Override
    public void updateStyleClass(List<String> styleClass, ListChangeListener.Change<String> change) {
        if (change == null)
            element.classList.add(Collections.toArray(styleClass, String[]::new));
        else while (change.next()) {
            if (change.wasRemoved())
                element.classList.remove(Collections.toArray(change.getRemoved(), String[]::new));
            if (change.wasAdded())
                element.classList.add(Collections.toArray(change.getAddedSubList(), String[]::new));
        }
    }

    private static boolean BUTTON_DOWN_STATES[] = {false, false, false, false};

    private MouseEvent toFxMouseEvent(elemental2.dom.MouseEvent me, String type) {
        MouseButton button;
        switch ((int) me.button) {
            case 0: button = MouseButton.PRIMARY; break;
            case 1: button = MouseButton.MIDDLE; break;
            case 2: button = MouseButton.SECONDARY; break;
            default: button = MouseButton.NONE;
        }
        EventType<MouseEvent> eventType;
        switch (type) {
            case "mousedown": eventType = MouseEvent.MOUSE_PRESSED; BUTTON_DOWN_STATES[button.ordinal()] = true; break;
            case "mouseup": eventType = MouseEvent.MOUSE_RELEASED; BUTTON_DOWN_STATES[button.ordinal()] = false; break;
            case "mouseenter": eventType = MouseEvent.MOUSE_ENTERED; break;
            case "mouseleave": eventType = MouseEvent.MOUSE_EXITED; break;
            case "mousemove": eventType = BUTTON_DOWN_STATES[button.ordinal()] ? MouseEvent.MOUSE_DRAGGED : MouseEvent.MOUSE_MOVED; break;
            default: return null;
        }
        return new MouseEvent(null, getNode(), eventType, me.pageX, me.pageY, me.screenX, me.screenY, button,
                1, me.shiftKey, me.ctrlKey, me.altKey, me.metaKey,
                 BUTTON_DOWN_STATES[MouseButton.PRIMARY.ordinal()],
                 BUTTON_DOWN_STATES[MouseButton.MIDDLE.ordinal()],
                 BUTTON_DOWN_STATES[MouseButton.SECONDARY.ordinal()],
                false,
                false,
                false,
                null);
    }

    private static KeyEvent toFxKeyEvent(KeyboardEvent e, String type) {
        KeyCode keyCode = toFxKeyCode(e.code);
        EventType<KeyEvent> eventType;
        if (keyCode == KeyCode.ESCAPE)
            eventType = KeyEvent.KEY_PRESSED;
        else
            switch (type) {
                case "keydown": eventType = KeyEvent.KEY_TYPED; break;
                case "keyup": eventType = KeyEvent.KEY_RELEASED; break;
                default: eventType = KeyEvent.KEY_PRESSED;
            }
        return new KeyEvent(eventType, e.char_, e.keyIdentifier, keyCode, e.shiftKey, e.ctrlKey, e.altKey, e.metaKey);
    }

    private static KeyCode toFxKeyCode(String htmlKey) {
        // See https://developer.mozilla.org/fr/docs/Web/API/KeyboardEvent/code
        switch (htmlKey) {
            case "Escape": return KeyCode.ESCAPE; // 0x0001
            case "Minus": return KeyCode.MINUS; // 0x000C
            case "Equal": return KeyCode.EQUALS; // 0x000D
            case "Backspace": return KeyCode.BACK_SPACE; // 0x000E
            case "Tab": return KeyCode.TAB; // 0x000F
            // KeyQ (0x0010) to KeyP (0x0019) -> default
            case "BracketLeft": return KeyCode.OPEN_BRACKET; // 0x001A
            case "BracketRight": return KeyCode.CLOSE_BRACKET; // 0x001B
            case "Enter": return KeyCode.ENTER; // 0x001C
            case "ControlLeft": return KeyCode.CONTROL; // 0x001D
            // KeyA (0x001E) to KeyL (0x0026) -> default
            case "Semicolon": return KeyCode.SEMICOLON; // 0x0027
            case "Quote": return KeyCode.QUOTE; // 0x0028
            case "Backquote": return KeyCode.BACK_QUOTE; // 0x0029
            case "ShiftLeft": return KeyCode.SHIFT; // 0x002A
            case "Backslash": return KeyCode.BACK_SLASH; // 0x002B
            // KeyZ (0x002C) to KeyM (0x0032) -> default
            case "Comma": return KeyCode.COMMA; // 0x0033
            case "Period": return KeyCode.PERIOD; // 0x0034
            case "Slash": return KeyCode.SLASH; // 0x0035
            case "ShiftRight" : return KeyCode.SHIFT; // 0x0036
            case "NumpadMultiply": return KeyCode.MULTIPLY; // 0x0037
            case "AltLeft": return KeyCode.ALT; // 0x0038
            case "Space": return KeyCode.SPACE; // 0x0039
            case "CapsLock": return KeyCode.CAPS; // 0x003A
            // F1 (0x003B) to F10 (0x0044) -> default
            case "Pause": return KeyCode.PAUSE; // 0x0045
            case "ScrollLock": return KeyCode.SCROLL_LOCK; // 0x0046
            // Numpad7 (0x0047) to Numpad9 (0x0049) -> default
            case "NumpadSubtract": return KeyCode.SUBTRACT; // 0x004A
            // Numpad4 (0x004B) to Numpad6 (0x004D) -> default
            case "NumpadAdd": return KeyCode.ADD; // 0x004E
            // Numpad1 (0x004F) to Numpad0 (0x0052) -> default
            case "NumpadDecimal": return KeyCode.DECIMAL; // 0x0053
            case "PrintScreen": return KeyCode.PRINTSCREEN; // 0x0054
            case "IntlBackslash": return KeyCode.BACK_SLASH; // 0x0056
            // F11 (0x0057) to F12 (0x0058) -> default
            case "NumpadEqual": return KeyCode.EQUALS; // 0x0059
            // F13 (0x0057) to F23 (0x006E) -> default
            case "KanaMode": return KeyCode.KANA; // 0x0070
            case "Convert": return KeyCode.CONVERT; // 0x0079
            case "NonConvert": return KeyCode.NONCONVERT; // 0x007B
            case "NumpadComma": return KeyCode.COMMA; // 0x007E
            case "Paste": return KeyCode.PASTE; // 0xE00A
            case "MediaTrackPrevious": return KeyCode.TRACK_PREV; // 0xE010
            case "Cut": return KeyCode.CUT; // 0xE018
            case "Copy": return KeyCode.COPY; // 0xE018
            case "MediaTrackNext": return KeyCode.TRACK_NEXT; // 0xE019
            case "NumpadEnter": return KeyCode.ENTER; // 0xE01C
            case "ControlRight": return KeyCode.CONTROL; // 0xE01D
            case "VolumeMute":
            case "AudioVolumeMute": return KeyCode.MUTE; // 0xE020
            case "MediaPlayPause": return KeyCode.PAUSE; // 0xE022
            case "MediaStop": return KeyCode.STOP; // 0xE024
            case "Eject": return KeyCode.EJECT_TOGGLE; // 0xE02D
            case "VolumeDown":
            case "AudioVolumeDown": return KeyCode.VOLUME_DOWN; // 0xE02E
            case "VolumeUp":
            case "AudioVolumeUp": return KeyCode.VOLUME_UP; // 0xE030
            case "BrowserHome": return KeyCode.HOME; // 0xE032
            case "NumpadDivide": return KeyCode.DIVIDE; // 0xE035
            case "AltRight": return KeyCode.ALT_GRAPH; // 0xE038
            case "Help": return KeyCode.HELP; // 0xE03B
            case "NumLock": return KeyCode.NUM_LOCK; // 0xE045
            case "Home": return KeyCode.HOME; // 0xE047
            case "ArrowUp": return KeyCode.UP; // 0xE048
            case "PageUp": return KeyCode.PAGE_UP; // 0xE049
            case "ArrowLeft": return KeyCode.LEFT; // 0xE04B
            case "ArrowRight": return KeyCode.RIGHT; // 0xE04D
            case "End": return KeyCode.END; // 0xE04F
            case "ArrowDown": return KeyCode.DOWN; // 0xE050
            case "PageDown": return KeyCode.PAGE_DOWN; // 0xE051
            case "Insert": return KeyCode.INSERT; // 0xE052
            case "Delete": return KeyCode.DELETE; // 0xE053
            case "OSLeft":
            case "MetaLeft": return KeyCode.META; // 0xE05B
            case "OSRight":
            case "MetaRight": return KeyCode.META; // 0xE05C
            case "ContextMenu": return KeyCode.CONTEXT_MENU; // 0xE05D
            case "Power": return KeyCode.POWER; // 0xE05E
            default: {
                String fxKeyName = htmlKey;
                int length = htmlKey.length();
                if (length == 6 && htmlKey.startsWith("Digit")) // Digit0 to Digit9
                    fxKeyName = String.valueOf(htmlKey.charAt(5)); // -> 0 to 9
                else if (length == 4 && htmlKey.startsWith("Key")) // KeyQ, etc...
                    fxKeyName = String.valueOf(htmlKey.charAt(3)); // -> Q, etc...
                else if (htmlKey.startsWith("Numpad"))
                    fxKeyName = Strings.replaceAll(htmlKey,"Numpad", "Numpad ");
                return KeyCode.getKeyCode(fxKeyName);
            }
        }
    }

    protected void setElementTextContent(String textContent) {
        String text = Strings.toSafeString(textContent);
        if (!Objects.equals(element.textContent, text)) {
            element.textContent = text; // Using a safe string to avoid "undefined" with IE
            clearLayoutCache();
        }
    }

    protected void clearLayoutCache() {
        if (this instanceof LayoutMeasurable)
            ((LayoutMeasurable) this).clearCache();
    }

    /* String attributes */

    protected void setElementAttribute(String name, String value, String skipValue) {
        if (skipValue != null && Objects.equals(value, skipValue))
            value = null;
        setElementAttribute(name, value);
    }

    protected void setElementAttribute(String name, String value) {
        if (isStyleAttribute(name))
            setElementStyleAttribute(name, value);
        else
            setElementAttribute(container, name, value);
    }

    protected void setElementAttribute(Element e, String name, String value) {
        if (value == null)
            e.removeAttribute(name);
        else
            e.setAttribute(name, value);
    }

    /* Double attributes */

    protected void setElementAttribute(String name, Double value, Double skipValue) {
        if (skipValue != null && Objects.equals(value, skipValue))
            value = null;
        setElementAttribute(name, value);
    }

    protected void setElementAttribute(String name, Double value) {
        if (container == element && isStyleAttribute(name))
            setElementStyleAttribute(name, value);
        else
            setElementAttribute(container, name, value);
    }

    private void setElementAttribute(Element e, String name, Double value) {
        if (value == null)
            e.removeAttribute(name);
        else
            e.setAttribute(name, value);
    }

    protected void setFontAttributes(Font font) {
        if (font != null) {
            setElementAttribute("font-family", font.getFamily());
            setElementAttribute("font-style", font.getPosture() == FontPosture.ITALIC ? "italic" : "normal");
            setElementAttribute("font-weight", font.getWeight() == null ? 0d : font.getWeight().getWeight());
            setElementAttribute("font-size", toPx(font.getSize()));
        }
    }

    private static String toSvgBlendMode(BlendMode blendMode) {
        if (blendMode != null)
            switch (blendMode) {
                case SRC_OVER: return "";
                case SRC_ATOP: return "";
                case ADD: return "";
                case MULTIPLY: return "multiply";
                case SCREEN: return "screen";
                case OVERLAY: return "overlay";
                case DARKEN: return "darken";
                case LIGHTEN: return "lighten";
                case COLOR_DODGE: return "color-dodge";
                case COLOR_BURN: return "color-burn";
                case HARD_LIGHT: return "hard-light";
                case SOFT_LIGHT: return "soft-light";
                case DIFFERENCE: return "difference";
                case EXCLUSION: return "exclusion";
                case RED: return "";
                case GREEN: return "";
                case BLUE: return "";
            }
        return null;
    }

    private static String toCssCursor(Cursor cursor) {
        if (cursor == Cursor.DEFAULT)
            return "default";
        if (cursor == Cursor.HAND)
            return "pointer";
        return null;
    }

    static String toPx(double position) {
        return toPixel(position) + "px";
    }

    static long toPixel(double position) {
        return Math.round(position);
    }

    public static HtmlSvgNodePeer toNodePeer(Node node, Scene scene) {
        node.setScene(scene);
        return (HtmlSvgNodePeer) node.getOrCreateAndBindNodePeer();
    }

    public static Element toContainerElement(Node node, Scene scene) {
        return toNodePeer(node, scene).getContainer();
    }
}
