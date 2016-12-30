package naga.fx.spi.gwt.util;

import com.google.gwt.core.client.JavaScriptObject;
import elemental2.*;
import naga.commons.scheduler.Scheduled;
import naga.commons.util.Strings;
import naga.commons.util.tuples.Unit;
import naga.fx.spi.Toolkit;

import java.lang.Iterable;

import static elemental2.Global.document;

/**
 * @author Bruno Salmon
 */
public class HtmlUtil {

    public static <N extends Node> N removeChildren(N node) {
        if (node != null)
            while (node.firstChild != null)
                node.removeChild(node.firstChild);
        return node;
    }

    public static <N extends Node> N appendChild(N parent, Node child) {
        if (parent != null && child != null)
            parent.appendChild(child);
        return parent;
    }

    public static <N extends Node> N appendFirstChild(N parent, Node child) {
        if (parent != null && child != null)
            parent.insertBefore(child, parent.firstChild);
        return parent;
    }

    public static <N extends Node> N setChild(N parent, Node child) {
        return appendChild(removeChildren(parent), child);
    }

    public static <N extends Node> N setChildren(N parent, Iterable<? extends Node> children) {
        return appendChildren(removeChildren(parent), children);
    }

    public static <N extends Node> N setChildren(N parent, Node... children) {
        return appendChildren(removeChildren(parent), children);
    }

    public static <N extends Node> N appendChildren(N parent, Iterable<? extends Node> children) {
        for (Node child : children)
            appendChild(parent, child);
        return parent;
    }

    public static <N extends Node> N appendChildren(N parent, Node... children) {
        for (Node child : children)
            appendChild(parent, child);
        return parent;
    }

    public static HTMLBodyElement setBodyContent(Node content) {
        return appendChild(removeChildrenUpToScripts(document.body), content);
    }

    public static <N extends Node> N removeChildrenUpToScripts(N node) {
        if (node != null)
            while (node.firstChild != null && !(node.firstChild instanceof HTMLScriptElement))
                node.removeChild(node.firstChild);
        return node;
    }


    public static <E extends Element> E setAttribute(E e, String name, String value) {
        e.setAttribute(name, value);
        return e;
    }

    public static <E extends Element> E appendAttribute(E e, String name, String value, String separator) {
        return setAttribute(e, name, Strings.appendToken(e.getAttribute(name), value, separator));
    }

    public static <E extends HTMLElement> E setPseudoClass(E e, String pseudoClass) {
        return setAttribute(e, "class", pseudoClass);
    }

    public static <E extends HTMLElement> E setPseudoClass(E e, String pseudoClass, boolean present) {
        return present ? addPseudoClass(e, pseudoClass) : removePseudoClass(e, pseudoClass);
    }

    public static <E extends HTMLElement> E addPseudoClass(E e, String pseudoClass) {
        pseudoClass = " " + pseudoClass + " ";
        if (!e.className.contains(pseudoClass))
            e.className = Strings.concat(e.className, pseudoClass);
        return e;
    }

    public static <E extends HTMLElement> E removePseudoClass(E e, String pseudoClass) {
        pseudoClass = " " + pseudoClass + " ";
        return setPseudoClass(e, Strings.replaceAll(e.className, pseudoClass, ""));
    }

    public static <E extends Element> E setStyle(E e, String style) {
        return setAttribute(e, "style", style);
    }

    private final static String[] browserPrefixes = {"-webkit-", "-moz-"};
    private static String[] getAttributeBrowserPrefixes(String name) {
        switch (name) {
            case "clip-path":
                return browserPrefixes;
        }
        return null;
    }

    public static <N extends Node> N setStyleAttribute(N node, String name, Object value) {
        if (node instanceof Element)
            setStyleAttribute((Element) node, name, value);
        return node;
    }

    public static <E extends Element> E setStyleAttribute(E e, String name, Object value) {
        String[] prefixes = getAttributeBrowserPrefixes(name);
        if (prefixes != null)
            for (String prefix: prefixes)
                setPrefixedStyleAttribute(e, prefix + name, value);
        return setPrefixedStyleAttribute(e, name, value);
    }

    private static <E extends Element> E setPrefixedStyleAttribute(E e, String name, Object value) {
        //e.style.setProperty(name, Strings.toString(value));
        String s = Strings.toString(value);
        if (e instanceof HTMLElement)
            setJsAttribute(((JavaScriptObject) (Object) ((HTMLElement) e).style), name, s);
        else if (value != null)
            appendStyle(e, name + ": " + value);
        return e;
    }

    public static <E extends Element> E appendStyle(E e, String style) {
        return appendAttribute(e, "style", style, "; ");
    }

    private static native void setJsAttribute(JavaScriptObject o, String name, String value) /*-{
        o[name] = value;
    }-*/;

    public static native CSSStyleDeclaration getComputedStyle(Element e) /*-{
        return $wnd.getComputedStyle(e);
    }-*/;

    public static <E extends Element> E createElement(String tagName) {
        return (E) document.createElement(tagName);
    }

    public static HTMLButtonElement createButtonElement() {
        return createElement("button");
    }

    public static HTMLLabelElement createLabelElement() {
        return createElement("label");
    }

    public static HTMLTableElement createTableElement() {
        return createElement("table");
    }

    public static HTMLImageElement createImageElement() {
        return createElement("img");
    }

    public static HTMLDivElement createDivElement() {
        return createElement("div");
    }


    public static <E extends HTMLElement> E absolutePosition(E e) {
        e.style.position = "absolute";
        return e;
    }

    public static <E extends Node> E createNodeFromHtml(String innerHTML) {
        HTMLDivElement div = createDivElement();
        div.innerHTML = innerHTML;
        return (E) div.firstChild;
    }

    public static HTMLElement createSpanElement() {
        return createElement("span");
    }

    public static HTMLInputElement createInputElement(String type) {
        return setAttribute(createElement("input"), "type", type);
    }

    public static HTMLInputElement createTextInput() {
        return createInputElement("text");
    }

    public static HTMLInputElement createCheckBox() {
        return createInputElement("checkbox");
    }

    public static HTMLInputElement createRadioButton() {
        return createInputElement("radio");
    }

    public static <E extends Element> E getElementById(Element element, String id) {
        return getElementById(element, id, "*");
    }

    public static <E extends Element> E getElementById(Element element, String id, String tag) {
        NodeList<Element> elements = element.getElementsByTagName(tag);
        for (int i = 0; i < elements.length; i++) {
            Element e = elements.get(i);
            if (id.equals(e.getAttribute("id")))
                return (E) e;
        }
        return null;
    }

    public static void replaceNode(Node oldChild, Node newChild, boolean observeIfNotYetAttached) {
        if (oldChild.parentNode != null)
            oldChild.parentNode.replaceChild(newChild, oldChild);
        else if (observeIfNotYetAttached)
            runOnAttached(oldChild, () -> oldChild.parentNode.replaceChild(newChild, oldChild));
    }

    public static void runOnAttached(Node node, Runnable runnable) {
        if (node.parentNode != null)
            runnable.run();
        else {
/* Commented as can't make it work (just crashes) TODO: Make it work
            new MutationObserver((mutationRecords, mutationObserver) -> {
                Platform.log("mutationRecords");
                if (node.parentNode != null) {
                    mutationObserver.disconnect();
                    runnable.run();
                }
                return null;
            }).observe(oldChild);
*/
            // Using an alternative way with a periodic scan (quite ugly but works)
            Unit<Scheduled> scheduled = new Unit<>();
            scheduled.set(Toolkit.get().scheduler().schedulePeriodic(100, () -> {
                if (node.parentNode != null) {
                    scheduled.get().cancel();
                    runnable.run();
                }
            }));
        }
    }
}