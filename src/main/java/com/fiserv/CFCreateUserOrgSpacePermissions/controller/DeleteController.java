package com.fiserv.CFCreateUserOrgSpacePermissions.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Michael Hug
 */
@RestController("/api/delete")
public class DeleteController {
    
    @DeleteMapping("/api/delete/permission/{org}/{space}/{user}")
    public String deletePermission(@PathVariable String org, @PathVariable String space, @PathVariable String user) {
	return "UNIMPLEMENTED";
    }
    @DeleteMapping("/api/delete/user/{user}")
    public String deleteUser(@PathVariable String user) {
	return "UNIMPLEMENTED";
    }
    @DeleteMapping("/api/delete/space/{space}")
    public String deleteSpace(@PathVariable String space) {
	return "UNIMPLEMENTED";
    }
    @DeleteMapping("/api/delete/org/{org}")
    public String deleteOrg(@PathVariable String org) {
	return "UNIMPLEMENTED";
    }
    
    
}
