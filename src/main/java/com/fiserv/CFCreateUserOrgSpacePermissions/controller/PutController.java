package com.fiserv.CFCreateUserOrgSpacePermissions.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Michael Hug
 */
@RestController("/api/put")
public class PutController {
    
    @PutMapping("/api/put/user/{user}")
    public String putUser(@PathVariable String user) {
	return "";
    }
    @PutMapping("/api/put/org/{org}")
    public String putOrg(@PathVariable String org) {
	return "";
    }
    @PutMapping("/api/put/space/{org}/{space}")
    public String putSpace(@PathVariable String org, @PathVariable String space) {
	return "";
    }
    @PutMapping("/api/put/permision/{org}/{space}/{user}")
    public String putPermission(@PathVariable String org, @PathVariable String space, @PathVariable String user) {
	return "";
    }
}
