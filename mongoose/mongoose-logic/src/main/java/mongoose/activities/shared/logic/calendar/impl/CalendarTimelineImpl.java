package mongoose.activities.shared.logic.calendar.impl;

import javafx.beans.property.Property;
import mongoose.activities.shared.logic.calendar.CalendarTimeline;
import mongoose.activities.shared.logic.time.DateTimeRange;
import mongoose.activities.shared.logic.time.DayTimeRange;

/**
 * @author Bruno Salmon
 */
public class CalendarTimelineImpl implements CalendarTimeline {

    private final Property<String> displayNameProperty;
    private final DateTimeRange dateTimeRange;
    private final DayTimeRange dayTimeRange;

    public CalendarTimelineImpl(Property<String> displayNameProperty, DateTimeRange dateTimeRange, DayTimeRange dayTimeRange) {
        this.displayNameProperty = displayNameProperty;
        this.dateTimeRange = dateTimeRange;
        this.dayTimeRange = dayTimeRange;
    }

    @Override
    public Property<String> displayNameProperty() {
        return displayNameProperty;
    }

    @Override
    public DateTimeRange getDateTimeRange() {
        return dateTimeRange;
    }

    @Override
    public DayTimeRange getDayTimeRange() {
        return dayTimeRange;
    }
}