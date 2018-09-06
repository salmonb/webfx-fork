package mongooses.core.activities.sharedends.generic.eventdependent;

import mongooses.core.aggregates.EventAggregate;
import mongooses.core.aggregates.EventAggregateMixin;
import webfx.framework.activity.base.elementals.domain.DomainActivityContext;
import webfx.framework.activity.base.elementals.domain.DomainActivityContextMixin;
import webfx.framework.activity.base.elementals.uiroute.UiRouteActivityContext;
import webfx.framework.activity.base.elementals.uiroute.UiRouteActivityContextMixin;

/**
 * @author Bruno Salmon
 */
public interface EventDependentActivityMixin
        <C extends DomainActivityContext<C> & UiRouteActivityContext<C>>

        extends UiRouteActivityContextMixin<C>,
        DomainActivityContextMixin<C>,
        EventAggregateMixin,
        EventDependentPresentationModelMixin
{

    default EventAggregate getEventService() {
        return EventAggregate.getOrCreate(getEventId(), getDataSourceModel());
    }

    default void updateEventDependentPresentationModelFromContextParameters() {
        setEventId(getParameter("eventId"));
        setOrganizationId(getParameter("organizationId"));
    }

}