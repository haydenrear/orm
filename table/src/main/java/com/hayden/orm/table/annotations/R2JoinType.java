package com.hayden.orm.table.annotations;

import com.hayden.orm.table.key.Relationship;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface R2JoinType {
    public String[] foreignKey();
    public String[] primaryKey();
    public Relationship relationship();
}