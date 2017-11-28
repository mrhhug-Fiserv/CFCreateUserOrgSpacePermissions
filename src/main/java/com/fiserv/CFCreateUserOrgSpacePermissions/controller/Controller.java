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
        
            String ret = "{";
            String myOrg = user+"-org";
            String mySpace = "sandbox";
//            ret += putUser(user);
//            ret += putOrg(myOrg);
//            ret += putSpace(myOrg, mySpace);
//            ret += putOrgmanager(myOrg, user);
//            ret += putSpacedev(myOrg, mySpace, user);
            return ret + "}";
    }
//    @DeleteMapping("api/deleteorgUserSpacePermisions/{org}/{user}/{space}")
//    public String deleteorgUserSpacePermisions(@PathVariable String org, @PathVariable String user, @PathVariable String space) {
//	return "TODO";
//    }
   
    
}
