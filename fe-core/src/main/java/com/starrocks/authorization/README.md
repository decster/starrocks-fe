# StarRocks FE Authorization Package

The `com.starrocks.authorization` package is responsible for managing access control and permissions within the StarRocks Frontend (FE). It determines what actions a user can perform on various objects (like databases, tables, views, etc.) after they have been authenticated.

Key responsibilities of this package include:

- **Authorization Management**: The `AuthorizationMgr` is the core component that handles privilege granting, revoking, and checking.
- **Access Control Models**: It supports different access control mechanisms:
  - **Native Access Control**: `NativeAccessController` manages privileges stored directly within StarRocks.
  - **External Access Control**: `ExternalAccessController` can delegate authorization decisions to external systems. A key example is integration with Apache Ranger via `RangerAccessController` located in the `com.starrocks.authorization.ranger` sub-package.
- **Privilege System**: Defines a comprehensive set of `PrivilegeType`s (e.g., SELECT, INSERT, CREATE_TABLE) that can be granted on various `ObjectType`s (e.g., TABLE, DATABASE, USER, RESOURCE).
- **Roles and Users**: Manages privileges for both individual users (`UserPrivilegeCollectionV2`) and roles (`RolePrivilegeCollectionV2`). Users can inherit privileges from their assigned roles.
- **Object-Specific Privileges**: Allows fine-grained control by associating privileges with specific database objects (tables, views, materialized views, etc.) through `PEntryObject` (Privilege Entry Object) implementations.
- **Security Policies**: Includes mechanisms for more advanced security features like security policy rewrite rules.

Key classes in this package include:
- `AuthorizationMgr`: Central manager for authorization operations.
- `AccessController`: Interface for different access control implementations.
  - `NativeAccessController`: Manages StarRocks' native privileges.
  - `ExternalAccessController`: Base for integrating external authorization.
  - `RangerAccessController` (in `ranger` sub-package): Integrates with Apache Ranger.
- `PrivilegeCollectionV2`: Stores and manages privileges for an identity (user or role).
- `RolePrivilegeCollectionV2`: Specific privilege collection for roles.
- `UserPrivilegeCollectionV2`: Specific privilege collection for users.
- `PrivilegeType`: Enum defining various types of actions that can be permitted or denied.
- `ObjectType`: Enum defining the types of objects on which privileges can be granted.
- `PEntryObject`: Represents an object to which privileges are attached (e.g., `TablePEntryObject`, `DbPEntryObject`).
