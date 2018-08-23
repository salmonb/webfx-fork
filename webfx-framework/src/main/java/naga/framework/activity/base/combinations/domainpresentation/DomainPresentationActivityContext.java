package naga.framework.activity.base.combinations.domainpresentation;

import naga.framework.activity.base.elementals.domain.DomainActivityContext;
import naga.framework.activity.base.elementals.presentation.PresentationActivityContext;
import naga.framework.activity.base.elementals.presentation.logic.PresentationLogicActivityContext;
import naga.framework.activity.base.elementals.presentation.view.PresentationViewActivityContext;

/**
 * @author Bruno Salmon
 */
public interface DomainPresentationActivityContext
        <THIS extends DomainPresentationActivityContext<THIS, C1, C2, PM>,
                C1 extends PresentationViewActivityContext<C1, PM>,
                C2 extends PresentationLogicActivityContext<C2, PM>,
                PM>

        extends DomainActivityContext<THIS>,
        PresentationActivityContext<THIS, C1, C2, PM> {

}