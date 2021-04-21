package com.hayden.orm.table.mapper;

import com.hayden.orm.table.annotations.CollectionOrArray;
import com.hayden.orm.table.annotations.ManyToMany;
import com.hayden.orm.table.annotations.PrimitiveCollection;
import com.hayden.orm.table.annotations.R2Column;
import com.hayden.orm.table.annotations.R2JoinType;
import com.hayden.orm.table.annotations.TableName;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Stream;


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

    private Relationship getRelationship(Class<?> table, Field f) {
        return Arrays.stream(f.getDeclaredAnnotations())
                .filter(annotation -> !annotation.annotationType().isPrimitive())
                .map(Annotation::annotationType)
                .flatMap(clzz -> {
                    if(clzz.isAssignableFrom(R2JoinType.class))
                        return Stream.of(clzz.getAnnotation(R2JoinType.class).relationship());
                    else if(clzz.isAssignableFrom(PrimitiveCollection.class))
                        return Stream.of(Relationship.ONETOMANYUNI);
                    else
                        return Stream.empty();
                }).findFirst().orElse(Relationship.PRIMITIVE);
    }

    private boolean manyToMany(Class<?> table, Class<?> aClass) {
        return Arrays.stream(aClass.getFields()).map(Field::getType).anyMatch(fieldClass -> fieldClass.isAssignableFrom(table));
    }

    public boolean isTableExisting(Class<?> tableType){
        return tableType.isAnnotationPresent(TableName.class);
    }



}
