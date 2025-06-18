# StarRocks FE Backup and Restore Package

The `com.starrocks.backup` package is responsible for managing the backup and restore operations for StarRocks data and metadata within the Frontend (FE). It allows users to create snapshots of their data and later restore them, either to the same cluster or a different one.

Key responsibilities of this package include:

- **Backup and Restore Management**:
  - `BackupHandler`: Coordinates the overall backup and restore processes.
  - `BackupJob`: Represents a single backup job, tracking its state, progress, and metadata. It handles the process of creating snapshots of tables and partitions.
  - `RestoreJob`: Represents a single restore job, managing the restoration of data from a previously created backup. This includes table creation, data loading, and metadata updates.
- **Repository Management**:
  - `RepositoryMgr`: Manages the list of configured backup repositories.
  - `Repository`: Represents a storage location (e.g., HDFS, S3) where backups are stored. It provides an abstraction layer for interacting with different remote storage systems via `BlobStorage`.
- **Snapshotting**: Handles the creation of consistent snapshots of table metadata and, in coordination with the Backend (BE), the data itself.
- **Metadata Handling**: Backs up and restores table schemas, partition information, and other relevant metadata.
- **Extensibility**: Includes support for backing up and restoring more complex structures like Materialized Views (handled in the `mv` sub-package).
- **Job Lifecycle and Status**: Manages the various states of backup and restore jobs (e.g., PENDING, SNAPSHOTING, UPLOADING, FINISHED, CANCELLED).

Key classes in this package include:
- `BackupHandler`: Main handler for initiating and managing backup/restore operations.
- `BackupJob`: Defines the logic and state for a backup operation.
- `RestoreJob`: Defines the logic and state for a restore operation.
- `RepositoryMgr`: Manages backup repositories.
- `Repository`: Represents a remote storage location for backups.
- `BlobStorage`: Interface for interacting with various blob storage services.
- `Status`: Represents the status of backup/restore operations.
- `AbstractJob`: Base class for `BackupJob` and `RestoreJob`.
- `mv.MvBackupInfo` & `mv.MVRestoreUpdater` (in `mv` sub-package): Handle specific logic for materialized view backup and restore.
