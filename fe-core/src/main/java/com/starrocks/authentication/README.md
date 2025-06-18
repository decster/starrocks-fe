# StarRocks FE Authentication Package

The `com.starrocks.authentication` package is responsible for verifying the identity of users connecting to the StarRocks Frontend (FE). It provides a framework for different authentication methods and manages user credentials and properties related to authentication.

Key responsibilities of this package include:

- **Authentication Management**: The `AuthenticationMgr` is the central class that handles authentication requests, choosing the appropriate provider based on user information or system configuration.
- **Pluggable Authentication**: It supports various authentication mechanisms through the `AuthenticationProvider` interface. Implementations can include:
  - Standard password-based authentication (`PlainPasswordAuthenticationProvider`).
  - LDAP integration (`LDAPAuthProvider`).
  - OAuth2 based authentication (`OAuth2AuthenticationProvider`).
  - JWT (JSON Web Token) authentication (`JWTAuthenticationProvider`).
- **User Information**: Manages user-specific authentication details, potentially including password hashes, authentication plugin preferences, and security integration details. `UserAuthenticationInfo` stores this information.
- **Security Integrations**: Handles more complex authentication setups like OAuth2 and JWT through `SecurityIntegration` classes.
- **Group Providers**: Includes a system for mapping users to groups from external sources like LDAP or local files, which can be used in conjunction with authorization.

Key classes in this package include:
- `AuthenticationMgr`: Manages the overall authentication process and providers.
- `AuthenticationProvider`: Interface for different authentication methods.
- `PlainPasswordAuthenticationProvider`: Handles standard username/password authentication.
- `LDAPAuthProvider`: Provides authentication against an LDAP server.
- `OAuth2AuthenticationProvider`: Implements OAuth2 authentication flow.
- `JWTAuthenticationProvider`: Implements JWT-based authentication.
- `UserAuthenticationInfo`: Stores authentication-related information for a user.
- `UserProperty`: Represents various user-specific properties, some of which relate to authentication.
- `SecurityIntegration`: Base for integrating with external security services like OAuth2.
- `GroupProvider`: Interface for providing group information for users.
