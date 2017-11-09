package com.fiserv.CFCreateUserOrgSpacePermissions.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
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
    String LDAP_USER = System.getenv("LDAP_USER");
    String LDAP_PASS = System.getenv("LDAP_PASS");
    String LDAP_SEARCH_BASE = System.getenv("LDAP_SEARCH_BASE");
    
    //Methods for tesing connections and permisions to LDAP and CF
    @GetMapping("/api/is/LDAPConnectionPresent")
    public String isLDAPConnectionPresent() {
	boolean ret = false;
	try (Socket s = new Socket(LDAP_SERVER_ADDRESS, Integer.parseInt(LDAP_SERVER_PORT))) {
	    ret = true;
	} catch (IOException ex) {
	    /* ret remains false */
	}
	return "{\"isLDAPConnectionPresent\":"+ret+"}";
    }
    @GetMapping("/api/is/LDAPReadPermissionPresent")
    public String isLDAPReadPermissionPresent() {
        boolean ret = false;
        if(null != getLdapContext()) {
            ret = true;
        }
	return "{\"isLDAPReadPermissionPresent\":"+ret+"}";
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
    
    //private sector
    private LdapContext getLdapContext() {
        LdapContext ctx = null;
        try{
            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY,
                    "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.SECURITY_AUTHENTICATION, "Simple");
            env.put(Context.SECURITY_PRINCIPAL, LDAP_USER);
            env.put(Context.SECURITY_CREDENTIALS, LDAP_PASS);
            env.put(Context.PROVIDER_URL, "ldap://"+LDAP_SERVER_ADDRESS+":"+LDAP_SERVER_PORT);
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
            NamingEnumeration answer = ctx.search(LDAP_SEARCH_BASE, "sAMAccountName="
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
}
