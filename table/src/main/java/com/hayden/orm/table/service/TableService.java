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
                throw new LackOfPrimaryKey();
            if(entity.getAnnotation(TableName.class).tableName().length() == 0)
                throw new NotSqlTable("No table name");
            return Optional.of(
                    new SqlTable(metaMapper.getColumns(entity),
                        entity,
                        entity.getAnnotation(TableName.class).tableName(),
                        primaryKey.get().getSqlKey().getPrimaryKey(),
                        primaryKey.get().getFieldType()
                    )
            );
        } catch (MetaMappingException e) {
            e.metaMappingException(entity);
        }
        return Optional.empty();
    }

    public String createDatabaseCreationScript(){
        StringBuilder stringBuilder = new StringBuilder();
        tables.forEach(table -> {
               stringBuilder.append("CREATE TABLE [IF NOT EXISTS] "+table.getTableName() +" (");
               stringBuilder.append("\n");
               writeKey(stringBuilder, table.getPrimaryKey(), KeyType.PRIMARY, table.getClzz());
               table.getColumnList().forEach(sqlColumn -> {
                   if(ClassUtils.isPrimitiveOrWrapper(sqlColumn.getFieldType())){
                       stringBuilder.append(sqlColumn.getFieldType().getSimpleName()+" "+DataType.getDataType(sqlColumn.getFieldType()));
                   }
                   else {
                       stringBuilder.append(sqlColumn.getFieldType().getSimpleName()+" "+sqlColumn.getFieldType());
                       writeKey(stringBuilder, sqlColumn.getSqlKey().getForeignKey(), KeyType.FOREIGN, sqlColumn.getFieldType());
                   }
                   stringBuilder.append(",\n");
               });
            });
        return stringBuilder.toString();
    }

    private void writeKey(StringBuilder stringBuilder, String key, KeyType keyType, Class<?> fieldType) {
        stringBuilder.append(" "+key+" "+DataType.getDataType(fieldType)+" "+keyType.toString());
    }



}