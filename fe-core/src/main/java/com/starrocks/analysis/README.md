# StarRocks FE Analysis Package

The `com.starrocks.analysis` package is responsible for the syntactic and semantic analysis of SQL statements in the StarRocks Frontend (FE). Its primary role is to take a raw SQL query and transform it into a structured, validated, and understandable representation that can be used by the query planner and optimizer.

Key responsibilities of this package include:

- **Parsing Support**: While the actual parsing from string to an Abstract Syntax Tree (AST) is handled by the `com.starrocks.sql.parser` package, the `analysis` package defines many of the AST node classes (e.g., various `Expr` and `Statement` subclasses).
- **Semantic Analysis**: The `Analyzer` class traverses the AST, performing tasks such as:
  - Resolving table and column names.
  - Type checking and type inference.
  - Validating function calls and expressions.
  - Rewriting expressions and query structures where necessary.
- **AST Node Definitions**: This package defines a rich hierarchy of classes representing different parts of a SQL query, such as:
  - `Expr`: The base class for all expressions (e.g., `SlotRef`, `IntLiteral`, `BinaryPredicate`, `FunctionCallExpr`).
  - `Statement`: Base class for SQL statements (though many specific statement classes are in `com.starrocks.sql.ast`).
  - `TableName`, `LabelName`: Representations for identifiers.
  - `SelectListItem`, `SortInfo`: Structures used within select statements.

Key classes in this package include:
- `Analyzer`: Performs semantic analysis of the AST.
- `Expr`: Base class for all expressions.
- `SlotRef`: Represents a reference to a column.
- `LiteralExpr`: Base class for literal values (e.g., `IntLiteral`, `StringLiteral`).
- Various predicate classes like `BinaryPredicate`, `LikePredicate`, `IsNullPredicate`.
- `FunctionCallExpr`: Represents a function call.
- `SelectStmt`, `QueryStmt` (though often defined in `com.starrocks.sql.ast`, they are heavily used and manipulated here).
