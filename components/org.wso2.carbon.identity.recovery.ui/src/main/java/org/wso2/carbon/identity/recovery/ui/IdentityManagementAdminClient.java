/*
 *
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.recovery.ui;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.recovery.stub.UserIdentityManagementAdminServiceStub;
import org.wso2.carbon.identity.recovery.stub.model.ChallengeQuestion;
import org.wso2.carbon.identity.recovery.stub.model.UserChallengeAnswer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 *
 */
public class IdentityManagementAdminClient {

    public static final String CHALLENGE_QUESTION = "challenge.question";
    public static final String CHALLENGE_QUESTION_UPDATE = "challenge.question.update";
    public static final String CHALLENGE_QUESTION_DELETE = "challenge.question.delete";
    public static final String CHALLENGE_QUESTION_SET_TEMP= "challenge.question.set.temp";


    protected static Log log = LogFactory.getLog(IdentityManagementAdminClient.class);
    protected UserIdentityManagementAdminServiceStub stub = null;

    public IdentityManagementAdminClient(String cookie, String url, ConfigurationContext configContext)
            throws Exception {
        try {
            stub = new UserIdentityManagementAdminServiceStub(configContext, url + "UserIdentityManagementAdminService");
            ServiceClient client = stub._getServiceClient();
            Options option = client.getOptions();
            option.setManageSession(true);
            option.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING, cookie);
        } catch (Exception e) {
            handleException(e.getMessage(), e);
        }
    }

    public ChallengeQuestion[] getChallengeQuestionsForTenant(String tenantDomain) throws AxisFault {

        try {
            return stub.getChallengeQuestionsOfTenant(tenantDomain);
        } catch (Exception e) {
            handleException(e.getMessage(), e);
        }
        return new ChallengeQuestion[0];
    }


    public ChallengeQuestion[] getChallengeQuestionsForUser(String tenantAwareUserName) throws AxisFault {

        try {
            return stub.getChallengeQuestionsForUser(tenantAwareUserName);
        } catch (Exception e) {
            handleException(e.getMessage(), e);
        }

        return new ChallengeQuestion[0];
    }

    public ChallengeQuestion[] getChallengeQuestionsForLocale(String tenantDomain, String locale) throws AxisFault {

        try {
            return stub.getChallengeQuestionsForLocale(tenantDomain, locale);
        } catch (Exception e) {
            handleException(e.getMessage(), e);
        }

        return new ChallengeQuestion[0];
    }


    public void setChallengeQuestions(ChallengeQuestion[] challengeQuestions, String tenantDomain)
            throws AxisFault {
        try {
            stub.setChallengeQuestionsOfTenant(challengeQuestions, tenantDomain);
        } catch (Exception e) {
            handleException(e.getMessage(), e);
        }
    }

    public void deleteChallengeQuestions(ChallengeQuestion[] challengeQuestions, String tenantDomain)
            throws AxisFault {
        try {
            stub.deleteChallengeQuestionsOfTenant(challengeQuestions, tenantDomain);
        } catch (Exception e) {
            handleException(e.getMessage(), e);
        }
    }

    public void setUserChallengeAnswers(String username, UserChallengeAnswer[] userChallengeAnswers)
            throws AxisFault {
        try {
            stub.setUserChallengeAnswers(username, userChallengeAnswers);
        } catch (Exception e) {
            handleException(e.getMessage(), e);
        }
    }

    public UserChallengeAnswer[] getUserChallengeAnswers(String username) throws AxisFault{
        try {
            return stub.getUserChallengeAnswers(username);
        } catch (Exception e) {
            handleException(e.getMessage(), e);
        }

        return new UserChallengeAnswer[0];
    }

    private String[] handleException(String msg, Exception e) throws AxisFault {
        log.error(msg, e);
        throw new AxisFault(msg, e);
    }
}
