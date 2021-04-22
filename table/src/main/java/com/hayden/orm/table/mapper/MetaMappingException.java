package com.hayden.orm.table.mapper;

import java.lang.reflect.Field;

public class MetaMappingException extends Exception {

    public void lackOfPrimaryKey(Class<?> entity){
        System.out.println("no primary key for " + entity.getSimpleName());
    }

    public void notSqlTable(Field field){
        System.out.println(field.getType()+" is not annotated with TableName");
    }

    public void notAbleToGetColumnsFor(Class<?> entity) {
        System.out.println("exception getting columns for "+entity.getSimpleName());
    }
}
