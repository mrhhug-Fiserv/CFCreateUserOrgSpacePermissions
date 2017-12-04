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
    

    @GetMapping("/api/connectionPermisionCheck")
    public Map<String, Boolean> connectionPermisionCheck() {
        Map<String, Boolean> ret = new HashMap<>();
        Map<String, Boolean> LDAPConnectionPresent = new RestTemplate().getForObject(
                "http://localhost:"+System.getenv("PORT")+"/api/is/LDAPConnectionPresent", Map.class);
        Map<String, Boolean> CFConnectionPresent = new RestTemplate().getForObject(
                "http://localhost:"+System.getenv("PORT")+"/api/is/CFConnectionPresent", Map.class);
        Map<String, Boolean> CFReadWritePermissionsPresent = new RestTemplate().getForObject(
                "http://localhost:"+System.getenv("PORT")+"/api/is/CFReadWritePermissionsPresent", Map.class);
        ret.putAll(LDAPConnectionPresent);
        ret.putAll(CFConnectionPresent);
        ret.putAll(CFReadWritePermissionsPresent);
	return ret;
    }
    @GetMapping("/api/userSpacePermisionCheck/{org}/{space}/{user}")
    public Map<String, Boolean> userSpacePermisionCheck(@PathVariable String org, @PathVariable String space, @PathVariable String user) {
        Map<String, Boolean> ret = new HashMap<>();
	Map<String, Boolean> LDAPUserPresent = new RestTemplate().getForObject(
                "http://localhost:"+System.getenv("PORT")+"/api/is/LDAPUserPresent/"+user, Map.class);
        Map<String, Boolean> CFUserPresent = new RestTemplate().getForObject(
                "http://localhost:"+System.getenv("PORT")+"/api/is/CFUserPresent/"+user, Map.class);
        Map<String, Boolean> CfOrgPresent = new RestTemplate().getForObject(
                "http://localhost:"+System.getenv("PORT")+"/api/is/CfOrgPresent/"+org, Map.class);
        Map<String, Boolean> CfSpacePresent = new RestTemplate().getForObject(
                "http://localhost:"+System.getenv("PORT")+"/api/is/CfSpacePresent/"+org+"/"+space, Map.class);
        Map<String, Boolean> isCFPermisionPresent = new RestTemplate().getForObject(
                "http://localhost:"+System.getenv("PORT")+"/api/is/CFPermisionPresent/"+org+"/"+space+"/"+user, Map.class);
        ret.putAll(LDAPUserPresent);
        ret.putAll(CFUserPresent);
        ret.putAll(CfOrgPresent);
        ret.putAll(CfSpacePresent);
        ret.putAll(isCFPermisionPresent);
	return ret;
        
    }
    
    @PutMapping("/api/createOrgUserSpacePermisions/{user}")
    public Map<String, String> createOrgUserSpacePermisions(@PathVariable String user) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        Map<String, String> ret = new HashMap<>();
        String myOrg = user+"-org";
        String mySpace = "sandbox";
        
        Map<String, Object> putUser = new RestTemplate().getForObject(
                "http://localhost:"+System.getenv("PORT")+"/api/put/user/"+user, Map.class);
        if(!putUser.containsKey("id") || !putUser.containsKey("user_id")) {
            ret.put("putUser", "FAIL");
        } else {
                ret.put("putUser", "OK");
        }
        
        Map<String, Object> putOrg = new RestTemplate().getForObject(
                "http://localhost:"+System.getenv("PORT")+"/api/put/org/"+myOrg, Map.class);
        if(!putOrg.containsKey("entity")) {
           String error_code = (String) putOrg.get("error_code");
           if(!error_code.equals("CF-OrganizationNameTaken")) {
               ret.put("putOrg", "FAIL");
            } else {
                ret.put("putOrg", "OK");
            }
            
        }
        
        Map<String, Object> putSpace = new RestTemplate().getForObject(
                "http://localhost:"+System.getenv("PORT")+"/api/put/space/"+myOrg+"/"+mySpace, Map.class);
        if(!putSpace.containsKey("entity")) {
           String error_code = (String) putSpace.get("error_code");
           if(!error_code.equals("CF-SpaceNameTaken"))
            ret.put("putSpace", "FAIL");
        } else {
            ret.put("putSpace", "OK");
        }
        
        Map<String, Object> putOrgUser = new RestTemplate().getForObject(
                "http://localhost:"+System.getenv("PORT")+"/api/put/orguser/"+myOrg+"/"+user, Map.class);
        if(!putOrgUser.containsKey("entity")) {
            ret.put("putOrgUser", "FAIL");
        } else {
            ret.put("putOrgUser", "OK");
        }
        
        Map<String, Object> putOrgManager = new RestTemplate().getForObject(
                "http://localhost:"+System.getenv("PORT")+"/api/put/orgmanager/"+myOrg+"/"+user, Map.class);
        if(!putOrgUser.containsKey("entity")) {
            ret.put("putOrgManager", "FAIL");
        } else {
            ret.put("putOrgManager", "OK");
        }
        
        Map<String, Object> putSpaceDev = new RestTemplate().getForObject(
                "http://localhost:"+System.getenv("PORT")+"/api/put/spacedev/"+myOrg+"/"+"/"+mySpace+"/"+user, Map.class);
        if(!putSpaceDev.containsKey("entity")) {
            ret.put("putSpacedev", "FAIL");
        } else {
            ret.put("putSpacedev", "OK");
        }
        
        return ret;
    }
//    @DeleteMapping("api/deleteorgUserSpacePermisions/{org}/{user}/{space}")
//    public String deleteorgUserSpacePermisions(@PathVariable String org, @PathVariable String user, @PathVariable String space) {
//	return "TODO";
//    }
   
    
}
