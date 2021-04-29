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
                .filter(field -> field.isAnnotationPresent(R2Column.class) && !field.isAnnotationPresent(PrimaryKey.class))
                .flatMap(field -> {
                    try {
                        return Stream.of(getSqlColumn(primaryKey.getSqlKey().getPrimaryKey(), field));
                    } catch (MetaMappingException e) {
                        e.metaMappingException(field.getType());
                    }
                    return Stream.empty();
                }).collect(Collectors.toList());
    }


    private SqlColumn getSqlColumn(String primaryKey, Field f) throws MetaMappingException {
        if((ClassUtils.isPrimitiveOrWrapper(f.getType()) || f.getType().equals(String.class))){
            return new SqlColumn(SqlKey.PrimitiveSqlKey(primaryKey), f.getType());
        }
        else{
            if (isTableExisting(f.getType()) && f.isAnnotationPresent(R2JoinType.class)) {
                R2JoinType r2JoinType = f.getAnnotation(R2JoinType.class);
                return new SqlColumn(SqlKey.NewSqlKey(r2JoinType.relationship(), r2JoinType.foreignKey(), primaryKey), f.getType());
            }
            else if (f.isAnnotationPresent(PrimitiveCollection.class)){
                return new SqlColumn(SqlKey.PrimitiveCollection(f.getAnnotation(PrimitiveCollection.class), primaryKey), f.getType());
            }
            else {
                throw new MetaMappingException();
            }
        }
    }

    public boolean isTableExisting(Class<?> tableType){
        return tableType.isAnnotationPresent(TableName.class);
    }

    public Optional<SqlColumn> getPrimaryKey(Class<?> entity) {
        return Arrays.stream(entity.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(PrimaryKey.class))
                .map(f -> new SqlColumn(SqlKey.TablePrimarySqlKey(f.getAnnotation(PrimaryKey.class).primaryKey()), f.getType()))
                .findFirst();
    }
}
