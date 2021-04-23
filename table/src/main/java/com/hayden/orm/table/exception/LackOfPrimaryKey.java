package com.hayden.orm.table.exception;

public class LackOfPrimaryKey extends MetaMappingException {
        public void metaMappingException(Class<?> entity) {
                System.out.println("no primary key for " + entity.getSimpleName());
        }
}
