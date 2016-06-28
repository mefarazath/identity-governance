/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.identity.recovery.services;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.CarbonConstants;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.identity.application.common.model.User;
import org.wso2.carbon.identity.base.IdentityException;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.identity.recovery.ChallengeQuestionManager;
import org.wso2.carbon.identity.recovery.IdentityRecoveryClientException;
import org.wso2.carbon.identity.recovery.IdentityRecoveryConstants;
import org.wso2.carbon.identity.recovery.IdentityRecoveryException;
import org.wso2.carbon.identity.recovery.IdentityRecoveryServerException;
import org.wso2.carbon.identity.recovery.internal.IdentityRecoveryServiceDataHolder;
import org.wso2.carbon.identity.recovery.model.ChallengeQuestion;
import org.wso2.carbon.identity.recovery.model.UserChallengeAnswer;
import org.wso2.carbon.identity.recovery.util.Utils;
import org.wso2.carbon.user.api.AuthorizationManager;
import org.wso2.carbon.user.api.UserStoreException;

import java.util.List;

/**
 * This is the admin service for the identity management. Some of these
 * operations are can only be carried out by admins. The other operations are
 * allowed to all logged in users.
 *
 * @author sga
 */
public class UserIdentityManagementAdminService {

    private static Log log = LogFactory.getLog(UserIdentityManagementAdminService.class);


    public ChallengeQuestion[] getChallengeQuestionsOfTenant(String tenantDomain) throws IdentityRecoveryException {
        ChallengeQuestionManager questionManager = new ChallengeQuestionManager();
        List<ChallengeQuestion> challengeQuestionList;

        try {
            challengeQuestionList = questionManager.getAllChallengeQuestions(tenantDomain);
            return challengeQuestionList.toArray(new ChallengeQuestion[challengeQuestionList.size()]);
        } catch (IdentityRecoveryException e) {
            String errorMgs = "Error loading challenge questions for tenant : %s.";
            log.error(String.format(errorMgs, tenantDomain), e);
            throw new IdentityRecoveryException(String.format(errorMgs, tenantDomain), e);
        }
    }

    /**
     * Get all tenant questions of a locale in a tenant domain
     *
     * @param tenantAwareUserName
     * @return
     * @throws IdentityRecoveryServerException
     */
    public ChallengeQuestion[] getChallengeQuestionsForUser(String tenantAwareUserName)
            throws IdentityRecoveryException {

        String tenantDomain = CarbonContext.getThreadLocalCarbonContext().getTenantDomain();

        ChallengeQuestionManager questionManager = new ChallengeQuestionManager();
        List<ChallengeQuestion> challengeQuestionList;
        try {
            String locale = getLocaleOfUser(tenantAwareUserName, tenantDomain);
            challengeQuestionList = questionManager.getAllChallengeQuestions(tenantDomain, locale);
            return challengeQuestionList.toArray(new ChallengeQuestion[challengeQuestionList.size()]);
        } catch (IdentityRecoveryException e) {
            String errorMgs = "Error loading challenge questions for user : %s@%s.";
            log.error(String.format(errorMgs, tenantAwareUserName, tenantDomain), e);
            throw new IdentityRecoveryException(String.format(errorMgs, tenantAwareUserName, tenantDomain), e);
        }
    }


    /**
     * Get all tenant questions of a locale in a tenant domain
     *
     * @param tenantDomain
     * @param locale
     * @return
     * @throws IdentityRecoveryServerException
     */
    public ChallengeQuestion[] getChallengeQuestionsForLocale(String tenantDomain, String locale)
            throws IdentityRecoveryException {

        ChallengeQuestionManager questionManager = new ChallengeQuestionManager();
        List<ChallengeQuestion> challengeQuestionList;

        try {
            challengeQuestionList = questionManager.getAllChallengeQuestions(tenantDomain, locale);
            return challengeQuestionList.toArray(new ChallengeQuestion[challengeQuestionList.size()]);
        } catch (IdentityRecoveryException e) {
            String errorMgs = String.format("Error loading challenge questions for tenant %s in %s locale.",
                    tenantDomain, locale);
            log.error(errorMgs, e);
            throw new IdentityRecoveryException(errorMgs, e);
        }
    }


    /**
     * Set challenge questions for a tenant domain
     *
     * @param challengeQuestions
     * @param tenantDomain
     * @throws IdentityRecoveryException
     */
    public void setChallengeQuestionsOfTenant(ChallengeQuestion[] challengeQuestions, String tenantDomain)
            throws IdentityRecoveryException {
        try {
            ChallengeQuestionManager questionManager = new ChallengeQuestionManager();
            questionManager.addChallengeQuestions(challengeQuestions, tenantDomain);
        } catch (IdentityRecoveryException e) {
            String errorMsg = "Error setting challenge questions for tenant domain %s.";
            log.error(String.format(errorMsg, tenantDomain), e);
            throw new IdentityRecoveryException(String.format(errorMsg, tenantDomain), e);
        }
    }


    /**
     * Set challenge questions for a tenant domain
     *
     * @param challengeQuestions
     * @param tenantDomain
     * @throws IdentityRecoveryException
     */
    public void deleteChallengeQuestionsOfTenant(ChallengeQuestion[] challengeQuestions, String tenantDomain)
            throws IdentityRecoveryException {
        try {
            ChallengeQuestionManager questionManager = new ChallengeQuestionManager();
            questionManager.deleteChallengeQuestions(challengeQuestions, tenantDomain);
        } catch (IdentityRecoveryException e) {
            String errorMsg = "Error deleting challenge questions in tenant domain %s.";
            log.error(String.format(errorMsg, tenantDomain), e);
            throw new IdentityRecoveryException(String.format(errorMsg, tenantDomain), e);
        }
    }


    /**
     * Set challenge question answers for a user
     *
     * @param tenantAwareUserName
     * @param userChallengeAnswers
     * @throws IdentityRecoveryException
     */
    public void setUserChallengeAnswers(String tenantAwareUserName, UserChallengeAnswer[] userChallengeAnswers)
            throws IdentityRecoveryException {

        if (ArrayUtils.isEmpty(userChallengeAnswers)) {
            String errorMsg = "No challenge question answers provided by the user " + tenantAwareUserName;
            log.error(errorMsg);
            throw new IdentityRecoveryClientException(errorMsg);
        }

        String tenantDomain = CarbonContext.getThreadLocalCarbonContext().getTenantDomain();
        String loggedInName = CarbonContext.getThreadLocalCarbonContext().getUsername();

        // TODO externalize the authorization logic
        if (tenantAwareUserName != null && !tenantAwareUserName.equals(loggedInName)) {
            boolean isAuthorized = isUserAuthorized(tenantAwareUserName, tenantDomain);
            if (!isAuthorized) {
                throw new IdentityRecoveryClientException("Unauthorized access!! Possible elevation of privilege attack. " +
                        "User " + loggedInName + " trying to change challenge questions for user " + tenantAwareUserName);
            }
        } else if (tenantAwareUserName == null) {
            tenantAwareUserName = loggedInName;
        }

        try {
            ChallengeQuestionManager questionManager = new ChallengeQuestionManager();
            User user = Utils.createUser(tenantAwareUserName, tenantDomain);
            questionManager.setChallengesOfUser(user, userChallengeAnswers);

        } catch (IdentityException e) {
            String errorMessage = "Error while persisting user challenges for user : " + tenantAwareUserName;
            log.error(errorMessage, e);
            throw new IdentityRecoveryException(errorMessage, e);
        }
    }

    /**
     * Get Challenge question answers along with their encrypted answers of a user
     *
     * @param tenantAwareUserName
     * @return
     * @throws IdentityRecoveryException
     */
    public UserChallengeAnswer[] getUserChallengeAnswers(String tenantAwareUserName) throws IdentityRecoveryException {

        String tenantDomain = CarbonContext.getThreadLocalCarbonContext().getTenantDomain();
        String loggedInName = CarbonContext.getThreadLocalCarbonContext().getUsername();

        // TODO externalize authorization
        if (tenantAwareUserName != null && !tenantAwareUserName.equals(loggedInName)) {
            boolean isAuthorized = isUserAuthorized(tenantAwareUserName, tenantDomain);
            if (!isAuthorized) {
                throw new IdentityRecoveryClientException(
                        "Unauthorized access!! Possible violation of confidentiality. " +
                                "User " + loggedInName + " trying to get challenge questions for user " + tenantAwareUserName);
            }
        } else if (tenantAwareUserName == null) {
            tenantAwareUserName = loggedInName;
        }

        try {
            User user = Utils.createUser(tenantAwareUserName, tenantDomain);
            ChallengeQuestionManager processor = new ChallengeQuestionManager();
            return processor.getChallengeAnswersOfUser(user);
        } catch (IdentityRecoveryException e) {
            String msg = "Error retrieving user challenge answers for " + tenantAwareUserName;
            log.error(msg, e);
            throw new IdentityRecoveryException(msg, e);
        }
    }


//    /**
//     * This method is used to check the user's user store is read only.
//     *
//     * @param userName
//     * @param tenantDomain
//     * @return
//     * @throws IdentityRecoveryException
//     */
//    public boolean isReadOnlyUserStore(String userName, String tenantDomain) throws IdentityRecoveryException {
//        boolean isReadOnly = false;
//        org.wso2.carbon.user.core.UserStoreManager userStoreManager = null;
//
//        if (StringUtils.isEmpty(tenantDomain)) {
//            tenantDomain = MultitenantConstants.SUPER_TENANT_DOMAIN_NAME;
//        }
//
//        RealmService realmService = IdentityRecoveryServiceComponent.getRealmService();
//        int tenantId;
//
//        try {
//            tenantId = IdentityTenantUtil.getTenantId(tenantDomain);
//            if (realmService.getTenantUserRealm(tenantId) != null) {
//                userStoreManager = (org.wso2.carbon.user.core.UserStoreManager) getUserStore(userName);
//            }
//        } catch (Exception e) {
//            String msg = "Error retrieving the user store manager for the tenant";
//            throw new IdentityRecoveryServerException(msg, e);
//        }
//
//        try {
//            if (userStoreManager != null && userStoreManager.isReadOnly()) {
//                isReadOnly = true;
//            } else
//                isReadOnly = false;
//
//        } catch (org.wso2.carbon.user.core.UserStoreException e) {
//            String errorMessage = "Error while retrieving user store manager";
//            //     log.error(errorMessage, e);
//            throw new IdentityRecoveryServerException(errorMessage, e);
//        }
//
//        return isReadOnly;
//    }

//    private UserStoreManager getUserStore(String userName) throws UserStoreException {
//        UserStoreManager userStoreManager = IdentityRecoveryServiceComponent.getRealmService().
//                getTenantUserRealm(CarbonContext.getThreadLocalCarbonContext().getTenantId()).getUserStoreManager();
//        if (userName != null && userName.contains(UserCoreConstants.DOMAIN_SEPARATOR)) {
//            String userStoreDomain = getUserStoreDomainName(userName);
//            return ((AbstractUserStoreManager) userStoreManager)
//                    .getSecondaryUserStoreManager(userStoreDomain);
//        } else {
//            return userStoreManager;
//        }
//    }
//
//    private String getUserStoreDomainName(String userName) {
//        String userNameWithoutDomain = userName;
//        int index;
//        if ((index = userName.indexOf(CarbonConstants.DOMAIN_SEPARATOR)) >= 0) {
//            // remove domain name if exist
//            userNameWithoutDomain = userName.substring(0, index);
//        }
//        return userNameWithoutDomain;
//    }

    private String getLocaleOfUser(String tenantAwareUserName, String tenantDomain) throws IdentityRecoveryServerException {
        User user = Utils.createUser(tenantAwareUserName, tenantDomain);
        String locale = IdentityRecoveryConstants.LOCALE_EN_US;
        try {
            String userLocale =
                    Utils.getClaimFromUserStoreManager(user, IdentityRecoveryConstants.Questions.LOCALE_CLAIM);
            if (StringUtils.isNotBlank(userLocale)) {
                locale = userLocale;
            }
        } catch (UserStoreException e) {
            String errorMsg = String.format("Error when retrieving the locale claim of user %s of %s domain.",
                    tenantAwareUserName, tenantDomain);
            log.error(errorMsg);
            throw new IdentityRecoveryServerException(errorMsg, e);
        }

        return locale;
    }

    private boolean isUserAuthorized(String tenantAwareUserName, String tenantDomain)
            throws IdentityRecoveryException {

        int tenantId = IdentityTenantUtil.getTenantId(tenantDomain);
        AuthorizationManager authzManager = null;
        boolean isAuthorized;

        try {
            authzManager = IdentityRecoveryServiceDataHolder.getInstance().getRealmService().
                    getTenantUserRealm(tenantId).getAuthorizationManager();

            isAuthorized = authzManager.isUserAuthorized(tenantAwareUserName, "/permission/admin/configure/security",
                    CarbonConstants.UI_PERMISSION_ACTION);

        } catch (UserStoreException e) {
            throw new IdentityRecoveryServerException("Error occurred while checking access level for " +
                    "user " + tenantAwareUserName + " in tenant " + tenantDomain, e);
        }

        return isAuthorized;
    }

}
