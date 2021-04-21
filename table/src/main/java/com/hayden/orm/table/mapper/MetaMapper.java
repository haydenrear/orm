package com.hayden.orm.table.mapper;

import com.hayden.orm.table.annotations.ManyToMany;
import com.hayden.orm.table.annotations.R2Column;
import com.hayden.orm.table.annotations.R2ManyToMany;
import com.hayden.orm.table.annotations.TableName;

import java.lang.reflect.Field;
import java.util.*;


public class MetaMapper{

    public Map<Relationship,Class<?>> getColumns(Class<?> table) throws MetaMappingException {

        Map<Relationship,Class<?>> primitives = new HashMap<>();
        Map<Relationship,Class<?>> nonPrimitives = new HashMap<>();

        for(Field f : table.getFields()) {
            if(f.isAnnotationPresent(R2Column.class)){
                if(f.getType().isPrimitive()){
                    primitives.put(Relationship.PRIMITIVE, f.getType());
                }
                if (!isTableExisting(f.getType()) ) {
                    if (manyToMany(table, f.getType())) {
                        createTable(f.getType());
                        nonPrimitives.put(getRelationship(table,f), f.getType());
                    } else {
                        throw new MetaMappingException();
                    }
                }
                else if ()
            }
        }

    }

    // returning primitive here will catch Integer and Float
    private Relationship getRelationship(Class<?> table, Field f) {
        return Arrays.stream(f.getDeclaredAnnotations())
                .map(annotation -> {
                    if(annotation.getClass().isAssignableFrom(R2ManyToMany.class)){
                        return f.getAnnotation(R2ManyToMany.class).direction();
                    }
                    else {
                        return Relationship.ONETOONEUNI;
                    }
                }).findFirst().orElse(Relationship.PRIMITIVE);
    }

    private boolean manyToMany(Class<?> table, Class<?> aClass) {
        return Arrays.stream(aClass.getFields()).map(Field::getType).anyMatch(fieldClass -> fieldClass.isAssignableFrom(table));
    }

    public boolean isTableExisting(Class<?> tableType){
        return tableType.isAnnotationPresent(TableName.class);
    }



}
