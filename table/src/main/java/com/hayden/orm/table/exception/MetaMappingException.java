package com.hayden.orm.table.exception;

import java.lang.reflect.Field;

public class MetaMappingException extends Exception {
    public void metaMappingException(Class<?> entity){
        System.out.println("metamapping exception");
    }

}
