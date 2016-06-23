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

package org.wso2.carbon.identity.recovery;

/**
 * Identity management related constants
 */
public class IdentityRecoveryConstants {


    public static final String IDENTITY_MANAGEMENT_PATH = "/repository/components/org.wso2.carbon.identity.mgt";
    public static final String IDENTITY_MANAGEMENT_QUESTIONS = IDENTITY_MANAGEMENT_PATH + "/questionCollection";
    public static final String IDENTITY_MANAGEMENT_I18N_PATH = "/repository/components/identity";
    public static final String IDENTITY_I18N_QUESTIONS =
            IDENTITY_MANAGEMENT_I18N_PATH + "/questionCollection";
    public static final String LINE_SEPARATOR = "!";
    public static final String CHALLENGE_QUESTION_URI = "http://wso2.org/claims/challengeQuestionUris";
    public static final String NOTIFICATION_TYPE_PASSWORD_RESET = "passwordreset";
    public static final String NOTIFICATION_TYPE_PASSWORD_RESET_SUCCESS = "passwordresetsucess";
    public static final String NOTIFICATION_TYPE_PASSWORD_RESET_INITIATE = "initiaterecovery";
    public static final String NOTIFICATION_ACCOUNT_ID_RECOVERY = "accountidrecovery";
    public static final String RECOVERY_STATUS_INCOMPLETE = "INCOMPLETE";
    public static final String RECOVERY_STATUS_COMPLETE = "COMPLETE";
    public static final String TEMPLATE_TYPE = "TEMPLATE_TYPE";
    public static final String CONFIRMATION_CODE = "confirmation-code";
    public static final String WSO2CARBON_CLAIM_DIALECT = "http://wso2.org/claims";

    public static final String LOCALE_EN_US = "en_US";
    public static final String LOCALE_LK_LK = "lk_lk";


    private IdentityRecoveryConstants() {
    }

    public enum ErrorMessages {

        ERROR_CODE_INVALID_CODE("18001", "Invalid Code '%s.'"),
        ERROR_CODE_EXPIRED_CODE("18002", "Expired Code '%s.'"),
        ERROR_CODE_INVALID_USER("18003", "Invalid User '%s.'"),
        ERROR_CODE_UNEXPECTED("18013", "Expired Code '%s.'"),
        ERROR_CODE_RECOVERY_NOTIFICATION_FAILURE("18015", "Error sending recovery notification"),
        ERROR_CODE_INVALID_TENANT("18016", "Invalid tenant'%s.'"),
        ERROR_CODE_CHALLENGE_QUESTION_NOT_FOUND("18017", "No challenge question found"),
        ERROR_CODE_INVALID_CREDENTIALS("17002", "Invalid Credentials"),
        ERROR_CODE_LOCKED_ACCOUNT("17003", "user account is locked '%s.'"),
        ERROR_CODE_DISABLED_ACCOUNT("17004", "user account is disabled '%s.'"),
        ERROR_CODE_REGISTRY_EXCEPTION_GET_CHALLENGE_QUESTIONS("20001", "Registry exception while getting challenge question"),
        ERROR_CODE_REGISTRY_EXCEPTION_SET_CHALLENGE_QUESTIONS("20002", "Registry exception while setting challenge question"),
        ERROR_CODE_GETTING_CHALLENGE_URIS("20003", "Error while getting challenge question URIs '%s.'"),
        ERROR_CODE_GETTING_CHALLENGE_QUESTIONS("20004", "Error while getting challenge questions '%s.'"),
        ERROR_CODE_GETTING_CHALLENGE_QUESTION("20005", "Error while getting challenge question '%s.'"),
        ERROR_CODE_QUESTION_OF_USER("20006", "Error setting challenge quesitons of user '%s.'"),
        ERROR_CODE_NO_HASHING_ALGO("20007", "Error while hashing the security answer"),
        ERROR_CODE_INVALID_ANSWER_FOR_SECURITY_QUESTION("20008", "Error while storing recovery data"),
        ERROR_CODE_STORING_RECOVERY_DATA("20009", "Invalid answer for security question"),
        ERROR_CODE_NEED_TO_ANSWER_MORE_SECURITY_QUESTION("20010", "Need to answer more security questions"),
        ERROR_CODE_TRIGGER_NOTIFICATION("20011", "Error while trigger notification for user '%s.'"),
        ERROR_CODE_NEED_TO_ANSWER_TO_REQUESTED_QUESTIONS("20012", "Need to answer to all requested security questions"),
        ERROR_CODE_NO_VALID_USERNAME("20013", "No Valid username found for recovery"),
        ERROR_CODE_NO_FIELD_FOUND_FOR_USER_RECOVERY("20014", "No fileds found for username recovery"),
        ERROR_CODE_NO_USER_FOUND_FOR_RECOVERY("20015", "No valid user found"),
        ERROR_CODE_ISSUE_IN_LOADING_RECOVERY_CONFIGS("20016", "Error loading recovery configs"),
        ERROR_CODE_PASSWORD_BASED_RECOVERY_NOT_ENABLE("20017", "Notification based recovery is not enabled"),
        ERROR_CODE_QUESTION_BASED_RECOVERY_NOT_ENABLE("20017", "Security questions based recovery is not enabled"),;


        private final String code;
        private final String message;

        ErrorMessages(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return code + " - " + message;
        }

    }

    public static class ConnectorConfig {
        public static final String NOTIFICATION_INTERNALLY_MANAGE = "Recovery.notification.internallyManage";
        public static final String NOTIFICATION_BASED_PW_RECOVERY = "Recovery.notification.password.enable";
        public static final String QUESTION_BASED_PW_RECOVERY = "Recovery.question.password.enable";
        public static final String USERNAME_RECOVERY_ENABLE = "Recovery.notification.username.enable";
        public static final String QUESTION_CHALLENGE_SEPARATOR = "Recovery.question.password.separator";
        public static final String QUESTION_MIN_NO_ANSWER = "Recovery.question.password.minAnswers";
        public static final String EXPIRY_TIME = "Recovery.expiryTime";
    }

    public static class SQLQueries {

        public static final String STORE_RECOVERY_DATA = "INSERT INTO IDN_RECOVERY_DATA "
                + "(USER_NAME, USER_DOMAIN, TENANT_ID, CODE, SCENARIO,STEP, TIME_CREATED, REMAINING_SETS)"
                + "VALUES (?,?,?,?,?,?,?,?)";
        public static final String LOAD_RECOVERY_DATA = "SELECT "
                + "* FROM IDN_RECOVERY_DATA WHERE USER_NAME = ? AND USER_DOMAIN = ? AND TENANT_ID = ? AND CODE = ? AND " +
                "SCENARIO = ? AND STEP = ?";

        public static final String INVALIDATE_CODE = "DELETE FROM IDN_RECOVERY_DATA WHERE CODE = ?";

        public static final String INVALIDATE_USER_CODES = "DELETE FROM IDN_RECOVERY_DATA WHERE USER_NAME = ? AND " +
                "USER_DOMAIN = ? AND TENANT_ID =?";

    }

    public static class Questions {

        public static final String LOCALE_CLAIM = "http://wso2.org/claims/locality";

        public static final String CHALLENGE_QUESTION_SET_ID = "questionSetId";
        public static final String CHALLENGE_QUESTION_ID = "questionId";
        public static final String CHALLENGE_QUESTION_LOCALE = "locale";


        // TODO remove this
        public static String[] SECRET_QUESTIONS_SET01 = new String[]{"City where you were born ?",
                "Father's middle name ?", "Favorite food ?", "Favorite vacation location ?"};


        public static String[] SECRET_QUESTIONS_SET01_SIN = new String[]{"ඔබ ඉපදුනු  නගරය?",
                "පියාගේ මැද නම?",
                "ප්‍රියතම කෑම?",
                "නිවාඩුව ගත කිරීමට කැමතිම ස්ථානය?"};

        // TODO remove this
        public static String[] SECRET_QUESTIONS_SET02 = new String[]{"Model of your first car ?",
                "Name of the hospital where you were born ?", "Name of your first pet ?", "Favorite sport ?"};

    }
}