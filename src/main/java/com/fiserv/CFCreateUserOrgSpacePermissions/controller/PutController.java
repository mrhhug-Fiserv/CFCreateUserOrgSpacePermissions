package com.fiserv.CFCreateUserOrgSpacePermissions.controller;

import static com.fiserv.CFCreateUserOrgSpacePermissions.controller.isController.getCFBearerToken;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Michael Hug
 */
@RestController("/api/put")
public class PutController {
    
    @PutMapping("/api/put/user/{user}")
    public String putUser(@PathVariable String user) {
	return "";
    }
    @PutMapping("/api/put/org/{org}")
    public String putOrg(@PathVariable String org) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        String responseString = "Internal Failure";
        SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                builder.build());
        CloseableHttpClient client = HttpClients.custom().setSSLSocketFactory(
            sslsf).build();
        
        String bearerToken = getCFBearerToken();

        HttpPost httpPost = new HttpPost("https://api."+System.getenv("CF_SERVER_ADDRESS")+"/v2/organizations");
        httpPost.addHeader("Accept", "application/json");
        httpPost.addHeader("Authorization", "Bearer "+bearerToken);
        
        try {
            String payload = "{\"name\":\""+org+"\"}";
            httpPost.setEntity(new ByteArrayEntity(payload.getBytes("UTF-8")));
            CloseableHttpResponse response = client.execute(httpPost);
            HttpEntity entity = response.getEntity();
            responseString = EntityUtils.toString(entity, "UTF-8");
            client.close();
        } catch (IOException ex) {

        }
        
	return responseString;
    }
    @PutMapping("/api/put/space/{org}/{space}")
    public String putSpace(@PathVariable String org, @PathVariable String space) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        String responseString = "Internal Failure";
        Map<String, String> orgs = isController.getAllOrgs();
	SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                builder.build());
        CloseableHttpClient client = HttpClients.custom().setSSLSocketFactory(
            sslsf).build();
        
        String bearerToken = getCFBearerToken();

        HttpPost httpPost = new HttpPost("https://api."+System.getenv("CF_SERVER_ADDRESS")+"/v2/spaces?async=true&inline-relations-depth=1");
        httpPost.addHeader("Accept", "application/json");
        httpPost.addHeader("Authorization", "Bearer "+bearerToken);
        
        try {
            String payload = "{\"name\":\""+space+"\",\"organization_guid\":\""+orgs.get(org)+"\"}";
            httpPost.setEntity(new ByteArrayEntity(payload.getBytes("UTF-8")));
            CloseableHttpResponse response = client.execute(httpPost);
            HttpEntity entity = response.getEntity();
            responseString = EntityUtils.toString(entity, "UTF-8");
            client.close();
        } catch (IOException ex) {

        }
        
	return responseString;
    }
    @PutMapping("/api/put/permision/{org}/{space}/{user}")
    public String putPermission(@PathVariable String org, @PathVariable String space, @PathVariable String user) {
	return "";
    }
}
