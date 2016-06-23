/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.identity.recovery.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.identity.core.persistence.registry.RegistryResourceMgtService;
import org.wso2.carbon.identity.event.services.EventMgtService;
import org.wso2.carbon.identity.governance.IdentityGovernanceService;
import org.wso2.carbon.identity.governance.common.IdentityGovernanceConnector;
import org.wso2.carbon.identity.recovery.ChallengeQuestionManager;
import org.wso2.carbon.identity.recovery.IdentityRecoveryException;
import org.wso2.carbon.identity.recovery.connector.RecoveryConnectorImpl;
import org.wso2.carbon.identity.recovery.listener.TenantManagementListener;
import org.wso2.carbon.identity.recovery.password.NotificationPasswordRecoveryManager;
import org.wso2.carbon.identity.recovery.password.SecurityQuestionPasswordRecoveryManager;
import org.wso2.carbon.identity.recovery.username.NotificationUsernameRecoveryManager;
import org.wso2.carbon.registry.core.service.RegistryService;
import org.wso2.carbon.stratos.common.listeners.TenantMgtListener;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;


/**
 * @scr.component name="org.wso2.carbon.identity.recovery.internal.IdentityRecoveryServiceComponent" immediate="true"
 * @scr.reference name="registry.service"
 * interface="org.wso2.carbon.registry.core.service.RegistryService" cardinality="1..1"
 * policy="dynamic" bind="setRegistryService" unbind="unsetRegistryService"
 * @scr.reference name="realm.service"
 * interface="org.wso2.carbon.user.core.service.RealmService"cardinality="1..1"
 * policy="dynamic" bind="setRealmService" unbind="unsetRealmService"
 * @scr.reference name="IdentityGovernanceService"
 * interface="org.wso2.carbon.identity.governance.IdentityGovernanceService" cardinality="1..1"
 * policy="dynamic" bind="setIdentityGovernanceService" unbind="unsetIdentityGovernanceService"
 * @scr.reference name="EventMgtService"
 * interface="org.wso2.carbon.identity.event.services.EventMgtService" cardinality="1..1"
 * policy="dynamic" bind="setEventMgtService" unbind="unsetEventMgtService"
 * @scr.reference name="RegistryResourceMgtService"
 * interface="org.wso2.carbon.identity.core.persistence.registry.RegistryResourceMgtService" cardinality="1..1"
 * policy="dynamic" bind="setResourceMgtService" unbind="unsetResourceMgtService"
 */
public class IdentityRecoveryServiceComponent {

    private static Log log = LogFactory.getLog(IdentityRecoveryServiceComponent.class);

    protected void activate(ComponentContext context) {

        try {
            BundleContext bundleContext = context.getBundleContext();
            bundleContext.registerService(NotificationPasswordRecoveryManager.class.getName(),
                    new NotificationPasswordRecoveryManager(), null);
            bundleContext.registerService(SecurityQuestionPasswordRecoveryManager.class.getName(),
                    new SecurityQuestionPasswordRecoveryManager(), null);
            bundleContext.registerService(NotificationUsernameRecoveryManager.class.getName(),
                    new NotificationUsernameRecoveryManager(), null);

            context.getBundleContext().registerService(IdentityGovernanceConnector.class.getName(), new RecoveryConnectorImpl(), null);

        } catch (Exception e) {
            log.error("Error while activating identity governance component.", e);
        }

        // register the tenant management listener
        TenantMgtListener tenantMgtListener = new TenantManagementListener();
        context.getBundleContext().registerService(TenantMgtListener.class.getName(), tenantMgtListener, null);

        // register default challenge questions
        try {
            if (log.isDebugEnabled()) {
                log.debug("Loading default challenge questions for super tenant.");
            }
            loadDefaultChallengeQuestions();
         //   new ChallengeQuestionManager().getAllChallengeQuestions("carbon.super", "lk_LK");
        } catch (IdentityRecoveryException e) {
            log.error("Error persisting challenge question for super tenant.", e);
        }
    }

    protected void deactivate(ComponentContext context) {
        if (log.isDebugEnabled()) {
            log.debug("Identity Management bundle is de-activated");
        }
    }

    protected void setRealmService(RealmService realmService) {
        if (log.isDebugEnabled()) {
            log.debug("Setting the Realm Service");
        }
        IdentityRecoveryServiceDataHolder.getInstance().setRealmService(realmService);
    }

    protected void setRegistryService(RegistryService registryService) {
        if (log.isDebugEnabled()) {
            log.debug("Setting the Registry Service");
        }
        IdentityRecoveryServiceDataHolder.getInstance().setRegistryService(registryService);
    }

    protected void unsetRealmService(RealmService realmService) {
        log.debug("UnSetting the Realm Service");
        IdentityRecoveryServiceDataHolder.getInstance().setRealmService(null);
    }

    protected void unsetRegistryService(RegistryService registryService) {
        log.debug("UnSetting the Registry Service");
        IdentityRecoveryServiceDataHolder.getInstance().setRegistryService(null);
    }

    protected void unsetEventMgtService(EventMgtService eventMgtService) {
        IdentityRecoveryServiceDataHolder.getInstance().setEventMgtService(null);
    }

    protected void setEventMgtService(EventMgtService eventMgtService) {
        IdentityRecoveryServiceDataHolder.getInstance().setEventMgtService(eventMgtService);
    }

    protected void unsetIdentityGovernanceService(IdentityGovernanceService idpManager) {
        IdentityRecoveryServiceDataHolder.getInstance().setIdentityGovernanceService(null);
    }

    protected void setIdentityGovernanceService(IdentityGovernanceService idpManager) {
        IdentityRecoveryServiceDataHolder.getInstance().setIdentityGovernanceService(idpManager);
    }

    protected void unsetResourceMgtService(RegistryResourceMgtService registryResourceMgtService) {
        IdentityRecoveryServiceDataHolder.getInstance().setResourceMgtService(null);
        if (log.isDebugEnabled()) {
            log.debug("Setting Identity Resource Mgt service.");
        }
    }

    protected void setResourceMgtService(RegistryResourceMgtService registryResourceMgtService) {
        IdentityRecoveryServiceDataHolder.getInstance().setResourceMgtService(registryResourceMgtService);
        if (log.isDebugEnabled()) {
            log.debug("Unsetting Identity Resource Mgt service.");
        }
    }

    private void loadDefaultChallengeQuestions() throws IdentityRecoveryException {
        String tenantDomain = MultitenantConstants.SUPER_TENANT_DOMAIN_NAME;
        new ChallengeQuestionManager().setDefaultChallengeQuestions(tenantDomain);
    }


}
