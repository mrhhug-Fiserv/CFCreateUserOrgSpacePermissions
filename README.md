# CFCreateUserOrgSpacePermissions
Create User, Org, Space, and permissions in cloudfoundry for a self service portal

Initial configuration is setup so that users have an ldap origin.
LDAP short names are used in several places.

The following Environment variables must be modified to fit your org : 


LDAP_SERVER_ADDRESS=mycorp.example.net

LDAP_SERVER_PORT=389<br>
LDAP_USER=ldapreadonlyserviceaccount@mycorp.example.net<br>
LDAP_PASS=ldapreadonlyserviceaccountpassword

LDAP_SEARCH_BASE=OU=peepz,DC=mycorp,DC=example,DC=net

CF_SERVER_ADDRESS=pcfinstall.example.net

CF_SERVER_PORT=443

CF_USER=admin

CF_PASS=supersecretpassthatiwouldnevershare

BU={"Internal Sales": "IntSales-org", "POSIX Engineering": "Peng-org", "Vim Users": "wq-org"}
