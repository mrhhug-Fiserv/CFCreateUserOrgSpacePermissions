package com.fiserv.CFCreateUserOrgSpacePermissions.controller;

import static com.fiserv.CFCreateUserOrgSpacePermissions.CfCreateUserOrgSpacePermissionsApplication.localhost;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
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
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.BasicAttributes;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
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
        ret.put("isLDAPUserPresent", getLdapContext().search(System.getenv("LDAP_SEARCH_BASE"), new BasicAttributes("sAMAccountName=", user)).hasMore());
	return ret;
    }
    @GetMapping("/api/is/CFConnectionPresent")
    public Map<String, Boolean> isCFConnectionPresent() {
        Map<String, Boolean> ret = new HashMap<>();
        ret.put("isCFConnectionPresent", socketTest(System.getenv("CF_SERVER_ADDRESS"), Integer.parseInt(System.getenv("CF_SERVER_PORT"))));
        return ret;
    }
    @GetMapping("/api/is/CFReadWritePermissionsPresent")
    public String isCFReadWritePermissionsPresent() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        boolean ret = false;

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
            String myScope = (String) responseGson.get("scope");
            List<String> splitted = Arrays.asList(myScope.split("\\s+"));
            if(splitted.contains("cloud_controller.admin") && splitted.contains("cloud_controller.read") && splitted.contains("cloud_controller.write")) {
                ret = true;
            }
            client.close();
        } catch (IOException ex) {
            
        }
	return "{'isCFReadWritePermissionsPresent':"+ret+"}";
    }
    
    //Methods for validating CF end state
    @GetMapping("/api/is/CFUserPresent/{user}")
    public String isCFUserPresent(@PathVariable String user) {
	//TODO
        return "{'isCFUserPresent':";
    }
    @GetMapping("/api/is/CfOrgPresent/{org}")
    public Map<String,Boolean> isCfOrgPresent(@PathVariable String org) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        Map<String,Boolean> ret = new HashMap<>();
        ret.put("isCfOrgPresent", new RestTemplate().getForObject(localhost+"/api/get/orgs/", Map.class).containsKey(org));
	return ret;

    }
    @GetMapping("/api/is/CfSpacePresent/{org}/{space}")
    public String isCfSpacePresent(@PathVariable String org, @PathVariable String space) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
//        Map<String, String> orgs = getAllOrgs();
//        Map<String, String> spaces = getAllSpaces(orgs.get(org));
//	return "{'isCfSpacePresent':"+spaces.containsKey(space)+"}";
        return "";
    }
    @GetMapping("/api/is/CFPermisionPresent/{org}/{space}/{user}")
    public String isCFPermisionPresent(@PathVariable String org, @PathVariable String space, @PathVariable String user) {
	//TODO
        return "{'isCFUserSpacePermisionPresent':";
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
        }catch(NamingException nex){
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
	}
        return ret;
    }
}
