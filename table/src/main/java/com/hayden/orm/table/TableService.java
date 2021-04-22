package com.hayden.orm.table;

import com.hayden.orm.table.annotations.PrimaryKey;
import com.hayden.orm.table.annotations.TableName;
import com.hayden.orm.table.key.KeyType;
import com.hayden.orm.table.mapper.*;
import org.reflections.Reflections;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            Tuple2<Optional<Class<?>>, String[]> primaryKey = metaMapper.getPrimaryKey(entity);
            return Optional.of(
                    new SqlTable(metaMapper.getColumns(entity),
                        entity,
                        entity.getAnnotation(TableName.class).tableName(),
                        primaryKey.getT2(),
                        primaryKey.getT1().orElseGet(() -> String.class)
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
               table.getColumnList().forEach(sqlColumn -> {
                   writeKey(stringBuilder, table.getPrimaryKey(), KeyType.PRIMARY, sqlColumn.getFieldType());
                   stringBuilder.append(table.getPrimaryKey)
                   stringBuilder.append(sqlColumn.getFieldType().getSimpleName()+" "+sqlColumn.getFieldType());
               });
            });
        return stringBuilder.toString();
    }

    private void writeKey(StringBuilder stringBuilder, String[] primaryKey, KeyType keyType, Class<?> fieldType) {
        if(keyType == KeyType.PRIMARY){
            stringBuilder.append(primaryKey[0]+" "+DataType.getDataType(fieldType));
        }
    }

    private DataType getDataType(Class<?> fieldType){

    }


}