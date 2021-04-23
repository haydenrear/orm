package com.hayden.orm.table.service;

import com.hayden.orm.table.annotations.TableName;
import com.hayden.orm.table.exception.LackOfPrimaryKey;
import com.hayden.orm.table.exception.MetaMappingException;
import com.hayden.orm.table.exception.NotSqlTable;
import com.hayden.orm.table.key.KeyType;
import com.hayden.orm.table.mapper.*;
import org.reflections8.Reflections;
import org.springframework.util.ClassUtils;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TableService {

    private List<SqlTable> tables = new ArrayList<>();
    private final Reflections reflections = new Reflections();
    private final MetaMapper metaMapper = new MetaMapper();

    public Collection<Class<?>> findEntities(){
        return reflections.getTypesAnnotatedWith(TableName.class);
    }

    public List<SqlTable> getTables(){
        if(this.tables.size() == 0) {
            this.tables = findEntities()
                    .stream()
                    .map(this::getSqlTable)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        }
        return tables;
    }

    public Optional<SqlTable> getSqlTable(Class<?> entity){
        try {
            var primaryKey = metaMapper.getPrimaryKey(entity);
            if (primaryKey.isEmpty())
                throw new LackOfPrimaryKey("Table does not have primary key");
            if(entity.getAnnotation(TableName.class).tableName().length() == 0)
                throw new NotSqlTable("No table name");
            return Optional.of(
                    new SqlTable(metaMapper.getColumns(entity),
                        entity,
                        entity.getAnnotation(TableName.class).tableName(),
                        primaryKey.get(),
                        primaryKey.get().getFieldType()
                    )
            );
        } catch (MetaMappingException e) {
            e.metaMappingException(entity);
        }
        return Optional.empty();
    }

    public String createDatabaseCreationScript() {
        StringBuilder stringBuilder = new StringBuilder(1024);
        tables.forEach(table -> {

            stringBuilder.append("\n");
            stringBuilder.append("CREATE TABLE IF NOT EXISTS " + table.getTableName() + " (");
            stringBuilder.append("\n");

            writeColumn(stringBuilder, table.getPrimaryKey());
            stringBuilder.append(",\n");

            table.getColumnList().forEach(sqlColumn -> {
                writeColumn(stringBuilder, sqlColumn);
                stringBuilder.append(",\n");
            });

            int lastComma = stringBuilder.lastIndexOf(",");
            stringBuilder.replace(lastComma, lastComma + 1, "");
            stringBuilder.append(");");

        });
        return stringBuilder.toString();
    }

    private void writeColumn(StringBuilder stringBuilder, SqlColumn sqlColumn) {
        KeyType keyType = sqlColumn.getSqlKey().getKeyType();
        String columnKeyString= keyString(sqlColumn, keyType);
        try {

            DataType dataType = sqlColumn.dataType();
            String columnIdAndDataType = getIdString(columnKeyString, dataType);
            String keyTypeString = keyString(sqlColumn, keyType);
            String primaryOrForeignKey = getKeyString(keyTypeString, keyType, sqlColumn);

            stringBuilder
                .append(" ")
                .append(columnIdAndDataType)
                .append(",\n")
                .append(" ")
                .append(sqlColumn.dataType())
                .append(" ")
                .append(primaryOrForeignKey);

        } catch (LackOfPrimaryKey lackOfPrimaryKey) {
            lackOfPrimaryKey.printStackTrace();
        }
    }

    private String keyString(SqlColumn sqlColumn, KeyType keyType) {
        return keyType == KeyType.FOREIGN ? sqlColumn.getSqlKey().getForeignKey() : sqlColumn.getSqlKey().getPrimaryKey();
    }

    private String getKeyString(String key, KeyType keyType, SqlColumn sqlColumn) {
        String toAdd = "";
        if(keyType.toString().equals(KeyType.FOREIGN.toString())) {
            toAdd = " REFERENCES " + sqlColumn.getFieldType().getAnnotation(TableName.class).tableName() + "(" + sqlColumn.getSqlKey().getForeignKey() + ")";
        }
        return keyType+"("+key+")"+toAdd;
    }

    private String getIdString(String key, DataType dataType)  {
        return key+" "+dataType;
    }


}