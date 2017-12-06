package emul.javafx.scene.control;

import emul.com.sun.javafx.scene.control.skin.ToggleButtonSkin;
import emul.javafx.beans.property.ObjectProperty;
import emul.javafx.beans.property.Property;
import emul.javafx.beans.property.SimpleObjectProperty;
import emul.javafx.event.ActionEvent;
import emul.javafx.geometry.Insets;
import emul.javafx.geometry.Pos;
import emul.javafx.scene.Node;
import emul.javafx.scene.layout.*;
import emul.javafx.scene.paint.Color;
import emul.javafx.scene.paint.LinearGradient;

/**
 * @author Bruno Salmon
 */
public class ToggleButton extends ButtonBase
        implements Toggle {

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a toggle button with an empty string for its label.
     */
    public ToggleButton() {
        initialize();
    }

    /**
     * Creates a toggle button with the specified text as its label.
     *
     * @param text A text string for its label.
     */
    public ToggleButton(String text) {
        setText(text);
        initialize();
    }

    /**
     * Creates a toggle button with the specified text and icon for its label.
     *
     * @param text A text string for its label.
     * @param graphic the icon for its label.
     */
    public ToggleButton(String text, Node graphic) {
        setText(text);
        setGraphic(graphic);
        initialize();
    }

    protected void initialize() {
        //getStyleClass().setAll(DEFAULT_STYLE_CLASS);
        //setAccessibleRole(AccessibleRole.TOGGLE_BUTTON);
        // alignment is styleable through css. Calling setAlignment
        // makes it look to css like the user set the value and css will not
        // override. Initializing alignment by calling set on the
        // CssMetaData ensures that css will be able to override the value.
        //((StyleableProperty<Pos>)(WritableValue<Pos>)alignmentProperty()).applyStyle(null, Pos.CENTER);
        //setMnemonicParsing(true);     // enable mnemonic auto-parsing by default

        setBorder(BORDER);
        setBackground(BACKGROUND);
        setPadding(PADDING);
        setAlignment(Pos.CENTER);
    }
    /***************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/
    /**
     * Indicates whether this toggle button is selected. This can be manipulated
     * programmatically.
     */
    private Property<Boolean> selected;
    public final void setSelected(boolean value) {
        selectedProperty().setValue(value);
    }

    public final boolean isSelected() {
        return selected == null ? false : selected.getValue();
    }

    public final Property<Boolean> selectedProperty() {
        if (selected == null) {
            selected = new SimpleObjectProperty<Boolean>(false) {
                @Override protected void invalidated() {
                    final boolean selected = get();
                    final ToggleGroup tg = getToggleGroup();
                    // Note: these changes need to be done before selectToggle/clearSelectedToggle since
                    // those operations change properties and can execute user code, possibly modifying selected property again
                    //pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, selected);
                    //notifyAccessibleAttributeChanged(AccessibleAttribute.SELECTED);
                    if (tg != null) {
                        if (selected) {
                            tg.selectToggle(ToggleButton.this);
                        } else if (tg.getSelectedToggle() == ToggleButton.this) {
                            tg.clearSelectedToggle();
                        }
                    }
                }

                @Override
                public Object getBean() {
                    return ToggleButton.this;
                }

                @Override
                public String getName() {
                    return "selected";
                }
            };
        }
        return selected;
    }
    /**
     * The {@link ToggleGroup} to which this {@code ToggleButton} belongs. A
     * {@code ToggleButton} can only be in one group at any one time. If the
     * group is changed, then the button is removed from the old group prior to
     * being added to the new group.
     */
    private ObjectProperty<ToggleGroup> toggleGroup;
    public final void setToggleGroup(ToggleGroup value) {
        toggleGroupProperty().set(value);
    }

    public final ToggleGroup getToggleGroup() {
        return toggleGroup == null ? null : toggleGroup.get();
    }

    public final ObjectProperty<ToggleGroup> toggleGroupProperty() {
        if (toggleGroup == null) {
            toggleGroup = new SimpleObjectProperty<ToggleGroup>() {
                private ToggleGroup old;
/*
            toggleGroup = new ObjectPropertyBase<ToggleGroup>() {
                private ChangeListener<Toggle> listener = (o, oV, nV) ->
                        getImpl_traversalEngine().setOverriddenFocusTraversability(nV != null ? isSelected() : null);
*/
                @Override protected void invalidated() {
                    final ToggleGroup tg = get();
                    if (tg != null && !tg.getToggles().contains(ToggleButton.this)) {
                        if (old != null) {
                            old.getToggles().remove(ToggleButton.this);
                        }
                        tg.getToggles().add(ToggleButton.this);
                        /*final ParentTraversalEngine parentTraversalEngine = new ParentTraversalEngine(ToggleButton.this);
                        setImpl_traversalEngine(parentTraversalEngine);
                        // If there's no toggle selected, do not override
                        parentTraversalEngine.setOverriddenFocusTraversability(tg.getSelectedToggle() != null ? isSelected() : null);
                        tg.selectedToggleProperty().addListener(listener);*/
                    } else if (tg == null) {
                        //old.selectedToggleProperty().removeListener(listener);
                        old.getToggles().remove(ToggleButton.this);
                        //setImpl_traversalEngine(null);
                    }

                    old = tg;
                }

                @Override
                public Object getBean() {
                    return ToggleButton.this;
                }

                @Override
                public String getName() {
                    return "toggleGroup";
                }
            };
        }
        return toggleGroup;
    }

    /***************************************************************************
     *                                                                         *
     * Methods                                                                 *
     *                                                                         *
     **************************************************************************/

    /** {@inheritDoc} */
    @Override public void fire() {
        // TODO (aruiz): if (!isReadOnly(isSelected()) {
        if (!isDisabled()) {
            setSelected(!isSelected());
            fireEvent(new ActionEvent());
        }
    }

    // Naga default hardcoded Style to match JavaFx default theme

    @Override
    protected Skin<?> createDefaultSkin() {
        return new ToggleButtonSkin(this);
    }

    private final static CornerRadii RADII = new CornerRadii(1);
    private final static Border BORDER = new Border(new BorderStroke(Color.DARKGRAY, BorderStrokeStyle.SOLID, RADII, BorderWidths.DEFAULT));
    private final static Background BACKGROUND = new Background(new BackgroundFill(LinearGradient.valueOf("from 0% 0% to 0% 100%, white 0%, #E0E0E0 100%"), RADII, Insets.EMPTY));
    private final static Insets PADDING = new Insets(7);

}