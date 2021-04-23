package com.hayden.orm.table.mapper;

import com.hayden.orm.table.annotations.*;
import com.hayden.orm.table.exception.MetaMappingException;
import com.hayden.orm.table.key.SqlKey;
import org.springframework.util.ClassUtils;

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

        SqlColumn primaryKey = primaryKeyOptional.get();

        return Arrays.stream(table.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(R2Column.class))
                .flatMap(field -> {
                    try {
                        return Stream.of(getSqlColumn(table, primaryKey.getSqlKey().getPrimaryKey(), field));
                    } catch (MetaMappingException e) {
                        e.metaMappingException(field.getType());
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

    public Optional<SqlColumn> getPrimaryKey(Class<?> entity) {
        for (Field f : entity.getDeclaredFields()){
            if(f.isAnnotationPresent(PrimaryKey.class)){
                return Optional.of(new SqlColumn(SqlKey.PrimitiveSqlKey(f.getAnnotation(PrimaryKey.class).primaryKey()), entity));
            }
        }
        return Optional.empty();
    }
}
