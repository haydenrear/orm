package com.hayden.orm.table.mapper;

import com.hayden.orm.table.annotations.*;
import com.hayden.orm.table.key.SqlKey;
import org.springframework.util.ClassUtils;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MetaMapper {


    private void createTable(Class<?> type) {
        //Todo: add this
    }


    public List<SqlColumn> getColumns(Class<?> table) throws MetaMappingException {

        var primaryKeyOptional = getPrimaryKey(table);

        if(primaryKeyOptional.isEmpty()){
            throw new MetaMappingException();
        }

        String primaryKey = primaryKeyOptional.get().getT2();

        return Arrays.stream(table.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(R2Column.class))
                .flatMap(field -> {
                    try {
                        return Stream.of(getSqlColumn(table, primaryKey, field));
                    } catch (MetaMappingException e) {
                        e.notSqlTable(field);
                    }
                    return Stream.empty();
                }).collect(Collectors.toList());
    }


    private SqlColumn getSqlColumn(Class<?> table, String primaryKey, Field f) throws MetaMappingException {
        if(ClassUtils.isPrimitiveOrWrapper(f.getType())){
            return new SqlColumn(SqlKey.PrimitiveSqlKey(primaryKey), f.getType());
        }
        if (!isTableExisting(f.getType()) ) {
            throw new MetaMappingException();
        }
        else{
            String foreignKey;
            if (isTableExisting(f.getType()) && f.isAnnotationPresent(R2JoinType.class)) {
                R2JoinType r2JoinType = f.getAnnotation(R2JoinType.class);
                return new SqlColumn(SqlKey.NewSqlKey(r2JoinType.relationship(), r2JoinType.foreignKey(), r2JoinType.primaryKey()), f.getType());
            }
            else if ((foreignKey = biDirectional(table, f.getType())).length() != 0) {
                return new SqlColumn(SqlKey.NewSqlKey(primaryKey, foreignKey), f.getType());
            } else {
                throw new MetaMappingException();
            }
        }
    }

    // get primary key from foreign key or return null if not bidirectional
    private String biDirectional(Class<?> table, Class<?> aClass) {
        return Arrays.stream(aClass.getDeclaredFields()).map(Field::getType)
                .filter(fieldClass -> fieldClass.isAssignableFrom(table))
                .map(fieldClass -> fieldClass.getAnnotation(R2JoinType.class))
                .map(R2JoinType::primaryKey)
                .findFirst().orElse("");
    }

    public boolean isTableExisting(Class<?> tableType){
        return tableType.isAnnotationPresent(TableName.class);
    }

    public DataType dataTypeFromField(Field field){
        if(field.getType().isAssignableFrom(Integer.class) || field.getType().isAssignableFrom(int.class)){
            return DataType.INT;
        }
        return DataType.DATE;
    }


    public Optional<Tuple2<Class<?>, String>> getPrimaryKey(Class<?> entity) {
        for (Field f : entity.getDeclaredFields()){
            if(f.isAnnotationPresent(PrimaryKey.class)){
                return Optional.of(Tuples.of(f.getType(), f.getAnnotation(PrimaryKey.class).primaryKey()));
            }
        }
        return Optional.empty();
    }
}
