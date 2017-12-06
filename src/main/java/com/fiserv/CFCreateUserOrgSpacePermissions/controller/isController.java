package com.fiserv.CFCreateUserOrgSpacePermissions.controller;

import static com.fiserv.CFCreateUserOrgSpacePermissions.CfCreateUserOrgSpacePermissionsApplication.getHttpClient;
import static com.fiserv.CFCreateUserOrgSpacePermissions.CfCreateUserOrgSpacePermissionsApplication.localhost;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author Michael Hug
 */
@RestController("/api/is")
public class isController {
   
    //Methods for tesing connections and permisions to LDAP and CF
    @GetMapping("/api/is/LDAPConnectionPresent")
    public Map<String, Boolean> isLDAPConnectionPresent() {
        Map<String, Boolean> ret = new HashMap<>();
        ret.put("isLDAPConnectionPresent", socketTest(System.getenv("LDAP_SERVER_ADDRESS"), Integer.parseInt(System.getenv("LDAP_SERVER_PORT"))));
	return ret;
    }

    @GetMapping("/api/is/LDAPUserPresent/{user}")
    public Map<String, Boolean> isLDAPUserPresent(@PathVariable String user) throws NamingException {
        Map<String, Boolean> ret = new HashMap<>();
        SearchControls constraints = new SearchControls();
        constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
        ret.put("isLDAPUserPresent", getLdapContext().search(System.getenv("LDAP_SEARCH_BASE"), "sAMAccountName="+ user, constraints).hasMore());
	return ret;
    }
    
    @GetMapping("/api/is/CFConnectionPresent")
    public Map<String, Boolean> isCFConnectionPresent() {
        Map<String, Boolean> ret = new HashMap<>();
        ret.put("isCFConnectionPresent", socketTest("api."+System.getenv("CF_SERVER_ADDRESS"), Integer.parseInt(System.getenv("CF_SERVER_PORT"))));
        return ret;
    }
    @GetMapping("/api/is/CFAdminReadWritePermissionsPresent")
    public Map<String, Boolean> isAdminCFReadWritePermissionsPresent() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        Map<String, Boolean> ret = new HashMap<>();

        HttpPost httpPost = new HttpPost("https://login."+System.getenv("CF_SERVER_ADDRESS")+"/oauth/token");
        httpPost.addHeader("Accept", "application/json");
        httpPost.addHeader("Authorization", "Basic Y2Y6");
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
         
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("grant_type", "password"));
        params.add(new BasicNameValuePair("password", System.getenv("CF_PASS")));
        params.add(new BasicNameValuePair("scope", ""));
        params.add(new BasicNameValuePair("username", System.getenv("CF_USER")));
        httpPost.setEntity(new UrlEncodedFormEntity(params));
        System.out.println("DEBUG 0");
        try (CloseableHttpClient client = getHttpClient()) {
            System.out.println("DEBUG 1");
            String responseString = EntityUtils.toString(client.execute(httpPost).getEntity());
            System.out.println("DEBUG 2");
            Map<String, String> responseGson = new Gson().fromJson(responseString, Map.class);
            System.out.println("DEBUG 3");
            System.out.println("DEBUG 3.1");
            System.out.println(Arrays.toString(responseGson.entrySet().toArray()));
            System.out.println("DEBUG 3.2");
            String myScope = responseGson.get("scope");
            System.out.println("DEBUG 4");
            System.out.println("enable debug here(and everywhwre) later---dont forget mh 4 Dec 2017");
            System.out.println("DEBUG 5");
            if(myScope.contains("cloud_controller.admin") && myScope.contains("cloud_controller.read") && myScope.contains("cloud_controller.write")) {
                ret.put("isCFReadWritePermissionsPresent", Boolean.TRUE);
            }
            else {
                ret.put("isCFReadWritePermissionsPresent", Boolean.FALSE);
            }
        }
        return ret;
    }
    
    //Methods for validating CF end state
    @GetMapping("/api/is/CFUserPresent/{user}")
    public Map<String, Boolean> isCFUserPresent(@PathVariable String user) {
        Map<String, Boolean> ret = new HashMap<>();
        boolean returnType = new RestTemplate().getForObject(localhost+"/api/get/users/", Map.class).containsKey(user);
        ret.put("isCFUserPresent", new RestTemplate().getForObject(localhost+"/api/get/users/", Map.class).containsKey(user));
        return ret;
    }
    @GetMapping("/api/is/CfOrgPresent/{org}")
    public Map<String,Boolean> isCfOrgPresent(@PathVariable String org) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        Map<String,Boolean> ret = new HashMap<>();
        ret.put("isCfOrgPresent", new RestTemplate().getForObject(localhost+"/api/get/orgs/", Map.class).containsKey(org));
	return ret;

    }
    @GetMapping("/api/is/CfSpacePresent/{org}/{space}")
    public Map<String, Boolean> isCfSpacePresent(@PathVariable String org, @PathVariable String space) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        Map<String,Boolean> ret = new HashMap<>();
        Map<String, String> orgs = new RestTemplate().getForObject(localhost+"/api/get/orgs/", Map.class);
        ret.put("isCfSpacePresent", new RestTemplate().getForObject(localhost+"/api/get/spaces/"+orgs.get(org), Map.class).containsKey(space));
        return ret;
    }
    @GetMapping("/api/is/CFPermissionPresent/{org}/{space}/{user}")
    public Map<String, Boolean> isCFPermissionPresent(@PathVariable String org, @PathVariable String space, @PathVariable String user) {
        boolean orgPresent = false;
        boolean spacePresent = false;
        Map<String, Object> permissions = new RestTemplate().getForObject(localhost+"/api/get/userpermission/"+user, Map.class);
        Map<String, List<Map<String, Map<String, String>>>> crazyMap;
        crazyMap = (Map<String, List<Map<String, Map<String, String>>>>) permissions.get("entity");
        List<Map<String,Map<String, String>>> usersOrgs = crazyMap.get("organizations");
        for( Map<String, Map<String, String>> i : usersOrgs) {
            Map<String, String> myEntity = i.get("entity");
            String myName = myEntity.get("name");
            if(myName.equals(org)) {
                orgPresent = true;
            }
        }
        List<Map<String, Map<String, String>>> usersSpaces = crazyMap.get("spaces");
        for( Map<String, Map<String, String>> i : usersSpaces) {
            Map<String, String> myEntity = i.get("entity");
            String myName = myEntity.get("name");
            if(myName.equals(space)) {
                spacePresent = true;
            }
        }
        
        Map<String, Boolean> ret = new HashMap<>();
        ret.put("isCFUserSpacePermisionPresent", orgPresent && spacePresent);
        return ret;
    }
    
    //private sector
    private LdapContext getLdapContext() {
        LdapContext ctx = null;
        try{
            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY,
                    "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.SECURITY_AUTHENTICATION, "Simple");
            env.put(Context.SECURITY_PRINCIPAL, System.getenv("LDAP_USER"));
            env.put(Context.SECURITY_CREDENTIALS, System.getenv("LDAP_PASS"));
            env.put(Context.PROVIDER_URL, "ldap://"+System.getenv("LDAP_SERVER_ADDRESS")+":"+System.getenv("LDAP_SERVER_PORT"));
            ctx = new InitialLdapContext(env, null);
        } catch(NamingException nex){
            /* returns a null ctx */
        }
        return ctx;
    }
    private boolean socketTest(String address, int port) {
        boolean ret = false;
	try (Socket s = new Socket(address, port)) {
	    ret = true;
	} catch (IOException ex) {
            /* ret remains false */
            System.out.println("Cannot connect to address on port " + address + port);
	}
        return ret;
    }
}
