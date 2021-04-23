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
            writeColumn(stringBuilder, table.getPrimaryKey());
            writeKey(stringBuilder, table.getPrimaryKey(), KeyType.PRIMARY);
            stringBuilder.append(",\n");

            table.getColumnList().forEach(sqlColumn -> {
                if (ClassUtils.isPrimitiveOrWrapper(sqlColumn.getFieldType())) {
                    writeColumn(stringBuilder, sqlColumn);
                    writeKey(stringBuilder, sqlColumn, KeyType.PRIMITIVE);
                } else {
                    writeColumn(stringBuilder, sqlColumn);
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

    private void writeColumn(StringBuilder stringBuilder, SqlColumn sqlColumn) {
        KeyType keyType = sqlColumn.getSqlKey().getKeyType();
        String keyString = keyType == KeyType.FOREIGN ? sqlColumn.getSqlKey().getForeignKey() : sqlColumn.getSqlKey().getPrimaryKey();
        try {
            DataType dataType = sqlColumn.dataType();
            String key = getIdString(keyString, dataType);
            stringBuilder.append(" ")
            .append(key)
            .append(",\n");
        } catch (LackOfPrimaryKey lackOfPrimaryKey) {
            lackOfPrimaryKey.printStackTrace();
        }
    }

    private void writeKey(StringBuilder stringBuilder, SqlColumn sqlColumn, KeyType keyType) {
        String keyString = keyType == KeyType.PRIMARY ? sqlColumn.getSqlKey().getPrimaryKey() : sqlColumn.getSqlKey().getForeignKey();
        String key = getKeyString(keyString, keyType);
        try {
            stringBuilder.append(" ")
                    .append(sqlColumn.dataType())
                    .append(" ")
                    .append(key);
        } catch (LackOfPrimaryKey lackOfPrimaryKey) {
            lackOfPrimaryKey.metaMappingException(sqlColumn.getFieldType());
        }
    }

    private String getKeyStringSupp(KeyType keyType, String key, BiFunction<KeyType, String, String> supp){
        return supp.apply(keyType, key);
    }

    private String getKeyString(String key, KeyType keyType) {
        return getKeyStringSupp(keyType, key, (keyString, keyTypeVal) -> keyTypeVal +"("+keyString+")");
    }

    private String getIdString(String key, DataType dataType)  {
        return getKeyStringSupp(null, key, ((keyType1, s) -> s+" "+dataType));
    }


}