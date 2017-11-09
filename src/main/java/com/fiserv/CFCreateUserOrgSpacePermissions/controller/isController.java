package com.fiserv.CFCreateUserOrgSpacePermissions.controller;

import java.io.IOException;
import java.net.Socket;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Michael Hug
 */
@RestController("/api/is")
public class isController {
    String LDAP_SERVER_ADDRESS = System.getenv("LDAP_SERVER_ADDRESS");
    String LDAP_SERVER_PORT = System.getenv("LDAP_SERVER_PORT");
    
    //Methods for tesing connections and permisions to LDAP and CF
    @GetMapping("/api/is/LDAPConnectionPresent")
    public String isLDAPConnectionPresent() {
	boolean ret = false;
	try (Socket s = new Socket(LDAP_SERVER_ADDRESS, Integer.parseInt(LDAP_SERVER_PORT))) {
	    ret = true;
	} catch (IOException ex) {
	    /* ignore */
	}
	return "{'isLDAPConnectionPresent':"+ret+"}";
    }
    @GetMapping("/api/is/LDAPReadPermissionPresent")
    public String isLDAPReadPermissionPresent() {
	return "{'isLDAPReadPermissionPresent':";
    }
    @GetMapping("/api/is/LDAPUserPresent/{user}")
    public String isLDAPUserPresent(@PathVariable String user) {
	return "{'isLDAPUserPresent':";
    }
    @GetMapping("/api/is/CFConnectionPresent")
    public String isCFConnectionPresent() {
	return "{'isCFConnectionPresent':";
    }
    @GetMapping("/api/is/CFReadWritePermissionsPresent")
    public String isCFReadWritePermissionsPresent() {
	return "{'isCFReadWritePermissionsPresent':";
    }
    
    //Methods for validating CF end state
    @GetMapping("/api/is/CFUserPresent/{user}")
    public String isCFUserPresent(@PathVariable String user) {
	return "{'isCFUserPresent':";
    }
    @GetMapping("/api/is/CfOrgPresent/{org}")
    public String isCfOrgPresent(@PathVariable String org) {
	return "{'isCfOrgPresent':";
    }
    @GetMapping("/api/is/CfSpacePresent/{org}/{space}")
    public String isCfSpacePresent(@PathVariable String org, @PathVariable String space) {
	return "{'isCfSpacePresent':";
    }
    @GetMapping("/api/is/CFPermisionPresent/{org}/{space}/{user}")
    public String isCFPermisionPresent(@PathVariable String org, @PathVariable String space, @PathVariable String user) {
	return "{'isCFUserSpacePermisionPresent':";
    }
}
