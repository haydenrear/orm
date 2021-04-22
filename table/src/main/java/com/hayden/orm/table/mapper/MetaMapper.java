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

        String[] primaryKey = table.getAnnotation(PrimaryKey.class).primaryKey();

        if(primaryKey.length == 0){
            throw new MetaMappingException();
        }

        return Arrays.stream(table.getFields())
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


    private SqlColumn getSqlColumn(Class<?> table, String[] primaryKey, Field f) throws MetaMappingException {
        if(ClassUtils.isPrimitiveOrWrapper(f.getType())){
            return new SqlColumn(SqlKey.PrimitiveSqlKey(primaryKey), f.getType());
        }
        if (!isTableExisting(f.getType()) ) {
            throw new MetaMappingException();
        }
        else{
            String[] foreignKey;
            if (isTableExisting(f.getType()) && f.isAnnotationPresent(R2JoinType.class)) {
                R2JoinType r2JoinType = f.getAnnotation(R2JoinType.class);
                return new SqlColumn(SqlKey.NewSqlKey(r2JoinType.relationship(), r2JoinType.foreignKey(), r2JoinType.primaryKey()), f.getType());
            }
            else if ((foreignKey = biDirectional(table, f.getType())).length != 0) {
                return new SqlColumn(SqlKey.NewSqlKey(primaryKey, foreignKey), f.getType());
            } else {
                throw new MetaMappingException();
            }
        }
    }

    // get primary key from foreign key or return null if not bidirectional
    private String[] biDirectional(Class<?> table, Class<?> aClass) {
        return Arrays.stream(aClass.getFields()).map(Field::getType)
                .filter(fieldClass -> fieldClass.isAssignableFrom(table))
                .map(fieldClass -> fieldClass.getAnnotation(R2JoinType.class))
                .map(R2JoinType::primaryKey)
                .findFirst().orElse(new String[]{});
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


    public Tuple2<Optional<Class<?>>, String[]> getPrimaryKey(Class<?> entity) {
        String[] primaryKey = entity.getClass().getAnnotation(PrimaryKey.class).primaryKey();
        return Tuples.of(Arrays.stream(entity.getFields()).filter(field -> field.isAnnotationPresent(PrimaryKey.class))
                .findFirst().map(Field::getType), primaryKey);
    }
}
