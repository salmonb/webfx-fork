package naga.framework.activity.i18n.impl;

import naga.framework.activity.i18n.I18nActivityContext;
import naga.framework.ui.i18n.I18n;
import naga.platform.activity.ActivityContext;
import naga.platform.activity.ActivityContextFactory;
import naga.platform.activity.impl.ActivityContextBase;

/**
 * @author Bruno Salmon
 */
public class I18nActivityContextBase
        <THIS extends I18nActivityContextBase<THIS>>

        extends ActivityContextBase<THIS>
        implements I18nActivityContext<THIS> {

    private I18n i18n;

    protected I18nActivityContextBase(ActivityContext parentContext, ActivityContextFactory<THIS> contextFactory) {
        super(parentContext, contextFactory);
    }

    @Override
    public void setI18n(I18n i18n) {
        this.i18n = i18n;
    }

    @Override
    public I18n getI18n() {
        if (i18n != null)
            return i18n;
        ActivityContext parentContext = getParentContext();
        if (parentContext instanceof I18nActivityContext)
            return ((I18nActivityContext) parentContext).getI18n();
        return null;
    }
}