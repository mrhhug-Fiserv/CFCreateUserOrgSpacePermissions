package com.fiserv.CFCreateUserOrgSpacePermissions.controller;

import GsonDefinitions.CFAPIResponse;
import GsonDefinitions.resource;
import static com.fiserv.CFCreateUserOrgSpacePermissions.CfCreateUserOrgSpacePermissionsApplication.getCFBearerToken;
import com.google.gson.Gson;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author michael.hug@fiserv.com
 * Fiserv Internal Software
 */
@RestController("/api/get")
public class GetController {

    private final String baseUrl = "https://api."+System.getenv("CF_SERVER_ADDRESS");
    
    @GetMapping("/api/get/orgs/")
    public Set<resource> getOrgs() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        Set<resource> ret = new HashSet<>();
        pagenate("/v2/organizations", ret);
        return ret;
    }
    
    @GetMapping("/api/get/spaces/{orgGuid}")
    public Set<resource> getSpaces(@PathVariable String orgGuid) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException { 
        Set<resource> ret = new HashSet<>();
        //String extendedUrl = "/v2/organizations/"+orgGuid+"/spaces?results-per-page=3";
        pagenate("/v2/organizations/"+orgGuid+"/spaces", ret);
        return ret;
    }
    
    private CloseableHttpClient getHttpClient() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                builder.build());
        return HttpClients.custom().setSSLSocketFactory(sslsf).build();
    }
    
    private HttpGet getHTTPGET(String baseUrl, String extendedUrl) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        HttpGet httpGet = new HttpGet(baseUrl+extendedUrl);
        httpGet.addHeader("Accept", "application/json");
        httpGet.addHeader("Authorization", "Bearer "+ getCFBearerToken());
        return httpGet;
    }
    
    private void pagenate(String extendedUrl, Set<resource> ret) throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        while(null != extendedUrl) {
            HttpGet httpGet = getHTTPGET(baseUrl, extendedUrl);
            CFAPIResponse responseGson;
            try (CloseableHttpClient client = getHttpClient()) {
                String responseString = EntityUtils.toString(client.execute(httpGet).getEntity());
                responseGson = new Gson().fromJson(responseString, CFAPIResponse.class);
            }
            extendedUrl = responseGson.next_url;
            ret.addAll(responseGson.resources);
        }
    }
}
