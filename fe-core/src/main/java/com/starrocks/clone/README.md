# StarRocks FE Clone and Scheduling Package

The `com.starrocks.clone` package in the StarRocks Frontend (FE) is primarily responsible for managing the placement, replication, and balancing of data tablets across the cluster. It also handles scheduling of various background tasks related to data management and table maintenance.

Key responsibilities of this package include:

- **Tablet Scheduling and Repair**:
  - `TabletScheduler`: The core class responsible for checking tablet health, managing replica creation (cloning), and repairing tablets that are missing replicas or are in an unhealthy state. It ensures data redundancy and availability.
  - `TabletChecker`: Periodically scans tablets to identify issues that require scheduling actions (e.g., replica missing, version mismatch).
  - `TabletSchedCtx`: Context object for tablet scheduling decisions, holding information about tablets and backends.
- **Load Balancing**:
  - `ColocateTableBalancer`: Manages the balancing of colocate table groups, ensuring that tablets belonging to the same group are distributed appropriately across backends.
  - `DiskAndTabletLoadReBalancer`: A more general rebalancer that considers disk usage and tablet distribution across backends to achieve better cluster balance.
  - `BackendLoadStatistic`, `ClusterLoadStatistic`, `RootPathLoadStatistic`: Classes used to gather and represent load information from backends and the cluster, which informs balancing decisions.
- **Dynamic Partition Scheduling**:
  - `DynamicPartitionScheduler`: Manages the creation and dropping of partitions for tables that use dynamic partitioning, based on specified rules and time windows.
- **TTL (Time-To-Live) Scheduling for Partitions**:
  - `PartitionTTLScheduler`: Handles the automatic dropping of partitions based on their Time-To-Live (TTL) properties.
- **Task Scheduling Framework**: Provides a basis for scheduling various background maintenance and management tasks within the FE.

Key classes in this package include:
- `TabletScheduler`: Manages tablet health, repair, and replica creation.
- `TabletChecker`: Identifies tablets needing scheduling actions.
- `ColocateTableBalancer`: Balances colocate table groups.
- `DiskAndTabletLoadReBalancer`: General cluster rebalancer based on disk and tablet load.
- `BackendLoadStatistic`, `ClusterLoadStatistic`: Collect and store load statistics.
- `DynamicPartitionScheduler`: Manages dynamic partition creation/deletion.
- `PartitionTTLScheduler`: Manages partition TTL.
- `Rebalancer`: (Often an interface or abstract class) Base for rebalancing strategies.

This package plays a crucial role in maintaining data availability, durability, and efficient resource utilization across the StarRocks cluster.
