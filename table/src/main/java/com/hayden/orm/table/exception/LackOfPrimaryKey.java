package com.hayden.orm.table.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LackOfPrimaryKey extends MetaMappingException {
        public void metaMappingException(Class<?> entity) {
                System.out.println("no primary key for " + entity.getSimpleName());
                System.out.println(exception);
        }
        String exception;

}
