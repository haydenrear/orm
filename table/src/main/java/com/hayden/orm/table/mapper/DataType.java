package com.hayden.orm.table.mapper;

import org.springframework.util.ClassUtils;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public enum DataType {

    bool,
    TEXT,
    INT{
        @Override
        public String toString() {
            return "integer";
        }
    },
    DATE,
    DOUBLE{
        @Override
        public String toString() {
            return "double precision";
        }
    },
    TIME,
    COMPLEX;

    String value;

    public static DataType getDataType(Class<?> classType) {
        if(!ClassUtils.isPrimitiveOrWrapper(classType))
            return COMPLEX;
        if(isAssignableFrom(classType, Boolean.class, boolean.class))
           return bool;
        else if(isAssignableFrom(classType, Date.class, java.util.Date.class))
            return DATE;
        else if(isAssignableFrom(classType, LocalDateTime.class))
            return TIME;
        else if(isAssignableFrom(classType, Integer.class, int.class, long.class, Long.class))
            return INT;
        else if(isAssignableFrom(classType, Double.class, double.class, Float.class, float.class))
            return DOUBLE;
        else
            return TEXT;
    }

    public static boolean isAssignableFrom(Class<?> clzz, Class<?> ... classTypes){
        return Arrays
                .stream(classTypes)
                .filter(clzz::isAssignableFrom)
                .collect(Collectors.toList()).size() != 0;
    }

}
