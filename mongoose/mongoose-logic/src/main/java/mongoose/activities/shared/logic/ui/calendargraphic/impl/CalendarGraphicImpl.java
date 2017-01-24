package mongoose.activities.shared.logic.ui.calendargraphic.impl;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Duration;
import mongoose.activities.shared.logic.calendar.Calendar;
import mongoose.activities.shared.logic.calendar.CalendarTimeline;
import mongoose.activities.shared.logic.ui.calendargraphic.CalendarClickEvent;
import mongoose.activities.shared.logic.ui.calendargraphic.CalendarGraphic;
import mongoose.activities.shared.logic.time.DayTimeRange;
import mongoose.activities.shared.logic.time.TimeInterval;
import naga.commons.util.async.Handler;
import naga.commons.util.collection.Collections;
import naga.framework.ui.i18n.I18n;
import naga.fx.properties.Properties;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * @author Bruno Salmon
 */
public class CalendarGraphicImpl implements CalendarGraphic {

    private Calendar calendar;
    private final I18n i18n;
    private Pane rootNode;
    private long firstEpochDay;

    public CalendarGraphicImpl(Calendar calendar, I18n i18n) {
        this.i18n = i18n;
        setCalendar(calendar);
    }

    @Override
    public Calendar getCalendar() {
        return calendar;
    }

    @Override
    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
        firstEpochDay = calendar.getPeriod().getIncludedStart(TimeUnit.DAYS);
        if (rootNode != null)
            createOrUpdateRootNodeCalendar();
    }

    private final Property<Handler<CalendarClickEvent>> calendarClickHandlerProperty = new SimpleObjectProperty<>();
    @Override
    public Property<Handler<CalendarClickEvent>> calendarClickHandlerProperty() {
        return calendarClickHandlerProperty;
    }

    @Override
    public Node getNode() {
        if (rootNode == null)
            createRootNode();
        return rootNode;
    }

    private void createRootNode() {
        rootNode = new Pane();
        rootNode.widthProperty().addListener((observable, oldValue, newWidth) -> updateTotalWidth(newWidth.doubleValue()));
        createOrUpdateRootNodeCalendar();
    }

    private HorizontalDayPositioner horizontalDayPositioner;
    private VerticalDayTimePositioner verticalDayPositioner;

    private void createOrUpdateRootNodeCalendar() {
        horizontalDayPositioner = new HorizontalDayPositioner(calendar);
        verticalDayPositioner = new VerticalDayTimePositioner((Property) rootNode.prefHeightProperty());
        Group calendarGroup = createCalendarGroup();
        rootNode.getChildren().setAll(calendarGroup);
        updateTotalWidth(rootNode.getWidth());
        Rotate rotate = null; // Rotate.create();
        if (rotate != null) {
            calendarGroup.getTransforms().setAll(rotate);
            Properties.runOnPropertiesChange(arg -> {
                rotate.setPivotX(rootNode.getWidth() / 2);
                rotate.setPivotY(rootNode.getHeight() / 2);
            }, rootNode.widthProperty(), rootNode.heightProperty());
            Timeline timeline = new Timeline();
            timeline.getKeyFrames().setAll(new KeyFrame(Duration.seconds(5), new KeyValue(rotate.angleProperty(), 360d)));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
        }
    }

    private void updateTotalWidth(double totalWidth) {
        horizontalDayPositioner.setTotalWidth(totalWidth);
    }

    private Group createCalendarGroup() {
        Group calendarGroup = new Group();
        Group headersGroup = createDayColumnHeadersGroup();
        Group bodyGroup = createBodyGroup();
        bodyGroup.getTransforms().setAll(new Translate(0, DayColumnHeaderViewModel.dayColumnHeaderHeight + 1));
        calendarGroup.getChildren().setAll(headersGroup, bodyGroup);
        return calendarGroup;
    }

    private Group createDayColumnHeadersGroup() {
        Group daysHeadGroup = new Group();
        for (long displayedEpochDay = horizontalDayPositioner.getFirstDisplayedEpochDay(); displayedEpochDay <= horizontalDayPositioner.getLastDisplayedEpochDay(); displayedEpochDay++) {
            DayColumnHeaderViewModel model = new DayColumnHeaderViewModel(displayedEpochDay, DayColumnHeaderViewModel.dayColumnHeaderHeight, i18n);
            horizontalDayPositioner.addHorizontalDayPositioned(model);
            daysHeadGroup.getChildren().add(model.group);
        }
        return daysHeadGroup;
    }

    private Group createBodyGroup() {
        Group bodyGroup = new Group();
        Collections.forEach(calendar.getTimelines(), timeline -> addTimelineNodes(timeline, bodyGroup.getChildren()));
        verticalDayPositioner.updateVerticalPositions();
        return bodyGroup;
    }

    private void addTimelineNodes(CalendarTimeline timeline, Collection<Node> destCollection) {
        Collections.forEach(timeline.getDateTimeRange().changeTimeUnit(TimeUnit.DAYS).getDaysArray(),
                epochDay -> addBlockNodes(epochDay, timeline.getDayTimeRange(), timeline, destCollection));
    }

    private void addBlockNodes(long epochDay, DayTimeRange dayTimeRange, CalendarTimeline timeline, Collection<Node> destCollection) {
        for (TimeInterval dayTimeInterval : dayTimeRange.getDayTimeSeries(epochDay, TimeUnit.DAYS).getArray())
            destCollection.add(createBlockNode(epochDay, dayTimeInterval, timeline));
    }

    private Node createBlockNode(long epochDay, TimeInterval minuteInterval, CalendarTimeline timeline) {
        DayColumnBodyBlockViewModel model = new DayColumnBodyBlockViewModel(this, epochDay, minuteInterval, timeline, epochDay == firstEpochDay);
        horizontalDayPositioner.addHorizontalDayPositioned(model);
        verticalDayPositioner.addVerticalDayTimePositioned(model);
        return model.getGroup();
    }
}