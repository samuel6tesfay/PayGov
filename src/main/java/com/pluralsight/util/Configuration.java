package com.pluralsight.util;

import java.util.HashMap;
import java.util.Map;

/**
 *  For a full list of configuration parameters refer in wiki page.(https://github.com/paypal/sdk-core-java/blob/master/README.md)
 */
public class Configuration {

    private Configuration() {}

    public static Map<String, String> getAcctAndConfig() {
        Map<String, String> configMap = new HashMap<>(getConfig());

        configMap.put("acct1.UserName", "sb-6nobw17519147_api1.business.example.com");
        configMap.put("acct1.Password", "ETXHQ9JHGHAN9KJD");
        configMap.put("acct1.Signature", "AgPVKok55QKjI0jenqTH.wU7KKhEAcPHKq4N9OkNM9ypiBi24wZ-Wlrl");

        /** Subject is optional, only required in case of third party permission
		 *   configMap.put("acct1.Subject", "");

         * Sample Certificate credential
         * configMap.put("acct2.UserName", "certuser_biz_api1.paypal.com");
         * configMap.put("acct2.Password", "D6JNKKULHN3G5B8A");
         * configMap.put("acct2.CertKey", "password");
         * configMap.put("acct2.CertPath", "resource/sdk-cert.p12");
         * configMap.put("acct2.AppId", "APP-80W284485P519543T");
         */

        return configMap;
    }

    public static Map<String, String> getConfig() {
        Map<String, String> configMap = new HashMap<>();
        configMap.put("mode", "sandbox");

        /** These values are defaulted in SDK. If you want to override default values, uncomment it and add your value.
         * configMap.put("http.ConnectionTimeOut", "5000");
         * configMap.put("http.Retry", "2");
         * configMap.put("http.ReadTimeOut", "30000");
         * configMap.put("http.MaxConnection", "100");
         */
        return configMap;
    }
}