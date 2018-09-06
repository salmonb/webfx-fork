package mongooses.loadtester.activities.saveloadtest;

import webfx.framework.activity.base.combinations.domainpresentation.impl.DomainPresentationActivityImpl;

/**
 * @author Bruno Salmon
 */
class SaveLoadTestActivity extends DomainPresentationActivityImpl<SaveLoadTestPresentationModel> {

    SaveLoadTestActivity() {
        super(SaveLoadTestPresentationViewActivity::new, SaveLoadTestPresentationLogicActivity::new);
    }
}