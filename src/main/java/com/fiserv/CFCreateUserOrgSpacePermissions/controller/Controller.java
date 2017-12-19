package com.fiserv.CFCreateUserOrgSpacePermissions.controller;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author Michael Hug
 */
@RestController("/api/")
public class Controller {
    

    @GetMapping("/api/connectionPermissionCheck")
    public Map<String, Boolean> connectionPermissionCheck() {
        Map<String, Boolean> ret = new HashMap<>();
        Map<String, Boolean> LDAPConnectionPresent = new RestTemplate().getForObject(
                "http://localhost:"+System.getenv("PORT")+"/api/is/LDAPConnectionPresent", Map.class);
        Map<String, Boolean> CFConnectionPresent = new RestTemplate().getForObject(
                "http://localhost:"+System.getenv("PORT")+"/api/is/CFConnectionPresent", Map.class);
        Map<String, Boolean> CFAdminReadWritePermissionsPresent = new RestTemplate().getForObject(
                "http://localhost:"+System.getenv("PORT")+"/api/is/CFAdminReadWritePermissionsPresent", Map.class);
        ret.putAll(LDAPConnectionPresent);
        ret.putAll(CFConnectionPresent);
        ret.putAll(CFAdminReadWritePermissionsPresent);
	return ret;
    }
    @GetMapping("/api/userSpacePermissionCheck/{user}")
    public Map<String, Boolean> userSpacePermissionCheck(@PathVariable String user) {
        String org = getUsersSandboxOrg(user);
        String space = getUsersSandboxSpace(user);
        Map<String, Boolean> ret = new HashMap<>();
        System.out.println("userSpacePermissionCheck begin");
	Map<String, Boolean> LDAPUserPresent = new RestTemplate().getForObject(
                "http://localhost:"+System.getenv("PORT")+"/api/is/LDAPUserPresent/"+user, Map.class);
        System.out.println("userSpacePermissionCheck got LDAPUserPresent");
        if( LDAPUserPresent.get("isLDAPUserPresent")) {
            Map<String, Boolean> CFUserPresent = new RestTemplate().getForObject(
                "http://localhost:"+System.getenv("PORT")+"/api/is/CFUserPresent/"+user, Map.class);
            ret.putAll(CFUserPresent);
            if (CFUserPresent.get("isCFUserPresent")) {
                Map<String, Boolean> CfOrgPresent = new RestTemplate().getForObject(
                        "http://localhost:"+System.getenv("PORT")+"/api/is/CfOrgPresent/"+org, Map.class);
                ret.putAll(CfOrgPresent);
                Map<String, Boolean> CfSpacePresent = new RestTemplate().getForObject(
                        "http://localhost:"+System.getenv("PORT")+"/api/is/CfSpacePresent/"+org+"/"+space, Map.class);
                ret.putAll(CfSpacePresent);
                Map<String, Boolean> isCFPermissionPresent = new RestTemplate().getForObject(
                    "http://localhost:"+System.getenv("PORT")+"/api/is/CFPermissionPresent/"+org+"/"+space+"/"+user, Map.class);
                ret.putAll(isCFPermissionPresent);
            }               
        }
        System.out.println("userSpacePermissionCheck end calls");
        ret.putAll(LDAPUserPresent);
	return ret;
        
    }
    
    @PutMapping("/api/createUserSpacePermissions/{org}/{user}")
    public Map<String, String> createUserSpacePermissions(@PathVariable String org, @PathVariable String user) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        String myOrg = org;
        String mySpace = getUsersSandboxSpace(user);
        
        new RestTemplate().put("http://localhost:"+System.getenv("PORT")+"/api/put/user/"+user, null);
        if(myOrg.equals("new")) {
            myOrg = getUsersSandboxOrg(user);
            new RestTemplate().put("http://localhost:"+System.getenv("PORT")+"/api/put/org/"+myOrg, null);
            new RestTemplate().put("http://localhost:"+System.getenv("PORT")+"/api/put/orgmanager/"+myOrg+"/"+user, null);
        }
        new RestTemplate().put("http://localhost:"+System.getenv("PORT")+"/api/put/space/"+myOrg+"/"+mySpace, null);
        new RestTemplate().put("http://localhost:"+System.getenv("PORT")+"/api/put/orguser/"+myOrg+"/"+user, null);
        new RestTemplate().put("http://localhost:"+System.getenv("PORT")+"/api/put/spacedev/"+myOrg+"/"+"/"+mySpace+"/"+user, null);
        Map<String, String> ret = new HashMap<>();
        ret.put("username", user);
        ret.put("api_endpoint", "api."+System.getenv("CF_SERVER_ADDRESS"));
        ret.put("org", myOrg);
        ret.put("space", mySpace);
        return ret;
    }
//    @DeleteMapping("api/deleteorgUserSpacePermisions/{org}/{user}/{space}")
//    public String deleteorgUserSpacePermisions(@PathVariable String org, @PathVariable String user, @PathVariable String space) {
//	return "TODO";
    //cf delete-user dhassan; cf delete-org dhassan-org;
//    }
    
    private String getUsersSandboxOrg(String user) {
        return user+"-org";
    }
    private String getUsersSandboxSpace(String user) {
        return "development";
    }
 
}
