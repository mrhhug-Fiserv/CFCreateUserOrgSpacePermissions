package com.fiserv.CFCreateUserOrgSpacePermissions;

import com.google.gson.Gson;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CfCreateUserOrgSpacePermissionsApplication {
    
    public static String localhost = "http://localhost:" + System.getenv("PORT");
    
    public static void main(String[] args) {
        SpringApplication.run(CfCreateUserOrgSpacePermissionsApplication.class, args);
    }
    
    //you don't need to get this every time, but it does go stale
    public static String getCFBearerToken() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        String ret = "";

        SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                builder.build());
        CloseableHttpClient client = HttpClients.custom().setSSLSocketFactory(
            sslsf).build();
        
        HttpPost httpPost = new HttpPost("https://login."+System.getenv("CF_SERVER_ADDRESS")+"/oauth/token");
        httpPost.addHeader("Accept", "application/json");
        httpPost.addHeader("Authorization", "Basic Y2Y6");
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("grant_type", "password"));
        params.add(new BasicNameValuePair("password", System.getenv("CF_PASS")));
        params.add(new BasicNameValuePair("scope", ""));
        params.add(new BasicNameValuePair("username", System.getenv("CF_USER")));
              
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            CloseableHttpResponse response = client.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            Map responseGson = new Gson().fromJson(responseString, Map.class);
            ret = (String) responseGson.get("access_token");
            
            client.close();
        } catch (IOException ex) {
            
        }
	return ret;
    }
}
