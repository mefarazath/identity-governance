package org.wso2.carbon.identity.recovery.ui;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.wso2.carbon.identity.recovery.stub.model.ChallengeQuestion;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class Utils {

    public static final String DEFAULT_LOCALE = "en_US";
    public static final String WSO2_CLAIM_DIALECT = "http://wso2.org/claims/";

    private Utils() {
    }

    public static String getLocaleString(Locale locale) {
        String languageCode = locale.getLanguage();
        String countryCode = locale.getCountry();
        if (StringUtils.isBlank(countryCode)) {
            countryCode = languageCode;
        }

        return languageCode + "_" + countryCode;
    }

    public static Comparator<ChallengeQuestion> ChallengeQuestionComparator = new Comparator<ChallengeQuestion>() {
        public int compare(ChallengeQuestion q1, ChallengeQuestion q2) {
            String stringQ1 = q1.getQuestionId() + q1.getLocale();
            String stringQ2 = q2.getQuestionId() + q2.getLocale();
            //ascending order
            return stringQ1.compareTo(stringQ2);
        }
    };

    public static List<String> getChallengeSetUris(ChallengeQuestion[] challengeQuestions) {
        HashSet<String> questionSetNames = new HashSet<String>();
        if (ArrayUtils.isNotEmpty(challengeQuestions)) {
            for (ChallengeQuestion question : challengeQuestions) {
                if (question.getQuestionSetId() != null) {
                    questionSetNames.add(question.getQuestionSetId());
                }
            }
        }
        return new ArrayList<String>(questionSetNames);
    }
}