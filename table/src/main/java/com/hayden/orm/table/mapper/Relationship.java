package com.hayden.orm.table.mapper;

public enum Relationship {
    ONETOONEUNI(Direction.UNI),
    MANYTOMANYUNI(Direction.UNI),
    ONETOONEBI(Direction.UNI),
    MANYTOMANYBI(Direction.UNI),
    ONETOMANYUNI(Direction.UNI),
    ONETOMANYBI(Direction.BI),
    PRIMITIVE(Direction.NONE);

    public Direction direction;

    Relationship(Direction direction){
        this.direction = direction;
    }
}
