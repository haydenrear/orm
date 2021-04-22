package com.hayden.orm.table.mapper;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public enum DataType {

    bool,
    TEXT,
    INT,
    FLOAT{
        @Override
        public String toString() {
            return "float(8)";
        }
    },
    DATE,
    TIME;

    String value;

    public static DataType getDataType(Class<?> classType) {
        if(isAssignableFrom(classType, Boolean.class, boolean.class))
           return bool;
        else if(isAssignableFrom(classType, Float.class, float.class))
            return FLOAT;
        else if(isAssignableFrom(classType, Date.class, java.util.Date.class))
            return DATE;
        else if(isAssignableFrom(LocalDateTime.class))
            return TIME;
        else if(isAssignableFrom(Integer.class, int.class))
            return INT;
        else {
            return TEXT;
        }
    }

    public static boolean isAssignableFrom(Class<?> clzz, Class<?> ... classTypes){
        return Arrays
                .stream(classTypes)
                .filter(clzz::isAssignableFrom)
                .collect(Collectors.toList()).size() != 0;

    }

}
