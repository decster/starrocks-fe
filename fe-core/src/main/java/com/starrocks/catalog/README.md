# StarRocks FE Catalog Package

The `com.starrocks.catalog` package is a cornerstone of the StarRocks Frontend (FE), responsible for defining and managing all metadata related to the logical structure of data within StarRocks. It acts as the central repository for information about databases, tables, columns, partitions, views, materialized views, and other catalog entities.

Key responsibilities of this package include:

- **Metadata Representation**: Defines Java classes that model all catalog objects, such as:
  - `Database`: Represents a database, containing tables and other related information.
  - `Table`: Base class for different table types. Important derived classes include:
    - `OlapTable`: Represents StarRocks' native columnar tables.
    - `MaterializedView`: Represents materialized views, which store precomputed query results.
    - External table types like `MysqlTable`, `HiveTable`, `IcebergTable`, `HudiTable`, `DeltaLakeTable`, `FileTable`, `EsTable` (Elasticsearch), `JDBCDbtable` for connecting to various external data sources.
  - `Column`: Represents a column within a table, including its name, type, and properties.
  - `Partition`: Represents a partition of a table, used for data distribution and management.
  - `MaterializedIndex`: Defines the schema and properties of base tables and rollup indexes within an OlapTable.
  - `Tablet`: Represents a data shard or replica of a partition.
- **Type System**: Defines StarRocks' data types through the `Type` class hierarchy (e.g., `ScalarType` for primitive types like INT, VARCHAR, DECIMAL; `ArrayType`, `MapType`, `StructType` for complex types).
- **Catalog Management**: While the `GlobalStateMgr` (`com.starrocks.server.GlobalStateMgr`) often orchestrates overall state, the `Catalog` class (or interfaces it uses) provides mechanisms for creating, altering, dropping, and retrieving catalog objects.
- **Schema Management**: Handles table schemas, including column definitions, indexes, and constraints.
- **Data Properties**: Manages properties related to data storage, such as replication number, storage medium, and bucketing for OlapTables.
- **External Catalogs**: Provides support for integrating with external catalog services (e.g., Hive Metastore, AWS Glue) through `ExternalCatalog` and related classes.
- **Recycle Bin**: Manages soft deletion of databases, tables, and partitions via `CatalogRecycleBin`.

Key classes and concepts:
- `Database`: Container for tables.
- `Table`: Generic table representation.
  - `OlapTable`: Core StarRocks table type.
  - `MaterializedView`: For precomputed results.
  - Various external table types.
- `Column`: Defines table columns.
- `Type`: Base for all data types.
  - `ScalarType`: For primitive types.
  - `ArrayType`, `MapType`, `StructType`: For complex data structures.
- `Partition`: Logical data division.
- `DataProperty`: Defines storage characteristics.
- `DistributionInfo`: Defines how data is distributed (e.g., hash-based bucketing).
- `Tablet` & `MaterializedIndex`: Core components of OlapTable data organization.
- `CatalogRecycleBin`: Handles metadata garbage collection.
- `BrokerMgr`, `FsBroker`: For managing access to external file systems via brokers.
- `Function`: Represents user-defined and built-in functions.

This package is fundamental to how StarRocks understands and organizes data, serving as the source of truth for query planning, optimization, and execution.
