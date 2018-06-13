package com.fiserv.CFCreateUserOrgSpacePermissions.controller;

import GsonDefinitions.CFAPIResponse;
import GsonDefinitions.resource;
import static com.fiserv.CFCreateUserOrgSpacePermissions.controller.Main.getCFBearerToken;
import static com.fiserv.CFCreateUserOrgSpacePermissions.controller.Main.getHttpClient;
import com.google.gson.Gson;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author michael.hug@fiserv.com
 */
@RestController("/api/")
public class Controller{
    private final String devSpaceName = "development";
    private final String baseUrl = "https://api."+System.getenv("CF_SYS");

    @PutMapping("/api/put/putAll/{orgGuid}/{user}")
    public Map<String, String> putAll(@PathVariable String orgGuid, @PathVariable String user) {
        Map<String, String> ret = new HashMap();
        try {
            putUser(user);
            String spaceGuid = putDevSpace(orgGuid);
            putOrgUser(orgGuid, user);
            putSpacedev(orgGuid, spaceGuid, user);
            ret.put("api_endpoint", "api."+System.getenv("CF_SYS"));
            ret.put("space", devSpaceName);
            ret.put("username", user);
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException | IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }
    
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
    
    private String putUser(String user) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {        
        //this schmoe might already have an account
        Map<String, String> allUsers = getUsers();
        if(allUsers.containsKey(user)) {
            return allUsers.get("user");
        }
        
        String hpptPost = "https://uaa."+System.getenv("CF_SYS")+"/Users";
        HttpPost httpPost = new HttpPost(hpptPost);
        populateHttpEntityEnclosingRequestBase(httpPost);
        //this is basically what the cf-cli does. its really hacky, but thats how it works for ldap users
        String payload = "{\"emails\":[{\"primary\": true,\"value\": \""+user+"\"}],"
                + "\"name\": {\"familyName\": \""+user+"\",\"givenName\": \""+user+"\"},"
                + "\"origin\": \"ldap\",\"password\": \"[PRIVATE DATA HIDDEN]\","
                + "\"userName\": \""+user+"\"}";
        httpPost.setEntity(new ByteArrayEntity(payload.getBytes("UTF-8")));
        //i know i shouldn't cast things...
	return (String) executeHttpCall(httpPost).get("id");
    }
    
    private String putDevSpace(String orgGuid) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        Map<String, String> allSpaces = getSpaces(orgGuid);
        if( allSpaces.containsKey(devSpaceName)) {
            return allSpaces.get(devSpaceName);
        }
        HttpPost httpPost = new HttpPost("https://api."+System.getenv("CF_SYS")+"/v2/spaces?async=true&inline-relations-depth=1");
        populateHttpEntityEnclosingRequestBase(httpPost);
        String payload = "{\"name\":\""+devSpaceName+"\",\"organization_guid\":\""+orgGuid+"\"}";
        httpPost.setEntity(new ByteArrayEntity(payload.getBytes("UTF-8")));

        Map<String, String> almostreturn = (Map<String, String>) executeHttpCall(httpPost).get("metadata");
	return almostreturn.get("guid");
    }
    
    private Map<String, Object> putOrgUser(String orgGuid, String user) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException, IOException {       
        String url = "https://api."+System.getenv("CF_SYS")+"/v2/organizations/"+orgGuid+"/users";
        HttpPut httpPut = new HttpPut(url);
        populateHttpEntityEnclosingRequestBase(httpPut);
        
        String payload = "{\"username\": \""+user+"\"}";
        httpPut.setEntity(new ByteArrayEntity(payload.getBytes("UTF-8")));

        return executeHttpCall(httpPut);
         /*
        only valid response
        {"metadata":{"guid":"92d0d39d-5061-40c6-bb52-5111b5bbc3ac","url":"/v2/organizations/92d0d39d-5061-40c6-bb52-5111b5bbc3ac","created_at":"2016-02-05T14:45:27Z","updated_at":null},"entity":{"name":"mhug-org","billing_enabled":false,"quota_definition_guid":"adaaf229-00ef-4391-b4af-c54bb722071b","status":"active","default_isolation_segment_guid":null,"quota_definition_url":"/v2/quota_definitions/adaaf229-00ef-4391-b4af-c54bb722071b","spaces_url":"/v2/organizations/92d0d39d-5061-40c6-bb52-5111b5bbc3ac/spaces","domains_url":"/v2/organizations/92d0d39d-5061-40c6-bb52-5111b5bbc3ac/domains","private_domains_url":"/v2/organizations/92d0d39d-5061-40c6-bb52-5111b5bbc3ac/private_domains","users_url":"/v2/organizations/92d0d39d-5061-40c6-bb52-5111b5bbc3ac/users","managers_url":"/v2/organizations/92d0d39d-5061-40c6-bb52-5111b5bbc3ac/managers","billing_managers_url":"/v2/organizations/92d0d39d-5061-40c6-bb52-5111b5bbc3ac/billing_managers","auditors_url":"/v2/organizations/92d0d39d-5061-40c6-bb52-5111b5bbc3ac/auditors","app_events_url":"/v2/organizations/92d0d39d-5061-40c6-bb52-5111b5bbc3ac/app_events","space_quota_definitions_url":"/v2/organizations/92d0d39d-5061-40c6-bb52-5111b5bbc3ac/space_quota_definitions"}}
        */
    }
    
    private Map<String, Object> putSpacedev(String orgGuid, String spaceGuid, String user) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException, IOException {
        String url = "https://api."+System.getenv("CF_SYS")+"/v2/spaces/"+spaceGuid+"/developers";
        HttpPut httpPut = new HttpPut(url);
        populateHttpEntityEnclosingRequestBase(httpPut);
        
        String payload = "{\"username\": \""+user+"\"}";
        httpPut.setEntity(new ByteArrayEntity(payload.getBytes("UTF-8")));

        return executeHttpCall(httpPut);
        //{"metadata":{"guid":"b77b933a-c72c-4da9-a54f-6c260f37ca4e","url":"/v2/spaces/b77b933a-c72c-4da9-a54f-6c260f37ca4e","created_at":"2017-11-28T13:27:12Z","updated_at":"2017-11-28T13:27:12Z"},"entity":{"name":"sandbox","organization_guid":"92d0d39d-5061-40c6-bb52-5111b5bbc3ac","space_quota_definition_guid":null,"isolation_segment_guid":null,"allow_ssh":true,"organization_url":"/v2/organizations/92d0d39d-5061-40c6-bb52-5111b5bbc3ac","developers_url":"/v2/spaces/b77b933a-c72c-4da9-a54f-6c260f37ca4e/developers","managers_url":"/v2/spaces/b77b933a-c72c-4da9-a54f-6c260f37ca4e/managers","auditors_url":"/v2/spaces/b77b933a-c72c-4da9-a54f-6c260f37ca4e/auditors","apps_url":"/v2/spaces/b77b933a-c72c-4da9-a54f-6c260f37ca4e/apps","routes_url":"/v2/spaces/b77b933a-c72c-4da9-a54f-6c260f37ca4e/routes","domains_url":"/v2/spaces/b77b933a-c72c-4da9-a54f-6c260f37ca4e/domains","service_instances_url":"/v2/spaces/b77b933a-c72c-4da9-a54f-6c260f37ca4e/service_instances","app_events_url":"/v2/spaces/b77b933a-c72c-4da9-a54f-6c260f37ca4e/app_events","events_url":"/v2/spaces/b77b933a-c72c-4da9-a54f-6c260f37ca4e/events","security_groups_url":"/v2/spaces/b77b933a-c72c-4da9-a54f-6c260f37ca4e/security_groups","staging_security_groups_url":"/v2/spaces/b77b933a-c72c-4da9-a54f-6c260f37ca4e/staging_security_groups"}}
    }
    
    private HttpEntityEnclosingRequestBase populateHttpEntityEnclosingRequestBase(HttpEntityEnclosingRequestBase par) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException { 
        par.addHeader("Accept", "application/json");
        par.addHeader("Authorization", "Bearer "+getCFBearerToken());
        par.addHeader("Content-Type", "application/json");
        return par;
    }
    
    private Map<String, Object> executeHttpCall(HttpEntityEnclosingRequestBase method) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        Map<String, Object> ret;
        try (CloseableHttpClient client = getHttpClient()) {
            CloseableHttpResponse response = client.execute(method);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity);
            ret = new Gson().fromJson(responseString, Map.class);
        }
        return ret;
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