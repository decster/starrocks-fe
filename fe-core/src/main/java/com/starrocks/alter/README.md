# StarRocks FE Alter Package

The `com.starrocks.alter` package is responsible for managing and executing schema alteration tasks within the StarRocks Frontend (FE). This includes operations like:

- Handling various types of table alterations (e.g., schema changes, rollup creations, table optimization).
- Managing the lifecycle of alteration jobs.
- Coordinating changes with the underlying storage and metadata.
- Specific handlers for different alteration types, such as schema changes (`SchemaChangeHandler`), materialized view modifications (`MaterializedViewHandler`), and system-level alterations (`SystemHandler`).

Key classes in this package include:
- `AlterJobMgr`: Manages the queue and status of alteration jobs.
- `AlterHandler`: Base class for different alteration job handlers.
- `SchemaChangeHandler`: Handles schema modification operations.
- `MaterializedViewHandler`: Manages operations related to materialized views.
- `SystemHandler`: Deals with system-level alteration tasks like cluster scaling.
- `AlterJobV2`: Represents an alteration job.
