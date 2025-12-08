package com.poc.shared.util;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class to convert Liquibase YAML changelog files to pure SQL.
 *
 * <p>Supports common Liquibase change types:
 * <ul>
 *   <li>createTable, dropTable, renameTable</li>
 *   <li>addColumn, dropColumn, renameColumn, modifyDataType</li>
 *   <li>createIndex, dropIndex</li>
 *   <li>addPrimaryKey, dropPrimaryKey</li>
 *   <li>addForeignKeyConstraint, dropForeignKeyConstraint</li>
 *   <li>addUniqueConstraint, dropUniqueConstraint</li>
 *   <li>insert, update, delete</li>
 *   <li>sql (raw SQL)</li>
 * </ul>
 *
 * <p>Usage:
 * <pre>{@code
 * // Convert a single file
 * String sql = LiquibaseYamlToSqlConverter.convertFile("path/to/changelog.yaml");
 *
 * // Convert and save to file
 * LiquibaseYamlToSqlConverter.convertFileAndSave("input.yaml", "output.sql");
 *
 * // Convert entire directory
 * LiquibaseYamlToSqlConverter.convertDirectory("db/changelog", "db/sql");
 * }</pre>
 *
 * @author my-platform
 */
public class LiquibaseYamlToSqlConverter {

    private static final String DEFAULT_DIALECT = "mysql";
    private final String dialect;

    public LiquibaseYamlToSqlConverter() {
        this(DEFAULT_DIALECT);
    }

    public LiquibaseYamlToSqlConverter(String dialect) {
        this.dialect = dialect.toLowerCase();
    }

    /**
     * Convert a Liquibase YAML changelog file to SQL.
     *
     * @param yamlFilePath path to the YAML file
     * @return SQL statements as a string
     * @throws IOException if file cannot be read
     */
    public static String convertFile(String yamlFilePath) throws IOException {
        return new LiquibaseYamlToSqlConverter().convert(yamlFilePath);
    }

    /**
     * Convert a Liquibase YAML changelog file to SQL with specified dialect.
     *
     * @param yamlFilePath path to the YAML file
     * @param dialect      SQL dialect (mysql, postgresql, h2)
     * @return SQL statements as a string
     * @throws IOException if file cannot be read
     */
    public static String convertFile(String yamlFilePath, String dialect) throws IOException {
        return new LiquibaseYamlToSqlConverter(dialect).convert(yamlFilePath);
    }

    /**
     * Convert and save to a SQL file.
     *
     * @param yamlFilePath input YAML file path
     * @param sqlFilePath  output SQL file path
     * @throws IOException if file operations fail
     */
    public static void convertFileAndSave(String yamlFilePath, String sqlFilePath) throws IOException {
        String sql = convertFile(yamlFilePath);
        Files.writeString(Path.of(sqlFilePath), sql);
    }

    /**
     * Convert all YAML files in a directory to SQL files.
     *
     * @param inputDir  directory containing YAML files
     * @param outputDir directory for SQL output files
     * @throws IOException if file operations fail
     */
    public static void convertDirectory(String inputDir, String outputDir) throws IOException {
        convertDirectory(inputDir, outputDir, DEFAULT_DIALECT);
    }

    /**
     * Convert all YAML files in a directory to SQL files with specified dialect.
     *
     * @param inputDir  directory containing YAML files
     * @param outputDir directory for SQL output files
     * @param dialect   SQL dialect
     * @throws IOException if file operations fail
     */
    public static void convertDirectory(String inputDir, String outputDir, String dialect) throws IOException {
        Path input = Path.of(inputDir);
        Path output = Path.of(outputDir);

        Files.createDirectories(output);

        LiquibaseYamlToSqlConverter converter = new LiquibaseYamlToSqlConverter(dialect);

        try (var files = Files.list(input)) {
            files.filter(p -> p.toString().endsWith(".yaml") || p.toString().endsWith(".yml"))
                 .filter(p -> !p.getFileName().toString().contains("master"))
                 .sorted()
                 .forEach(yamlFile -> {
                     try {
                         String sql = converter.convert(yamlFile.toString());
                         String sqlFileName = yamlFile.getFileName().toString()
                                 .replaceAll("\\.ya?ml$", ".sql");
                         Files.writeString(output.resolve(sqlFileName), sql);
                         System.out.println("Converted: " + yamlFile.getFileName() + " -> " + sqlFileName);
                     } catch (IOException e) {
                         System.err.println("Error converting " + yamlFile + ": " + e.getMessage());
                     }
                 });
        }
    }

    /**
     * Convert YAML content to SQL.
     *
     * @param yamlFilePath path to YAML file
     * @return SQL statements
     * @throws IOException if file cannot be read
     */
    public String convert(String yamlFilePath) throws IOException {
        String yamlContent = Files.readString(Path.of(yamlFilePath));
        return convertYamlContent(yamlContent, Path.of(yamlFilePath).getFileName().toString());
    }

    /**
     * Convert YAML content string to Liquibase SQL format.
     *
     * @param yamlContent YAML content as string
     * @param sourceName  source file name for comments
     * @return Liquibase-formatted SQL statements
     */
    @SuppressWarnings("unchecked")
    public String convertYamlContent(String yamlContent, String sourceName) {
        Yaml yaml = new Yaml();
        Map<String, Object> root = yaml.load(yamlContent);

        if (root == null || !root.containsKey("databaseChangeLog")) {
            return "--liquibase formatted sql\n\n-- No databaseChangeLog found in " + sourceName + "\n";
        }

        StringBuilder sql = new StringBuilder();
        sql.append("--liquibase formatted sql\n\n");

        List<Map<String, Object>> changeLog = (List<Map<String, Object>>) root.get("databaseChangeLog");

        for (Map<String, Object> entry : changeLog) {
            if (entry.containsKey("changeSet")) {
                Map<String, Object> changeSet = (Map<String, Object>) entry.get("changeSet");
                sql.append(processChangeSet(changeSet));
            }
        }

        return sql.toString();
    }

    @SuppressWarnings("unchecked")
    private String processChangeSet(Map<String, Object> changeSet) {
        StringBuilder sql = new StringBuilder();

        String id = String.valueOf(changeSet.get("id"));
        String author = String.valueOf(changeSet.getOrDefault("author", "unknown"));
        String comment = (String) changeSet.get("comment");

        // Liquibase SQL format: --changeset author:id
        sql.append("--changeset ").append(author).append(":").append(id).append("\n");

        if (comment != null) {
            sql.append("--comment: ").append(comment).append("\n");
        }

        // Handle preConditions
        if (changeSet.containsKey("preConditions")) {
            sql.append(processPreConditions(changeSet.get("preConditions")));
        }

        List<Map<String, Object>> changes = (List<Map<String, Object>>) changeSet.get("changes");
        if (changes != null) {
            for (Map<String, Object> change : changes) {
                sql.append(processChange(change));
            }
        }

        sql.append("\n");
        return sql.toString();
    }

    @SuppressWarnings("unchecked")
    private String processPreConditions(Object preConditions) {
        StringBuilder sql = new StringBuilder();

        if (preConditions instanceof List) {
            List<Object> conditions = (List<Object>) preConditions;
            for (Object condition : conditions) {
                if (condition instanceof Map) {
                    Map<String, Object> condMap = (Map<String, Object>) condition;

                    // Handle onFail
                    if (condMap.containsKey("onFail")) {
                        sql.append("--preconditions onFail:").append(condMap.get("onFail")).append("\n");
                    }

                    // Handle sqlCheck
                    if (condMap.containsKey("sqlCheck")) {
                        Map<String, Object> sqlCheck = (Map<String, Object>) condMap.get("sqlCheck");
                        String expectedResult = getString(sqlCheck, "expectedResult");
                        String checkSql = getString(sqlCheck, "sql");
                        if (expectedResult != null && checkSql != null) {
                            sql.append("--precondition-sql-check expectedResult:").append(expectedResult)
                               .append(" ").append(checkSql.trim().replace("\n", " ")).append("\n");
                        }
                    }

                    // Handle tableExists check (wrapped in 'not')
                    if (condMap.containsKey("not")) {
                        Object notCond = condMap.get("not");
                        if (notCond instanceof Map) {
                            Map<String, Object> notMap = (Map<String, Object>) notCond;
                            if (notMap.containsKey("tableExists")) {
                                Map<String, Object> tableCheck = (Map<String, Object>) notMap.get("tableExists");
                                String tableName = getString(tableCheck, "tableName");
                                sql.append("--precondition-sql-check expectedResult:0 ")
                                   .append("SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = '")
                                   .append(tableName).append("' AND TABLE_SCHEMA = DATABASE()\n");
                            }
                        }
                    }

                    // Handle direct tableExists
                    if (condMap.containsKey("tableExists")) {
                        Map<String, Object> tableCheck = (Map<String, Object>) condMap.get("tableExists");
                        String tableName = getString(tableCheck, "tableName");
                        sql.append("--precondition-sql-check expectedResult:1 ")
                           .append("SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = '")
                           .append(tableName).append("' AND TABLE_SCHEMA = DATABASE()\n");
                    }
                }
            }
        }

        return sql.toString();
    }

    @SuppressWarnings("unchecked")
    private String processChange(Map<String, Object> change) {
        StringBuilder sql = new StringBuilder();

        for (Map.Entry<String, Object> entry : change.entrySet()) {
            String changeType = entry.getKey();
            Object changeData = entry.getValue();

            switch (changeType) {
                case "createTable" -> sql.append(processCreateTable((Map<String, Object>) changeData));
                case "dropTable" -> sql.append(processDropTable((Map<String, Object>) changeData));
                case "renameTable" -> sql.append(processRenameTable((Map<String, Object>) changeData));
                case "addColumn" -> sql.append(processAddColumn((Map<String, Object>) changeData));
                case "dropColumn" -> sql.append(processDropColumn((Map<String, Object>) changeData));
                case "renameColumn" -> sql.append(processRenameColumn((Map<String, Object>) changeData));
                case "modifyDataType" -> sql.append(processModifyDataType((Map<String, Object>) changeData));
                case "createIndex" -> sql.append(processCreateIndex((Map<String, Object>) changeData));
                case "dropIndex" -> sql.append(processDropIndex((Map<String, Object>) changeData));
                case "addPrimaryKey" -> sql.append(processAddPrimaryKey((Map<String, Object>) changeData));
                case "dropPrimaryKey" -> sql.append(processDropPrimaryKey((Map<String, Object>) changeData));
                case "addForeignKeyConstraint" -> sql.append(processAddForeignKey((Map<String, Object>) changeData));
                case "dropForeignKeyConstraint" -> sql.append(processDropForeignKey((Map<String, Object>) changeData));
                case "addUniqueConstraint" -> sql.append(processAddUniqueConstraint((Map<String, Object>) changeData));
                case "dropUniqueConstraint" -> sql.append(processDropUniqueConstraint((Map<String, Object>) changeData));
                case "addNotNullConstraint" -> sql.append(processAddNotNull((Map<String, Object>) changeData));
                case "dropNotNullConstraint" -> sql.append(processDropNotNull((Map<String, Object>) changeData));
                case "addDefaultValue" -> sql.append(processAddDefaultValue((Map<String, Object>) changeData));
                case "dropDefaultValue" -> sql.append(processDropDefaultValue((Map<String, Object>) changeData));
                case "insert" -> sql.append(processInsert((Map<String, Object>) changeData));
                case "update" -> sql.append(processUpdate((Map<String, Object>) changeData));
                case "delete" -> sql.append(processDelete((Map<String, Object>) changeData));
                case "sql" -> sql.append(processSql(changeData));
                case "sqlFile" -> sql.append(processSqlFile((Map<String, Object>) changeData));
                case "createSequence" -> sql.append(processCreateSequence((Map<String, Object>) changeData));
                case "dropSequence" -> sql.append(processDropSequence((Map<String, Object>) changeData));
                case "createView" -> sql.append(processCreateView((Map<String, Object>) changeData));
                case "dropView" -> sql.append(processDropView((Map<String, Object>) changeData));
                case "setTableRemarks" -> sql.append(processSetTableRemarks((Map<String, Object>) changeData));
                case "setColumnRemarks" -> sql.append(processSetColumnRemarks((Map<String, Object>) changeData));
                default -> sql.append("-- Unsupported change type: ").append(changeType).append("\n");
            }
        }

        return sql.toString();
    }

    @SuppressWarnings("unchecked")
    private String processCreateTable(Map<String, Object> data) {
        StringBuilder sql = new StringBuilder();
        String tableName = getString(data, "tableName");
        String remarks = getString(data, "remarks");

        sql.append("CREATE TABLE ").append(tableName).append(" (\n");

        List<Map<String, Object>> columns = (List<Map<String, Object>>) data.get("columns");
        List<String> columnDefs = new ArrayList<>();
        List<String> constraints = new ArrayList<>();

        if (columns != null) {
            for (Map<String, Object> colWrapper : columns) {
                Map<String, Object> column = (Map<String, Object>) colWrapper.get("column");
                if (column != null) {
                    String colDef = processColumnDefinition(column, constraints);
                    columnDefs.add(colDef);
                }
            }
        }

        columnDefs.addAll(constraints);
        sql.append(columnDefs.stream().map(c -> "    " + c).collect(Collectors.joining(",\n")));
        sql.append("\n)");

        if ("mysql".equals(dialect)) {
            sql.append(" ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");
        }

        sql.append(";\n");

        if (remarks != null && !remarks.isEmpty()) {
            sql.append(processSetTableRemarks(Map.of("tableName", tableName, "remarks", remarks)));
        }

        return sql.toString();
    }

    @SuppressWarnings("unchecked")
    private String processColumnDefinition(Map<String, Object> column, List<String> constraints) {
        StringBuilder def = new StringBuilder();

        String name = getString(column, "name");
        String type = getString(column, "type");
        Object defaultValue = column.get("defaultValue");
        Object defaultValueComputed = column.get("defaultValueComputed");
        Object defaultValueBoolean = column.get("defaultValueBoolean");
        Object defaultValueNumeric = column.get("defaultValueNumeric");
        Object defaultValueDate = column.get("defaultValueDate");
        Boolean autoIncrement = getBoolean(column, "autoIncrement");
        String remarks = getString(column, "remarks");

        Map<String, Object> constraintsMap = (Map<String, Object>) column.get("constraints");
        boolean primaryKey = false;
        boolean nullable = true;
        boolean unique = false;
        String foreignKeyName = null;
        String references = null;

        if (constraintsMap != null) {
            primaryKey = Boolean.TRUE.equals(getBoolean(constraintsMap, "primaryKey"));
            nullable = !Boolean.TRUE.equals(getBoolean(constraintsMap, "nullable") == null ? true :
                       Boolean.FALSE.equals(getBoolean(constraintsMap, "nullable")));
            if (constraintsMap.containsKey("nullable")) {
                nullable = Boolean.TRUE.equals(getBoolean(constraintsMap, "nullable"));
            }
            unique = Boolean.TRUE.equals(getBoolean(constraintsMap, "unique"));
            foreignKeyName = getString(constraintsMap, "foreignKeyName");
            references = getString(constraintsMap, "references");
        }

        def.append(name).append(" ").append(convertDataType(type));

        if (!nullable || primaryKey) {
            def.append(" NOT NULL");
        }

        if (Boolean.TRUE.equals(autoIncrement)) {
            if ("mysql".equals(dialect)) {
                def.append(" AUTO_INCREMENT");
            } else if ("postgresql".equals(dialect)) {
                // PostgreSQL uses SERIAL or IDENTITY
            }
        }

        // Handle default values
        if (defaultValue != null) {
            def.append(" DEFAULT ").append(formatDefaultValue(defaultValue));
        } else if (defaultValueComputed != null) {
            def.append(" DEFAULT ").append(defaultValueComputed);
        } else if (defaultValueBoolean != null) {
            def.append(" DEFAULT ").append(Boolean.TRUE.equals(defaultValueBoolean) ? "1" : "0");
        } else if (defaultValueNumeric != null) {
            def.append(" DEFAULT ").append(defaultValueNumeric);
        } else if (defaultValueDate != null) {
            if ("CURRENT_TIMESTAMP".equalsIgnoreCase(String.valueOf(defaultValueDate))) {
                def.append(" DEFAULT CURRENT_TIMESTAMP");
            } else {
                def.append(" DEFAULT '").append(defaultValueDate).append("'");
            }
        }

        if (unique && !primaryKey) {
            def.append(" UNIQUE");
        }

        if (primaryKey) {
            constraints.add("PRIMARY KEY (" + name + ")");
        }

        if (foreignKeyName != null && references != null) {
            String[] refParts = references.split("\\(");
            String refTable = refParts[0].trim();
            String refColumn = refParts.length > 1 ? refParts[1].replace(")", "").trim() : "ID";
            constraints.add("CONSTRAINT " + foreignKeyName + " FOREIGN KEY (" + name + ") REFERENCES " + refTable + "(" + refColumn + ")");
        }

        return def.toString();
    }

    private String processDropTable(Map<String, Object> data) {
        String tableName = getString(data, "tableName");
        Boolean cascade = getBoolean(data, "cascadeConstraints");

        StringBuilder sql = new StringBuilder();
        sql.append("DROP TABLE IF EXISTS ").append(tableName);
        if (Boolean.TRUE.equals(cascade) && "postgresql".equals(dialect)) {
            sql.append(" CASCADE");
        }
        sql.append(";\n");
        return sql.toString();
    }

    private String processRenameTable(Map<String, Object> data) {
        String oldName = getString(data, "oldTableName");
        String newName = getString(data, "newTableName");

        if ("mysql".equals(dialect)) {
            return "RENAME TABLE " + oldName + " TO " + newName + ";\n";
        } else {
            return "ALTER TABLE " + oldName + " RENAME TO " + newName + ";\n";
        }
    }

    @SuppressWarnings("unchecked")
    private String processAddColumn(Map<String, Object> data) {
        StringBuilder sql = new StringBuilder();
        String tableName = getString(data, "tableName");

        List<Map<String, Object>> columns = (List<Map<String, Object>>) data.get("columns");
        if (columns != null) {
            for (Map<String, Object> colWrapper : columns) {
                Map<String, Object> column = (Map<String, Object>) colWrapper.get("column");
                if (column != null) {
                    List<String> constraints = new ArrayList<>();
                    String colDef = processColumnDefinition(column, constraints);
                    sql.append("ALTER TABLE ").append(tableName).append(" ADD COLUMN ").append(colDef).append(";\n");
                    for (String constraint : constraints) {
                        sql.append("ALTER TABLE ").append(tableName).append(" ADD ").append(constraint).append(";\n");
                    }
                }
            }
        }

        return sql.toString();
    }

    private String processDropColumn(Map<String, Object> data) {
        String tableName = getString(data, "tableName");
        String columnName = getString(data, "columnName");
        return "ALTER TABLE " + tableName + " DROP COLUMN " + columnName + ";\n";
    }

    private String processRenameColumn(Map<String, Object> data) {
        String tableName = getString(data, "tableName");
        String oldName = getString(data, "oldColumnName");
        String newName = getString(data, "newColumnName");
        String columnDataType = getString(data, "columnDataType");

        if ("mysql".equals(dialect) && columnDataType != null) {
            return "ALTER TABLE " + tableName + " CHANGE " + oldName + " " + newName + " " + convertDataType(columnDataType) + ";\n";
        } else {
            return "ALTER TABLE " + tableName + " RENAME COLUMN " + oldName + " TO " + newName + ";\n";
        }
    }

    private String processModifyDataType(Map<String, Object> data) {
        String tableName = getString(data, "tableName");
        String columnName = getString(data, "columnName");
        String newDataType = getString(data, "newDataType");

        if ("mysql".equals(dialect)) {
            return "ALTER TABLE " + tableName + " MODIFY COLUMN " + columnName + " " + convertDataType(newDataType) + ";\n";
        } else {
            return "ALTER TABLE " + tableName + " ALTER COLUMN " + columnName + " TYPE " + convertDataType(newDataType) + ";\n";
        }
    }

    @SuppressWarnings("unchecked")
    private String processCreateIndex(Map<String, Object> data) {
        StringBuilder sql = new StringBuilder();
        String indexName = getString(data, "indexName");
        String tableName = getString(data, "tableName");
        Boolean unique = getBoolean(data, "unique");

        List<Map<String, Object>> columns = (List<Map<String, Object>>) data.get("columns");
        List<String> columnNames = new ArrayList<>();

        if (columns != null) {
            for (Map<String, Object> colWrapper : columns) {
                Map<String, Object> column = (Map<String, Object>) colWrapper.get("column");
                if (column != null) {
                    columnNames.add(getString(column, "name"));
                }
            }
        }

        // Handle single column attribute
        String singleColumn = getString(data, "columnNames");
        if (singleColumn != null) {
            columnNames.addAll(Arrays.asList(singleColumn.split(",")));
        }

        sql.append("CREATE ");
        if (Boolean.TRUE.equals(unique)) {
            sql.append("UNIQUE ");
        }
        sql.append("INDEX ").append(indexName).append(" ON ").append(tableName);
        sql.append(" (").append(String.join(", ", columnNames)).append(");\n");

        return sql.toString();
    }

    private String processDropIndex(Map<String, Object> data) {
        String indexName = getString(data, "indexName");
        String tableName = getString(data, "tableName");

        if ("mysql".equals(dialect)) {
            return "DROP INDEX " + indexName + " ON " + tableName + ";\n";
        } else {
            return "DROP INDEX IF EXISTS " + indexName + ";\n";
        }
    }

    private String processAddPrimaryKey(Map<String, Object> data) {
        String tableName = getString(data, "tableName");
        String columnNames = getString(data, "columnNames");
        String constraintName = getString(data, "constraintName");

        StringBuilder sql = new StringBuilder();
        sql.append("ALTER TABLE ").append(tableName).append(" ADD ");
        if (constraintName != null) {
            sql.append("CONSTRAINT ").append(constraintName).append(" ");
        }
        sql.append("PRIMARY KEY (").append(columnNames).append(");\n");
        return sql.toString();
    }

    private String processDropPrimaryKey(Map<String, Object> data) {
        String tableName = getString(data, "tableName");
        String constraintName = getString(data, "constraintName");

        if ("mysql".equals(dialect)) {
            return "ALTER TABLE " + tableName + " DROP PRIMARY KEY;\n";
        } else {
            return "ALTER TABLE " + tableName + " DROP CONSTRAINT " +
                   (constraintName != null ? constraintName : tableName + "_pkey") + ";\n";
        }
    }

    private String processAddForeignKey(Map<String, Object> data) {
        String constraintName = getString(data, "constraintName");
        String baseTableName = getString(data, "baseTableName");
        String baseColumnNames = getString(data, "baseColumnNames");
        String referencedTableName = getString(data, "referencedTableName");
        String referencedColumnNames = getString(data, "referencedColumnNames");
        String onDelete = getString(data, "onDelete");
        String onUpdate = getString(data, "onUpdate");

        StringBuilder sql = new StringBuilder();
        sql.append("ALTER TABLE ").append(baseTableName);
        sql.append(" ADD CONSTRAINT ").append(constraintName);
        sql.append(" FOREIGN KEY (").append(baseColumnNames).append(")");
        sql.append(" REFERENCES ").append(referencedTableName).append("(").append(referencedColumnNames).append(")");

        if (onDelete != null) {
            sql.append(" ON DELETE ").append(onDelete);
        }
        if (onUpdate != null) {
            sql.append(" ON UPDATE ").append(onUpdate);
        }
        sql.append(";\n");
        return sql.toString();
    }

    private String processDropForeignKey(Map<String, Object> data) {
        String constraintName = getString(data, "constraintName");
        String baseTableName = getString(data, "baseTableName");

        if ("mysql".equals(dialect)) {
            return "ALTER TABLE " + baseTableName + " DROP FOREIGN KEY " + constraintName + ";\n";
        } else {
            return "ALTER TABLE " + baseTableName + " DROP CONSTRAINT " + constraintName + ";\n";
        }
    }

    private String processAddUniqueConstraint(Map<String, Object> data) {
        String tableName = getString(data, "tableName");
        String columnNames = getString(data, "columnNames");
        String constraintName = getString(data, "constraintName");

        StringBuilder sql = new StringBuilder();
        sql.append("ALTER TABLE ").append(tableName).append(" ADD ");
        if (constraintName != null) {
            sql.append("CONSTRAINT ").append(constraintName).append(" ");
        }
        sql.append("UNIQUE (").append(columnNames).append(");\n");
        return sql.toString();
    }

    private String processDropUniqueConstraint(Map<String, Object> data) {
        String tableName = getString(data, "tableName");
        String constraintName = getString(data, "constraintName");

        if ("mysql".equals(dialect)) {
            return "ALTER TABLE " + tableName + " DROP INDEX " + constraintName + ";\n";
        } else {
            return "ALTER TABLE " + tableName + " DROP CONSTRAINT " + constraintName + ";\n";
        }
    }

    private String processAddNotNull(Map<String, Object> data) {
        String tableName = getString(data, "tableName");
        String columnName = getString(data, "columnName");
        String columnDataType = getString(data, "columnDataType");

        if ("mysql".equals(dialect)) {
            return "ALTER TABLE " + tableName + " MODIFY COLUMN " + columnName + " " +
                   convertDataType(columnDataType) + " NOT NULL;\n";
        } else {
            return "ALTER TABLE " + tableName + " ALTER COLUMN " + columnName + " SET NOT NULL;\n";
        }
    }

    private String processDropNotNull(Map<String, Object> data) {
        String tableName = getString(data, "tableName");
        String columnName = getString(data, "columnName");
        String columnDataType = getString(data, "columnDataType");

        if ("mysql".equals(dialect)) {
            return "ALTER TABLE " + tableName + " MODIFY COLUMN " + columnName + " " +
                   convertDataType(columnDataType) + " NULL;\n";
        } else {
            return "ALTER TABLE " + tableName + " ALTER COLUMN " + columnName + " DROP NOT NULL;\n";
        }
    }

    private String processAddDefaultValue(Map<String, Object> data) {
        String tableName = getString(data, "tableName");
        String columnName = getString(data, "columnName");
        Object defaultValue = data.get("defaultValue");
        Object defaultValueComputed = data.get("defaultValueComputed");
        Object defaultValueBoolean = data.get("defaultValueBoolean");
        Object defaultValueNumeric = data.get("defaultValueNumeric");

        String value;
        if (defaultValue != null) {
            value = formatDefaultValue(defaultValue);
        } else if (defaultValueComputed != null) {
            value = String.valueOf(defaultValueComputed);
        } else if (defaultValueBoolean != null) {
            value = Boolean.TRUE.equals(defaultValueBoolean) ? "1" : "0";
        } else if (defaultValueNumeric != null) {
            value = String.valueOf(defaultValueNumeric);
        } else {
            value = "NULL";
        }

        if ("mysql".equals(dialect)) {
            String columnDataType = getString(data, "columnDataType");
            return "ALTER TABLE " + tableName + " ALTER COLUMN " + columnName + " SET DEFAULT " + value + ";\n";
        } else {
            return "ALTER TABLE " + tableName + " ALTER COLUMN " + columnName + " SET DEFAULT " + value + ";\n";
        }
    }

    private String processDropDefaultValue(Map<String, Object> data) {
        String tableName = getString(data, "tableName");
        String columnName = getString(data, "columnName");

        return "ALTER TABLE " + tableName + " ALTER COLUMN " + columnName + " DROP DEFAULT;\n";
    }

    @SuppressWarnings("unchecked")
    private String processInsert(Map<String, Object> data) {
        StringBuilder sql = new StringBuilder();
        String tableName = getString(data, "tableName");

        List<Map<String, Object>> columns = (List<Map<String, Object>>) data.get("columns");
        if (columns != null && !columns.isEmpty()) {
            List<String> columnNames = new ArrayList<>();
            List<String> values = new ArrayList<>();

            for (Map<String, Object> colWrapper : columns) {
                Map<String, Object> column = (Map<String, Object>) colWrapper.get("column");
                if (column != null) {
                    columnNames.add(getString(column, "name"));
                    Object value = column.get("value");
                    Object valueNumeric = column.get("valueNumeric");
                    Object valueBoolean = column.get("valueBoolean");
                    Object valueComputed = column.get("valueComputed");
                    Object valueDate = column.get("valueDate");

                    if (value != null) {
                        values.add(formatValue(value));
                    } else if (valueNumeric != null) {
                        values.add(String.valueOf(valueNumeric));
                    } else if (valueBoolean != null) {
                        values.add(Boolean.TRUE.equals(valueBoolean) ? "1" : "0");
                    } else if (valueComputed != null) {
                        values.add(String.valueOf(valueComputed));
                    } else if (valueDate != null) {
                        if ("CURRENT_TIMESTAMP".equalsIgnoreCase(String.valueOf(valueDate)) ||
                            "NOW()".equalsIgnoreCase(String.valueOf(valueDate))) {
                            values.add("NOW()");
                        } else {
                            values.add("'" + valueDate + "'");
                        }
                    } else {
                        values.add("NULL");
                    }
                }
            }

            sql.append("INSERT INTO ").append(tableName);
            sql.append(" (").append(String.join(", ", columnNames)).append(")");
            sql.append(" VALUES (").append(String.join(", ", values)).append(");\n");
        }

        return sql.toString();
    }

    @SuppressWarnings("unchecked")
    private String processUpdate(Map<String, Object> data) {
        StringBuilder sql = new StringBuilder();
        String tableName = getString(data, "tableName");
        String where = getString(data, "where");

        List<Map<String, Object>> columns = (List<Map<String, Object>>) data.get("columns");
        if (columns != null && !columns.isEmpty()) {
            List<String> setClauses = new ArrayList<>();

            for (Map<String, Object> colWrapper : columns) {
                Map<String, Object> column = (Map<String, Object>) colWrapper.get("column");
                if (column != null) {
                    String name = getString(column, "name");
                    Object value = column.get("value");
                    Object valueNumeric = column.get("valueNumeric");
                    Object valueBoolean = column.get("valueBoolean");

                    String formattedValue;
                    if (value != null) {
                        formattedValue = formatValue(value);
                    } else if (valueNumeric != null) {
                        formattedValue = String.valueOf(valueNumeric);
                    } else if (valueBoolean != null) {
                        formattedValue = Boolean.TRUE.equals(valueBoolean) ? "1" : "0";
                    } else {
                        formattedValue = "NULL";
                    }

                    setClauses.add(name + " = " + formattedValue);
                }
            }

            sql.append("UPDATE ").append(tableName);
            sql.append(" SET ").append(String.join(", ", setClauses));
            if (where != null) {
                sql.append(" WHERE ").append(where);
            }
            sql.append(";\n");
        }

        return sql.toString();
    }

    private String processDelete(Map<String, Object> data) {
        String tableName = getString(data, "tableName");
        String where = getString(data, "where");

        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ").append(tableName);
        if (where != null) {
            sql.append(" WHERE ").append(where);
        }
        sql.append(";\n");
        return sql.toString();
    }

    @SuppressWarnings("unchecked")
    private String processSql(Object data) {
        if (data instanceof String) {
            return data + (((String) data).endsWith(";") ? "" : ";") + "\n";
        } else if (data instanceof Map) {
            Map<String, Object> sqlData = (Map<String, Object>) data;
            String sqlStatement = getString(sqlData, "sql");
            if (sqlStatement != null) {
                return sqlStatement + (sqlStatement.endsWith(";") ? "" : ";") + "\n";
            }
        }
        return "";
    }

    private String processSqlFile(Map<String, Object> data) {
        String path = getString(data, "path");
        return "-- Include SQL file: " + path + "\n-- \\i " + path + "\n";
    }

    private String processCreateSequence(Map<String, Object> data) {
        String sequenceName = getString(data, "sequenceName");
        Object startValue = data.get("startValue");
        Object incrementBy = data.get("incrementBy");
        Object minValue = data.get("minValue");
        Object maxValue = data.get("maxValue");
        Boolean cycle = getBoolean(data, "cycle");

        StringBuilder sql = new StringBuilder();
        sql.append("CREATE SEQUENCE ").append(sequenceName);

        if (startValue != null) {
            sql.append(" START WITH ").append(startValue);
        }
        if (incrementBy != null) {
            sql.append(" INCREMENT BY ").append(incrementBy);
        }
        if (minValue != null) {
            sql.append(" MINVALUE ").append(minValue);
        }
        if (maxValue != null) {
            sql.append(" MAXVALUE ").append(maxValue);
        }
        if (Boolean.TRUE.equals(cycle)) {
            sql.append(" CYCLE");
        }
        sql.append(";\n");
        return sql.toString();
    }

    private String processDropSequence(Map<String, Object> data) {
        String sequenceName = getString(data, "sequenceName");
        return "DROP SEQUENCE IF EXISTS " + sequenceName + ";\n";
    }

    private String processCreateView(Map<String, Object> data) {
        String viewName = getString(data, "viewName");
        String selectQuery = getString(data, "selectQuery");
        Boolean replaceIfExists = getBoolean(data, "replaceIfExists");

        StringBuilder sql = new StringBuilder();
        sql.append("CREATE ");
        if (Boolean.TRUE.equals(replaceIfExists)) {
            sql.append("OR REPLACE ");
        }
        sql.append("VIEW ").append(viewName).append(" AS\n").append(selectQuery).append(";\n");
        return sql.toString();
    }

    private String processDropView(Map<String, Object> data) {
        String viewName = getString(data, "viewName");
        return "DROP VIEW IF EXISTS " + viewName + ";\n";
    }

    private String processSetTableRemarks(Map<String, Object> data) {
        String tableName = getString(data, "tableName");
        String remarks = getString(data, "remarks");

        if ("mysql".equals(dialect)) {
            return "ALTER TABLE " + tableName + " COMMENT = '" + escapeString(remarks) + "';\n";
        } else {
            return "COMMENT ON TABLE " + tableName + " IS '" + escapeString(remarks) + "';\n";
        }
    }

    private String processSetColumnRemarks(Map<String, Object> data) {
        String tableName = getString(data, "tableName");
        String columnName = getString(data, "columnName");
        String remarks = getString(data, "remarks");

        if ("mysql".equals(dialect)) {
            return "-- MySQL column comments require full column definition\n" +
                   "-- ALTER TABLE " + tableName + " MODIFY " + columnName + " ... COMMENT '" + escapeString(remarks) + "';\n";
        } else {
            return "COMMENT ON COLUMN " + tableName + "." + columnName + " IS '" + escapeString(remarks) + "';\n";
        }
    }

    // Helper methods

    private String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? String.valueOf(value) : null;
    }

    private Boolean getBoolean(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return null;
    }

    private String convertDataType(String liquibaseType) {
        if (liquibaseType == null) return "VARCHAR(255)";

        String type = liquibaseType.toUpperCase();

        // Handle parameterized types
        if (type.contains("(")) {
            String baseType = type.substring(0, type.indexOf("("));
            String params = type.substring(type.indexOf("("));
            return convertBaseType(baseType) + params;
        }

        return convertBaseType(type);
    }

    private String convertBaseType(String baseType) {
        return switch (baseType) {
            case "BIGINT" -> "BIGINT";
            case "BLOB" -> "mysql".equals(dialect) ? "LONGBLOB" : "BYTEA";
            case "BOOLEAN", "BOOL" -> "mysql".equals(dialect) ? "TINYINT(1)" : "BOOLEAN";
            case "CHAR" -> "CHAR";
            case "CLOB" -> "mysql".equals(dialect) ? "LONGTEXT" : "TEXT";
            case "CURRENCY" -> "DECIMAL(19,4)";
            case "DATE" -> "DATE";
            case "DATETIME" -> "mysql".equals(dialect) ? "DATETIME" : "TIMESTAMP";
            case "DECIMAL", "NUMBER" -> "DECIMAL";
            case "DOUBLE" -> "DOUBLE PRECISION";
            case "FLOAT" -> "FLOAT";
            case "INT", "INTEGER" -> "INT";
            case "MEDIUMINT" -> "MEDIUMINT";
            case "NCHAR" -> "NCHAR";
            case "NVARCHAR" -> "NVARCHAR";
            case "SMALLINT" -> "SMALLINT";
            case "TEXT" -> "TEXT";
            case "TIME" -> "TIME";
            case "TIMESTAMP" -> "TIMESTAMP";
            case "TINYINT" -> "TINYINT";
            case "UUID" -> "mysql".equals(dialect) ? "CHAR(36)" : "UUID";
            case "VARCHAR" -> "VARCHAR";
            case "JSON" -> "mysql".equals(dialect) ? "JSON" : "JSONB";
            default -> baseType;
        };
    }

    private String formatDefaultValue(Object value) {
        if (value == null) return "NULL";
        String strValue = String.valueOf(value);
        if ("NULL".equalsIgnoreCase(strValue)) return "NULL";
        if ("CURRENT_TIMESTAMP".equalsIgnoreCase(strValue) ||
            "NOW()".equalsIgnoreCase(strValue)) {
            return "CURRENT_TIMESTAMP";
        }
        if (strValue.matches("-?\\d+(\\.\\d+)?")) {
            return strValue;
        }
        return "'" + escapeString(strValue) + "'";
    }

    private String formatValue(Object value) {
        if (value == null) return "NULL";
        String strValue = String.valueOf(value);
        if ("NULL".equalsIgnoreCase(strValue)) return "NULL";
        return "'" + escapeString(strValue) + "'";
    }

    private String escapeString(String value) {
        if (value == null) return "";
        return value.replace("'", "''");
    }

    /**
     * Main method for command-line usage.
     *
     * @param args [0] = input file/dir, [1] = output file/dir (optional), [2] = dialect (optional)
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: LiquibaseYamlToSqlConverter <input> [output] [dialect]");
            System.out.println("  input  - YAML file or directory containing YAML files");
            System.out.println("  output - SQL file or directory for output (optional)");
            System.out.println("  dialect - mysql (default), postgresql, h2");
            System.exit(1);
        }

        String input = args[0];
        String output = args.length > 1 ? args[1] : null;
        String dialect = args.length > 2 ? args[2] : "mysql";

        try {
            Path inputPath = Path.of(input);

            if (Files.isDirectory(inputPath)) {
                String outputDir = output != null ? output : input + "_sql";
                convertDirectory(input, outputDir, dialect);
                System.out.println("Conversion complete. Output in: " + outputDir);
            } else {
                String sql = convertFile(input, dialect);
                if (output != null) {
                    Files.writeString(Path.of(output), sql);
                    System.out.println("Conversion complete. Output in: " + output);
                } else {
                    System.out.println(sql);
                }
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}
