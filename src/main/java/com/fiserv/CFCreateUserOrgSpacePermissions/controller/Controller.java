package com.fiserv.CFCreateUserOrgSpacePermissions.controller;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Michael Hug
 */
@RestController("/api/")
public class Controller {
    

    @GetMapping("/api/connectionPermisionCheck")
    public String connectionPermisionCheck() {
	return "TODO";
    }
    @GetMapping("/api/userSpacePermisionCheck/{org}/{space}/{user}")
    public String userSpacePermisionCheck(@PathVariable String org, @PathVariable String space, @PathVariable String user) {
	return "TODO";
    }
    @PutMapping("/api/createOrgUserSpacePermisions/{user}")
    public String createOrgUserSpacePermisions(@PathVariable String user) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
//        if (!isController.isLDAPUserPresent(user).contains("false")) {
//                    String ret = "{";
//            String myOrg = user+"-org";
//            String mySpace = "sandbox";
//            ret += putUser(user);
//            ret += putOrg(myOrg);
//            ret += putSpace(myOrg, mySpace);
//            ret += putOrgmanager(myOrg, user);
//            ret += putSpacedev(myOrg, mySpace, user);
//            return ret + "}";
//        }
//        return "{\"ERROR\":\"LDAP user not found\"}";
return "";
        
    }
    @DeleteMapping("api/deleteorgUserSpacePermisions/{org}/{user}/{space}")
    public String deleteorgUserSpacePermisions(@PathVariable String org, @PathVariable String user, @PathVariable String space) {
	return "TODO";
    }
    
    //put
    @PutMapping("/api/put/user/{user}")
    public String putUser(@PathVariable String user) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
//        String responseString = "Internal Failure";
//        SSLContextBuilder builder = new SSLContextBuilder();
//        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
//        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
//                builder.build());
//        CloseableHttpClient client = HttpClients.custom().setSSLSocketFactory(
//            sslsf).build();
//        
//        String bearerToken = getCFBearerToken();
//
//        HttpPost httpPost = new HttpPost("https://uaa."+System.getenv("CF_SERVER_ADDRESS")+"/Users");
//        httpPost.addHeader("Accept", "application/json");
//        httpPost.addHeader("Authorization", "Bearer "+bearerToken);
//        httpPost.addHeader("Content-Type", "application/json");
//        try {
//            String payload = "{\"emails\":[{\"primary\": true,\"value\": \""+user+"\"}],\"name\": {\"familyName\": \""+user+"\",\"givenName\": \""+user+"\"},\"origin\": \"ldap\",\"password\": \"[PRIVATE DATA HIDDEN]\",\"userName\": \""+user+"\"}";
//            httpPost.setEntity(new ByteArrayEntity(payload.getBytes("UTF-8")));
//            CloseableHttpResponse response = client.execute(httpPost);
//            HttpEntity entity = response.getEntity();
//            responseString = EntityUtils.toString(entity, "UTF-8");
//            client.close();
//        } catch (IOException ex) {
//
//        }
//	return responseString;
return "";
    }
    @PutMapping("/api/put/org/{org}")
    public String putOrg(@PathVariable String org) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
//        String responseString = "Internal Failure";
//        SSLContextBuilder builder = new SSLContextBuilder();
//        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
//        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
//                builder.build());
//        CloseableHttpClient client = HttpClients.custom().setSSLSocketFactory(
//            sslsf).build();
//        
//        String bearerToken = getCFBearerToken();
//
//        HttpPost httpPost = new HttpPost("https://api."+System.getenv("CF_SERVER_ADDRESS")+"/v2/organizations");
//        httpPost.addHeader("Accept", "application/json");
//        httpPost.addHeader("Authorization", "Bearer "+bearerToken);
//        
//        try {
//            String payload = "{\"name\":\""+org+"\"}";
//            httpPost.setEntity(new ByteArrayEntity(payload.getBytes("UTF-8")));
//            CloseableHttpResponse response = client.execute(httpPost);
//            HttpEntity entity = response.getEntity();
//            responseString = EntityUtils.toString(entity, "UTF-8");
//            client.close();
//        } catch (IOException ex) {
//
//        }
//	return responseString;
return "";
    }
    @PutMapping("/api/put/space/{org}/{space}")
    public String putSpace(@PathVariable String org, @PathVariable String space) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
//        String responseString = "Internal Failure";
//        Map<String, String> orgs = isController.getAllOrgs();
//	SSLContextBuilder builder = new SSLContextBuilder();
//        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
//        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
//                builder.build());
//        CloseableHttpClient client = HttpClients.custom().setSSLSocketFactory(
//            sslsf).build();
//        
//        String bearerToken = getCFBearerToken();
//
//        HttpPost httpPost = new HttpPost("https://api."+System.getenv("CF_SERVER_ADDRESS")+"/v2/spaces?async=true&inline-relations-depth=1");
//        httpPost.addHeader("Accept", "application/json");
//        httpPost.addHeader("Authorization", "Bearer "+bearerToken);
//        
//        try {
//            String payload = "{\"name\":\""+space+"\",\"organization_guid\":\""+orgs.get(org)+"\"}";
//            httpPost.setEntity(new ByteArrayEntity(payload.getBytes("UTF-8")));
//            CloseableHttpResponse response = client.execute(httpPost);
//            HttpEntity entity = response.getEntity();
//            responseString = EntityUtils.toString(entity, "UTF-8");
//            client.close();
//        } catch (IOException ex) {
//
//        }
//        
//	return responseString;
return "";
    }
    @PutMapping("/api/put/spacedev/{org}/{space}/{user}")
    public String putSpacedev(@PathVariable String org, @PathVariable String space, @PathVariable String user) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
//	String responseString = "Internal Failure";
//        Map<String, String> orgs = isController.getAllOrgs();
//        Map<String, String> spaces = isController.getAllSpaces(orgs.get(org));
//	SSLContextBuilder builder = new SSLContextBuilder();
//        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
//        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
//                builder.build());
//        CloseableHttpClient client = HttpClients.custom().setSSLSocketFactory(
//            sslsf).build();
//        
//        String bearerToken = getCFBearerToken();
//
//        HttpPut httpPut = new HttpPut("https://api."+System.getenv("CF_SERVER_ADDRESS")+"/v2/spaces/"+spaces.get(space)+"/developers");
//        httpPut.addHeader("Accept", "application/json");
//        httpPut.addHeader("Authorization", "Bearer "+bearerToken);
//        httpPut.addHeader("Content-Type", "application/json");
//        
//        try {
//            String payload = "{\"username\": \""+user+"\"}";
//            httpPut.setEntity(new ByteArrayEntity(payload.getBytes("UTF-8")));
//            CloseableHttpResponse response = client.execute(httpPut);
//            HttpEntity entity = response.getEntity();
//            responseString = EntityUtils.toString(entity, "UTF-8");
//            client.close();
//        } catch (IOException ex) {
//
//        }
//        
//	return responseString;
return "";
    }
    @PutMapping("/api/put/orgmanager/{org}/{user}")
    public String putOrgmanager(@PathVariable String org, @PathVariable String user) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
//	String responseString = "Internal Failure";
//        Map<String, String> orgs = isController.getAllOrgs();
//	SSLContextBuilder builder = new SSLContextBuilder();
//        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
//        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
//                builder.build());
//        CloseableHttpClient client = HttpClients.custom().setSSLSocketFactory(
//            sslsf).build();
//        
//        String bearerToken = getCFBearerToken();
//
//        HttpPut httpPut = new HttpPut("https://api."+System.getenv("CF_SERVER_ADDRESS")+"/v2/organizations/"+orgs.get(org)+"/managers");
//        httpPut.addHeader("Accept", "application/json");
//        httpPut.addHeader("Authorization", "Bearer "+bearerToken);
//        httpPut.addHeader("Content-Type", "application/json");
//        
//        try {
//            String payload = "{\"username\": \""+user+"\"}";
//            httpPut.setEntity(new ByteArrayEntity(payload.getBytes("UTF-8")));
//            CloseableHttpResponse response = client.execute(httpPut);
//            HttpEntity entity = response.getEntity();
//            responseString = EntityUtils.toString(entity, "UTF-8");
//            client.close();
//        } catch (IOException ex) {
//
//        }
//        
//	return responseString;
return "";
    }
}
