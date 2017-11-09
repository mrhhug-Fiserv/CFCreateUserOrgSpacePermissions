package com.fiserv.CFCreateUserOrgSpacePermissions.controller;

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
	return "";
    }
    @GetMapping("/api/userSpacePermisionCheck/{org}/{space}/{user}")
    public String userSpacePermisionCheck(@PathVariable String org, @PathVariable String space, @PathVariable String user) {
	return "{'isCFUserSpacePermisionPresent':";
    }
    @PutMapping("/api/createOrgUserSpacePermisions/{org}/{space}/{user}")
    public String createOrgUserSpacePermisions(@PathVariable String org, @PathVariable String space, @PathVariable String user) {
	return "";
    }
    @DeleteMapping("api/deleteorgUserSpacePermisions/{org}/{user}/{space}")
    public String deleteorgUserSpacePermisions(@PathVariable String org, @PathVariable String user, @PathVariable String space) {
	return "";
    }
}
