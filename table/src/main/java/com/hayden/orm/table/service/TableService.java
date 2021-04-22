package com.hayden.orm.table.service;

import com.hayden.orm.table.annotations.TableName;
import com.hayden.orm.table.key.KeyType;
import com.hayden.orm.table.mapper.*;
import org.reflections8.Reflections;
import org.springframework.util.ClassUtils;
import reactor.util.function.Tuple2;

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
                return Optional.empty();
            return Optional.of(
                    new SqlTable(metaMapper.getColumns(entity),
                        entity,
                        entity.getAnnotation(TableName.class).tableName(),
                        primaryKey.get().getT2(),
                        primaryKey.get().getT1()
                    )
            );
        } catch (MetaMappingException e) {
            e.notAbleToGetColumnsFor(entity);
            e.lackOfPrimaryKey(entity);
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
                       writeKey(stringBuilder, sqlColumn.getSqlKey().getPrimaryKey(), KeyType.FOREIGN, sqlColumn.getFieldType());
                   }
                   stringBuilder.append(",\n");
               });
            });
        return stringBuilder.toString();
    }

    private void writeKey(StringBuilder stringBuilder, String primaryKey, KeyType keyType, Class<?> fieldType) {
        stringBuilder.append(" "+primaryKey+" "+DataType.getDataType(fieldType)+" "+keyType.toString());
    }



}