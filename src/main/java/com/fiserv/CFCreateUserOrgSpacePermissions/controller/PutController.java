package com.fiserv.CFCreateUserOrgSpacePermissions.controller;

import static com.fiserv.CFCreateUserOrgSpacePermissions.CfCreateUserOrgSpacePermissionsApplication.getCFBearerToken;
import static com.fiserv.CFCreateUserOrgSpacePermissions.CfCreateUserOrgSpacePermissionsApplication.getHttpClient;
import com.google.gson.Gson;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author michael.hug@fiserv.com
 * Fiserv Internal Software
 */
@RestController("/api/put")
public class PutController{ 

    @PutMapping("/api/put/user/{user}")
    public Map<String, Object> putUser(@PathVariable String user) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        HttpPost httpPost = new HttpPost("https://uaa."+System.getenv("CF_SERVER_ADDRESS")+"/Users");
        populateHttpEntityEnclosingRequestBase(httpPost);
        
        String payload = "{\"emails\":[{\"primary\": true,\"value\": \""+user+"\"}],"
                + "\"name\": {\"familyName\": \""+user+"\",\"givenName\": \""+user+"\"},"
                + "\"origin\": \"ldap\",\"password\": \"[PRIVATE DATA HIDDEN]\","
                + "\"userName\": \""+user+"\"}";
        httpPost.setEntity(new ByteArrayEntity(payload.getBytes("UTF-8")));
            
        /*
        two different types of responses from uaa api - in both cases the end result is that user exists
        {"id":"d66e00f4-96d7-4196-865a-33a0ebb77f5b","meta":{"version":0.0,"created":"2017-11-28T12:51:26.000Z","lastModified":"2017-11-28T12:51:26.000Z"},"userName":"USERNAME","name":{"familyName":"USERNAME","givenName":"USERNAME"},"emails":[{"value":"USERNAME","primary":false}],"groups":[{"value":"58d8370c-135d-4c23-a93c-2665344314d2","display":"password.write","type":"DIRECT"},{"value":"f5f44a38-8e0f-41a5-a972-b514081136c6","display":"uaa.user","type":"DIRECT"},{"value":"81d520ef-0b1d-4864-a741-ff04fc18ac05","display":"openid","type":"DIRECT"},{"value":"3baa6d37-b18f-43a4-9cb2-1ed2cf68cd27","display":"oauth.approvals","type":"DIRECT"},{"value":"9d0d08fb-e8b9-44ff-b137-652ef9d1fb56","display":"notification_preferences.read","type":"DIRECT"},{"value":"46d1b48b-3d6c-4837-8f6c-93b5c7a49c87","display":"notification_preferences.write","type":"DIRECT"},{"value":"e7cd7759-d90c-46cd-ac35-f6f935a3d3bc","display":"cloud_controller_service_permissions.read","type":"DIRECT"},{"value":"1b1cfdea-8c9d-41c0-817e-5cbcc0c40c50","display":"roles","type":"DIRECT"},{"value":"a08e2367-39ab-4d94-9899-e3c0251fc341","display":"scim.me","type":"DIRECT"},{"value":"be2c1886-bfdd-4490-8d08-e08a5803afcb","display":"cloud_controller.read","type":"DIRECT"},{"value":"f167eed8-f190-4a07-8692-b503f1819ba8","display":"cloud_controller.write","type":"DIRECT"},{"value":"33aa2492-54b9-4f86-b64e-01d4cef149ed","display":"cloud_controller.user","type":"DIRECT"},{"value":"a4e5db06-f9e3-4615-8164-06f875689d56","display":"profile","type":"DIRECT"},{"value":"be939d99-1700-45f9-b9f2-117f9877da89","display":"actuator.read","type":"DIRECT"},{"value":"58b9043b-67a4-4e2b-8c31-083297ee8793","display":"user_attributes","type":"DIRECT"},{"value":"69dafe2e-12b2-4afe-a03a-43d897cbb07a","display":"uaa.offline_token","type":"DIRECT"},{"value":"1614c9d8-7b2b-4d7b-bc35-a2ac43b48278","display":"approvals.me","type":"DIRECT"}],"approvals":[],"active":true,"verified":true,"origin":"ldap","zoneId":"uaa","passwordLastModified":"2017-11-28T12:51:26.000Z","schemas":["urn:scim:schemas:core:1.0"]}
        {"user_id":"5303ab33-d643-4178-92eb-08400e6880ae","error_description":"Username already in use: mhug","verified":true,"active":true,"error":"scim_resource_already_exists","message":"Username already in use: mhug"}
        */
	return executeHttpCall(httpPost);
    }
    
    @PutMapping("/api/put/org/{org}")
    public Map<String, Object> putOrg(@PathVariable String org) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        HttpPost httpPost = new HttpPost("https://api."+System.getenv("CF_SERVER_ADDRESS")+"/v2/organizations");
        populateHttpEntityEnclosingRequestBase(httpPost);
        
        String payload = "{\"name\":\""+org+"\"}";
        httpPost.setEntity(new ByteArrayEntity(payload.getBytes("UTF-8")));
            
        /*
        two different types of responses from api - in both cases the end result is that org exists
        {"description":"The organization name is taken: mhug-org","error_code":"CF-OrganizationNameTaken","code":30002.0}
        {"metadata":{"guid":"d1e9be33-c8f0-44b2-9f03-8f42e07ae82c","url":"/v2/organizations/d1e9be33-c8f0-44b2-9f03-8f42e07ae82c","created_at":"2017-11-28T13:04:26Z","updated_at":"2017-11-28T13:04:26Z"},"entity":{"name":"mhug-orgg","billing_enabled":false,"quota_definition_guid":"adaaf229-00ef-4391-b4af-c54bb722071b","status":"active","default_isolation_segment_guid":null,"quota_definition_url":"/v2/quota_definitions/adaaf229-00ef-4391-b4af-c54bb722071b","spaces_url":"/v2/organizations/d1e9be33-c8f0-44b2-9f03-8f42e07ae82c/spaces","domains_url":"/v2/organizations/d1e9be33-c8f0-44b2-9f03-8f42e07ae82c/domains","private_domains_url":"/v2/organizations/d1e9be33-c8f0-44b2-9f03-8f42e07ae82c/private_domains","users_url":"/v2/organizations/d1e9be33-c8f0-44b2-9f03-8f42e07ae82c/users","managers_url":"/v2/organizations/d1e9be33-c8f0-44b2-9f03-8f42e07ae82c/managers","billing_managers_url":"/v2/organizations/d1e9be33-c8f0-44b2-9f03-8f42e07ae82c/billing_managers","auditors_url":"/v2/organizations/d1e9be33-c8f0-44b2-9f03-8f42e07ae82c/auditors","app_events_url":"/v2/organizations/d1e9be33-c8f0-44b2-9f03-8f42e07ae82c/app_events","space_quota_definitions_url":"/v2/organizations/d1e9be33-c8f0-44b2-9f03-8f42e07ae82c/space_quota_definitions"}}
        */
	return executeHttpCall(httpPost);
    }
    
    @PutMapping("/api/put/space/{org}/{space}")
    public Map<String, Object> putSpace(@PathVariable String org, @PathVariable String space) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        HttpPost httpPost = new HttpPost("https://api."+System.getenv("CF_SERVER_ADDRESS")+"/v2/spaces?async=true&inline-relations-depth=1");
        populateHttpEntityEnclosingRequestBase(httpPost);

        Map<String, String> orgs = new RestTemplate().getForObject(
                "http://localhost:"+System.getenv("PORT")+"/api/get/orgs/", 
                Map.class);
        String payload = "{\"name\":\""+space+"\",\"organization_guid\":\""+orgs.get(org)+"\"}";
        httpPost.setEntity(new ByteArrayEntity(payload.getBytes("UTF-8")));
            
        /*
        two different types of responses from api - in both cases the end result is that space exists
        {"description":"The app space name is taken: sandbox","error_code":"CF-SpaceNameTaken","code":40002.0}
        {"metadata":{"guid":"b77b933a-c72c-4da9-a54f-6c260f37ca4e","url":"/v2/spaces/b77b933a-c72c-4da9-a54f-6c260f37ca4e","created_at":"2017-11-28T13:27:12Z","updated_at":"2017-11-28T13:27:12Z"},"entity":{"name":"sandbox","organization_guid":"92d0d39d-5061-40c6-bb52-5111b5bbc3ac","space_quota_definition_guid":null,"isolation_segment_guid":null,"allow_ssh":true,"organization_url":"/v2/organizations/92d0d39d-5061-40c6-bb52-5111b5bbc3ac","organization":{"metadata":{"guid":"92d0d39d-5061-40c6-bb52-5111b5bbc3ac","url":"/v2/organizations/92d0d39d-5061-40c6-bb52-5111b5bbc3ac","created_at":"2016-02-05T14:45:27Z","updated_at":null},"entity":{"name":"mhug-org","billing_enabled":false,"quota_definition_guid":"adaaf229-00ef-4391-b4af-c54bb722071b","status":"active","default_isolation_segment_guid":null,"quota_definition_url":"/v2/quota_definitions/adaaf229-00ef-4391-b4af-c54bb722071b","spaces_url":"/v2/organizations/92d0d39d-5061-40c6-bb52-5111b5bbc3ac/spaces","domains_url":"/v2/organizations/92d0d39d-5061-40c6-bb52-5111b5bbc3ac/domains","private_domains_url":"/v2/organizations/92d0d39d-5061-40c6-bb52-5111b5bbc3ac/private_domains","users_url":"/v2/organizations/92d0d39d-5061-40c6-bb52-5111b5bbc3ac/users","managers_url":"/v2/organizations/92d0d39d-5061-40c6-bb52-5111b5bbc3ac/managers","billing_managers_url":"/v2/organizations/92d0d39d-5061-40c6-bb52-5111b5bbc3ac/billing_managers","auditors_url":"/v2/organizations/92d0d39d-5061-40c6-bb52-5111b5bbc3ac/auditors","app_events_url":"/v2/organizations/92d0d39d-5061-40c6-bb52-5111b5bbc3ac/app_events","space_quota_definitions_url":"/v2/organizations/92d0d39d-5061-40c6-bb52-5111b5bbc3ac/space_quota_definitions"}},"developers_url":"/v2/spaces/b77b933a-c72c-4da9-a54f-6c260f37ca4e/developers","developers":[],"managers_url":"/v2/spaces/b77b933a-c72c-4da9-a54f-6c260f37ca4e/managers","managers":[],"auditors_url":"/v2/spaces/b77b933a-c72c-4da9-a54f-6c260f37ca4e/auditors","auditors":[],"apps_url":"/v2/spaces/b77b933a-c72c-4da9-a54f-6c260f37ca4e/apps","apps":[],"routes_url":"/v2/spaces/b77b933a-c72c-4da9-a54f-6c260f37ca4e/routes","routes":[],"domains_url":"/v2/spaces/b77b933a-c72c-4da9-a54f-6c260f37ca4e/domains","domains":[{"metadata":{"guid":"a460f839-477d-4e0d-a00b-b06eb2078a80","url":"/v2/shared_domains/a460f839-477d-4e0d-a00b-b06eb2078a80","created_at":"2015-10-28T15:18:30Z","updated_at":"2017-11-16T01:38:33Z"},"entity":{"name":"cloudfoundry.onefiserv.net","router_group_guid":null,"router_group_type":null}},{"metadata":{"guid":"65842fa8-3f2d-4dcc-90bf-aee6813a8066","url":"/v2/shared_domains/65842fa8-3f2d-4dcc-90bf-aee6813a8066","created_at":"2017-11-02T13:47:41Z","updated_at":"2017-11-02T13:47:41Z"},"entity":{"name":"tcp.cloudfoundry.onefiserv.net","router_group_guid":"3f8b68ab-3d42-4a18-591f-c9e9e7dbafd4","router_group_type":null}}],"service_instances_url":"/v2/spaces/b77b933a-c72c-4da9-a54f-6c260f37ca4e/service_instances","service_instances":[],"app_events_url":"/v2/spaces/b77b933a-c72c-4da9-a54f-6c260f37ca4e/app_events","events_url":"/v2/spaces/b77b933a-c72c-4da9-a54f-6c260f37ca4e/events","security_groups_url":"/v2/spaces/b77b933a-c72c-4da9-a54f-6c260f37ca4e/security_groups","security_groups":[{"metadata":{"guid":"90e68f5a-93ca-4641-9140-bec087cf1a29","url":"/v2/security_groups/90e68f5a-93ca-4641-9140-bec087cf1a29","created_at":"2015-10-28T15:18:30Z","updated_at":"2016-01-07T13:29:11Z"},"entity":{"name":"all_open","rules":[{"protocol":"all","destination":"0.0.0.0-255.255.255.255"}],"running_default":true,"staging_default":true,"spaces_url":"/v2/security_groups/90e68f5a-93ca-4641-9140-bec087cf1a29/spaces","staging_spaces_url":"/v2/security_groups/90e68f5a-93ca-4641-9140-bec087cf1a29/staging_spaces"}}],"staging_security_groups_url":"/v2/spaces/b77b933a-c72c-4da9-a54f-6c260f37ca4e/staging_security_groups","staging_security_groups":[{"metadata":{"guid":"90e68f5a-93ca-4641-9140-bec087cf1a29","url":"/v2/security_groups/90e68f5a-93ca-4641-9140-bec087cf1a29","created_at":"2015-10-28T15:18:30Z","updated_at":"2016-01-07T13:29:11Z"},"entity":{"name":"all_open","rules":[{"protocol":"all","destination":"0.0.0.0-255.255.255.255"}],"running_default":true,"staging_default":true,"spaces_url":"/v2/security_groups/90e68f5a-93ca-4641-9140-bec087cf1a29/spaces","staging_spaces_url":"/v2/security_groups/90e68f5a-93ca-4641-9140-bec087cf1a29/staging_spaces"}}]}}
        */
	return executeHttpCall(httpPost);
    }
    
    @PutMapping("/api/put/orguser/{org}/{user}")
    public Map<String, Object> putOrgUser(@PathVariable String org, @PathVariable String user) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException, IOException {
        Map<String, String> orgs = new RestTemplate().getForObject(
                "http://localhost:"+System.getenv("PORT")+"/api/get/orgs/", 
                Map.class);
        String orgGuid = orgs.get(org);
        
        String url = "https://api."+System.getenv("CF_SERVER_ADDRESS")+"/v2/organizations/"+orgGuid+"/users";
        HttpPut httpPut = new HttpPut(url);
        populateHttpEntityEnclosingRequestBase(httpPut);
        
        String payload = "{\"username\": \""+user+"\"}";
        httpPut.setEntity(new ByteArrayEntity(payload.getBytes("UTF-8")));

        return executeHttpCall(httpPut);
    }
    
    @PutMapping("/api/put/spacedev/{org}/{space}/{user}")
    public Map<String, Object> putSpacedev(@PathVariable String org, @PathVariable String space, @PathVariable String user) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException, IOException {
        Map<String, String> orgs = new RestTemplate().getForObject(
                "http://localhost:"+System.getenv("PORT")+"/api/get/orgs/", 
                Map.class);
        String orgGuid = orgs.get(org);
        
        Map<String, String> spaces = new RestTemplate().getForObject(
                "http://localhost:"+System.getenv("PORT")+"/api/get/spaces/"+orgGuid, 
                Map.class);
        String spaceGuid = spaces.get(space);

        String url = "https://api."+System.getenv("CF_SERVER_ADDRESS")+"/v2/spaces/"+spaceGuid+"/developers";
        HttpPut httpPut = new HttpPut(url);
        populateHttpEntityEnclosingRequestBase(httpPut);
        
        String payload = "{\"username\": \""+user+"\"}";
        httpPut.setEntity(new ByteArrayEntity(payload.getBytes("UTF-8")));

        return executeHttpCall(httpPut);
    }
    
    
    @PutMapping("/api/put/orgmanager/{org}/{user}")
    public Map<String, Object> putOrgManager(@PathVariable String org, @PathVariable String user) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        Map<String, String> orgs = new RestTemplate().getForObject(
                "http://localhost:"+System.getenv("PORT")+"/api/get/orgs/", 
                Map.class);
        String orgGuid = orgs.get(org);
        
        String url = "https://api."+System.getenv("CF_SERVER_ADDRESS")+"/v2/organizations/"+orgGuid+"/managers";
        HttpPut httpPut = new HttpPut(url);
        populateHttpEntityEnclosingRequestBase(httpPut);
        
        String payload = "{\"username\": \""+user+"\"}";
        httpPut.setEntity(new ByteArrayEntity(payload.getBytes("UTF-8")));

        return executeHttpCall(httpPut);
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
    
}
