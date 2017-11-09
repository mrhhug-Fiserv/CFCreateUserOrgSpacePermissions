package com.fiserv.CFCreateUserOrgSpacePermissions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CfCreateUserOrgSpacePermissionsApplication {

	public static void main(String[] args) {
            System.out.println(System.getenv("DBE"));
            SpringApplication.run(CfCreateUserOrgSpacePermissionsApplication.class, args);
	}
}
