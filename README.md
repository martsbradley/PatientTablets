For fun application.

Provides an API to manage patients & Prescriptions.

No real world data to be ever stored on this application.

#TODO

Grand Plan....

Remove Auth0, Split out the authentication code.

First get the principle coming in the token and when queried in authenticated methods.

Remove from Auth0, replace with Spring Security.


AuthenticationEar                       AuthenticationShared jar
AuthenticationEndPoint
AuthGroup                               Auth0Constants
AuthUser                                JsonWebTokenAuthFilter
AuthUserGroup                           JsonWebTokenVerifier
AuthUserGroupRepo                       KeyLoader (Only the public key needed)
CookieHandler                           SecuredResfulMethod
JsonWebToken                            SecuredResfulMethodHelper
AuthenticationBrokerImpl                
AuthenticationBroker
PasswordHelper
                                       
                                       
Explain...
I want this authentication solution to be reusable and not stuck in side
my patient website.

Have an ear that handles the authentication of users.  Named AuthenticationEar.

The Ear will have context path 'authentication' so will be hit at https://gorticrum.com/authentication/

The filter will be part of a jar that is reused by other web applications.
Both the AuthenticationEar and the AuthenticationJar will have access to the public/private keys
to allow them to create JWT tokens and also verify them.


