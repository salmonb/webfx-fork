package naga.fx.sun.scene.control.skin;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import naga.fx.scene.control.Cell;
import naga.fx.sun.scene.control.behaviour.BehaviorBase;

/**
 * A base skin implementation, specifically for ListCellSkin and TreeCellSkin.
 * This might not be a suitable base class for TreeCellSkin or some other
 * such skins.
 */
public class CellSkinBase<C extends Cell, B extends BehaviorBase<C>> extends LabeledSkinBase<C, B> {
    /**
     * The default cell size. For vertical ListView or a TreeView or TableView
     * this is the height, for a horizontal ListView this is the width. This
     * is settable from CSS
     */
    private Property<Double> cellSize;

    public final double getCellSize() {
        return cellSize == null ? DEFAULT_CELL_SIZE : cellSize.getValue();
    }

    public final ReadOnlyProperty<Double> cellSizeProperty() {
        return cellSizePropertyImpl();
    }

    private Property<Double> cellSizePropertyImpl() {
        if (cellSize == null) {
            cellSize = new SimpleObjectProperty<>(DEFAULT_CELL_SIZE)/* {

                @Override
                public void applyStyle(StyleOrigin origin, Number value) {
                    double size = value == null ? DEFAULT_CELL_SIZE : value.doubleValue();
                    // guard against a 0 or negative size
                    super.applyStyle(origin, size <= 0 ? DEFAULT_CELL_SIZE : size);
                }


                @Override public void set(double value) {
//                    // Commented this out due to RT-19794, because otherwise
//                    // cellSizeSet would be false when the default caspian.css
//                    // cell size was set. This would lead to
//                    // ListCellSkin.computePrefHeight computing the pref height
//                    // of the cell (which is about 22px), rather than use the
//                    // value provided by caspian.css (which is 24px).
//                    // cellSizeSet = true;//value != DEFAULT_CELL_SIZE;
                    super.set(value);
                    getSkinnable().requestLayout();
                }

                @Override public Object getBean() {
                    return CellSkinBase.this;
                }

                @Override public String getName() {
                    return "cellSize";
                }

                @Override public CssMetaData<Cell<?>, Number> getCssMetaData() {
                    return StyleableProperties.CELL_SIZE;
                }
            }*/;
        }
        return cellSize;
    }

    public CellSkinBase(final C control, final B behavior) {
        super (control, behavior);

        /**
         * The Cell does not typically want to block mouse events from going down
         * to the virtualized controls holding the cell. For example mouse clicks
         * on cells should also pass down to the ListView holding the cells.
         */
        consumeMouseEvents(false);
    }



    /***************************************************************************
     *                                                                         *
     *                         Stylesheet Handling                             *
     *                                                                         *
     **************************************************************************/

    static final double DEFAULT_CELL_SIZE = 24.0;

/**
     * Super-lazy instantiation pattern from Bill Pugh.
     * @treatAsPrivate implementation detail
     */
/*

    private static class StyleableProperties {
        private final static CssMetaData<Cell<?>,Number> CELL_SIZE =
                new CssMetaData<Cell<?>,Number>("-fx-cell-size",
                        SizeConverter.getInstance(), DEFAULT_CELL_SIZE) {

                    @Override
                    public boolean isSettable(Cell<?> n) {
                        final CellSkinBase<?,?> skin = (CellSkinBase<?,?>) n.getSkin();
                        return skin.cellSize == null || !skin.cellSize.isBound();
                    }

                    @Override
                    public StyleableProperty<Number> getStyleableProperty(Cell<?> n) {
                        final CellSkinBase<?,?> skin = (CellSkinBase<?,?>) n.getSkin();
                        return (StyleableProperty<Number>)(WritableValue<Number>)skin.cellSizePropertyImpl();
                    }
                };

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
        static {

            final List<CssMetaData<? extends Styleable, ?>> styleables =
                    new ArrayList<CssMetaData<? extends Styleable, ?>>(SkinBase.getClassCssMetaData());
            styleables.add(CELL_SIZE);
            STYLEABLES = Collections.unmodifiableList(styleables);

        }
    }

    */
/**
     * @return The CssMetaData associated with this class, which may include the
     * CssMetaData of its super classes.
     *//*

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }

    */
/**
     * {@inheritDoc}
     *//*

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }
*/

}

