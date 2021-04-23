package com.hayden.orm.table.exception;

import lombok.AllArgsConstructor;


@AllArgsConstructor
public class NotSqlTable extends MetaMappingException {

    String error;

    public void metaMappingException(Class<?> field){
        System.out.println(field.getClass().getSimpleName()+" is not annotated with TableName");
        if(error != null)
            System.out.println(error);
    }

    public NotSqlTable() {
    }
}
