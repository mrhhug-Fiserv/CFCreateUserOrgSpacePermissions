package com.fiserv.CFCreateUserOrgSpacePermissions.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
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

/**
 * @author Michael Hug
 */
@RestController("/api/is")
public class isController {
    
    //Methods for tesing connections and permisions to LDAP and CF
    @GetMapping("/api/is/LDAPConnectionPresent")
    public String isLDAPConnectionPresent() {
	boolean ret = false;
        String address = System.getenv("LDAP_SERVER_ADDRESS");
        int port = Integer.parseInt(System.getenv("LDAP_SERVER_PORT"));
	try (Socket s = new Socket(address, port)) {
	    ret = true;
	} catch (IOException ex) {
	    /* ret remains false */
	}
	return "{\"isLDAPConnectionPresent\":"+ret+"}";
    }

    @GetMapping("/api/is/LDAPUserPresent/{user}")
    public String isLDAPUserPresent(@PathVariable String user) {
        String ret = "false";
        Map value = getUserBasicAttributes(user,getLdapContext());
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String possibleReturn = gson.toJson(value);
        if(!possibleReturn.equals("{}")){
            ret = possibleReturn;
        }
	return "{\"isLDAPUserPresent\":{\""+user+"\":"+ret+"}}";
    }
    @GetMapping("/api/is/CFConnectionPresent")
    public String isCFConnectionPresent() {
        boolean ret = false;
        String address = System.getenv("CF_SERVER_ADDRESS");
        int port = Integer.parseInt(System.getenv("CF_SERVER_PORT"));
	try (Socket s = new Socket("api."+address, port)) {
	    ret = true;
	} catch (IOException ex) {
	    /* ret remains false */
	}
	return "{\"isCFConnectionPresent\":"+ret+"}";
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
	return "{'isCFUserPresent':";
    }
    @GetMapping("/api/is/CfOrgPresent/{org}")
    public String isCfOrgPresent(@PathVariable String org) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        Map<String, String> orgs = getAllOrgs();
	return "{'isCfOrgPresent':"+orgs.containsKey(org)+"}";
    }
    @GetMapping("/api/is/CfSpacePresent/{org}/{space}")
    public String isCfSpacePresent(@PathVariable String org, @PathVariable String space) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        Map<String, String> orgs = getAllOrgs();
        Map<String, String> spaces = getAllSpaces(orgs.get(org));
	return "{'isCfSpacePresent':"+spaces.containsKey(space)+"}";
    }
    @GetMapping("/api/is/CFPermisionPresent/{org}/{space}/{user}")
    public String isCFPermisionPresent(@PathVariable String org, @PathVariable String space, @PathVariable String user) {
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
    
    private Map getUserBasicAttributes(String username, LdapContext ctx) {
        Map<String, String> ret = new HashMap();
        try {
            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String[] attrIDs = { "distinguishedName",
                "sn",
                "givenname",
                "mail",
                "telephonenumber"
            };
            constraints.setReturningAttributes(attrIDs);
            NamingEnumeration answer = ctx.search(System.getenv("LDAP_SEARCH_BASE"), "sAMAccountName="
                + username, constraints);
            if (answer.hasMore()) {
                Attributes attrs = ((SearchResult) answer.next()).getAttributes();
                for ( String i : attrIDs) {
                    String LDAPresponse = attrs.get(i).toString();
                    String key = LDAPresponse.substring(0, LDAPresponse.indexOf(":"));
                    String value = LDAPresponse.substring(LDAPresponse.indexOf(":")+2);
                    ret.put(key,value);
                }
            }
        } catch (Exception ex) {
            /* sends back an empty map */
        }
        return ret;
    }
    
    
    //you don't need to get this every time, but it does go stale
    static String getCFBearerToken() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
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
    
    static Map<String, String> getAllOrgs() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        Map<String, String> orgs = new HashMap<>();
        SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                builder.build());
        CloseableHttpClient client = HttpClients.custom().setSSLSocketFactory(
            sslsf).build();
        
        String bearerToken = getCFBearerToken();
        
        int page =1;
        while(page > 0) {
            HttpGet httpGet = new HttpGet("https://api."+System.getenv("CF_SERVER_ADDRESS")+"/v2/organizations?order-by=name&order-direction=asc&page="+(page++)+"&results-per-page=100");
            httpGet.addHeader("Accept", "application/json");
            httpGet.addHeader("Authorization", "Bearer "+bearerToken);
            Map responseGson = null;
            try {
                CloseableHttpResponse response = client.execute(httpGet);
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity, "UTF-8");
                responseGson = new Gson().fromJson(responseString, Map.class);
                client.close();
            } catch (IOException ex) {
            
            }
            if(null == responseGson.get("next_url")) {
                    page = 0;
            }
            for ( Object i : (ArrayList) responseGson.get("resources")) {
                LinkedTreeMap theMap = (LinkedTreeMap) i;
                LinkedTreeMap nested = (LinkedTreeMap) theMap.get("entity");
                String name = (String) nested.get("name");
                nested = (LinkedTreeMap) theMap.get("metadata");
                String guid = (String) nested.get("guid");
                orgs.put(name, guid);
            }
        }
        return orgs;
    }
    static Map<String, String> getAllSpaces(String orgGuid) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        Map<String, String> spaces = new HashMap<>();
        SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                builder.build());
        CloseableHttpClient client = HttpClients.custom().setSSLSocketFactory(
            sslsf).build();
        
        String bearerToken = getCFBearerToken();

        HttpGet httpGet = new HttpGet("https://api."+System.getenv("CF_SERVER_ADDRESS")+"/v2/spaces?order-by=name&q=organization_guid%3A"+orgGuid);
        httpGet.addHeader("Accept", "application/json");
        httpGet.addHeader("Authorization", "Bearer "+bearerToken);
        Map responseGson = null;
        try {
            CloseableHttpResponse response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            responseGson = new Gson().fromJson(responseString, Map.class);
            client.close();
        } catch (IOException ex) {

        }
        for ( Object i : (ArrayList) responseGson.get("resources")) {
            LinkedTreeMap theMap = (LinkedTreeMap) i;
            LinkedTreeMap nested = (LinkedTreeMap) theMap.get("entity");
            String name = (String) nested.get("name");
            nested = (LinkedTreeMap) theMap.get("metadata");
            String guid = (String) nested.get("guid");
            spaces.put(name, guid);
        }
        return spaces;
    }
}
