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
            stringBuilder.append("CREATE TABLE [IF NOT EXISTS] " + table.getTableName() + " (");
            stringBuilder.append("\n");
            writeKey(stringBuilder, table.getPrimaryKey(), KeyType.PRIMARY);
            stringBuilder.append(",\n");

            table.getColumnList().forEach(sqlColumn -> {
                if (ClassUtils.isPrimitiveOrWrapper(sqlColumn.getFieldType())) {
                    writeKey(stringBuilder, sqlColumn, KeyType.PRIMITIVE);
                } else {
                    writeKey(stringBuilder, sqlColumn, KeyType.FOREIGN);
                }
                stringBuilder.append(",\n");
            });

            int lastComma = stringBuilder.lastIndexOf(",");
            stringBuilder.replace(lastComma, lastComma + 1, "");
            stringBuilder.append(");");

        });
        return stringBuilder.toString();
    }

    private void writeKey(StringBuilder stringBuilder, SqlColumn sqlColumn, KeyType keyType) {
        String key = "";
        if (keyType == KeyType.PRIMARY)
            key = sqlColumn.getSqlKey().getPrimaryKey();
        else if(keyType == KeyType.FOREIGN)
            key = sqlColumn.getSqlKey().getForeignKey();
        else if (keyType == KeyType.PRIMITIVE)
            key = "";
        try {
            stringBuilder.append(" ")
                    .append(key)
                    .append(" ")
                    .append(sqlColumn.dataType())
                    .append(" ")
                    .append(keyType.toString());
        } catch (LackOfPrimaryKey lackOfPrimaryKey) {
            lackOfPrimaryKey.metaMappingException(sqlColumn.getFieldType());
        }
    }




}