package com.fiserv.CFCreateUserOrgSpacePermissions.controller;

import GsonDefinitions.CFAPIResponse;
import GsonDefinitions.resource;
import static com.fiserv.CFCreateUserOrgSpacePermissions.CfCreateUserOrgSpacePermissionsApplication.getCFBearerToken;
import static com.fiserv.CFCreateUserOrgSpacePermissions.CfCreateUserOrgSpacePermissionsApplication.getHttpClient;
import com.google.gson.Gson;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author michael.hug@fiserv.com
 */
@RestController("/api/get")
public class GetController {

    private final String baseUrl = "https://api."+System.getenv("CF_SERVER_ADDRESS");
    
    @GetMapping("/api/get/users/")
    public Map<String, String> getUsers() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException { 
        Map<String, String> ret = new HashMap<>();
        for( resource i : pagenate("/v2/users")) {
            ret.put(i.entity.username, i.metadata.guid);
        }
        ret.remove(null); // spring exploded on the null key
        return ret;
    }
    
    @GetMapping("/api/get/orgs/")
    public Map<String, String> getOrgs() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        Map<String, String> ret = new HashMap<>();
        for( resource i : pagenate("/v2/organizations")) {
            ret.put(i.entity.name, i.metadata.guid);
        }
        return ret;
    }
    
    @GetMapping("/api/get/spaces/{orgGuid}")
    public Map<String, String> getSpaces(@PathVariable String orgGuid) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException { 
        Map<String, String> ret = new HashMap<>();
        for( resource i : pagenate("/v2/organizations/"+orgGuid+"/spaces")) {
            ret.put(i.entity.name, i.metadata.guid);
        }
        return ret;
    }
    
    @GetMapping("/api/get/userpermission/{user}")
    public Map<String, Object> getUserPermission(@PathVariable String user) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException { 
        Map<String, String> users = getUsers();
        String extendedUrl = "/v2/users/"+users.get(user)+"/summary";
        
        HttpGet httpGet = getHTTPGET(baseUrl, extendedUrl);
        Map<String, Object> responseGson;
        String responseString;
        try (CloseableHttpClient client = getHttpClient()) {
            responseString = EntityUtils.toString(client.execute(httpGet).getEntity());
            responseGson = new Gson().fromJson(responseString, Map.class);
        }
        return responseGson;
    }
    
    @GetMapping("/api/get/BU")
    public Map<String, String> getBUs() {
        return new Gson().fromJson(System.getenv("BU"), Map.class);
    }
    
    
    private HttpGet getHTTPGET(String baseUrl, String extendedUrl) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        HttpGet httpGet = new HttpGet(baseUrl+extendedUrl);
        httpGet.addHeader("Accept", "application/json");
        httpGet.addHeader("Authorization", "Bearer "+ getCFBearerToken());
        return httpGet;
    }
    private Set<resource> pagenate(String extendedUrl ) throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        Set<resource> ret = new HashSet<>();
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
        return ret;
    }
}
