package com.hayden.orm.table.exception;

public class NotAbleToGetColumnsFor extends MetaMappingException {
    public void metaMappingException(Class<?> entity) {
        System.out.println("exception getting columns for "+entity.getSimpleName());
    }
}
