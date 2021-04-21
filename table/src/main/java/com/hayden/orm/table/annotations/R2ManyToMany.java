package com.hayden.orm.table.annotations;

import com.hayden.orm.table.mapper.Relationship;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface R2ManyToMany {
    public String value() default "hello";
    public Relationship direction() default Relationship.MANYTOMANYUNI;
}