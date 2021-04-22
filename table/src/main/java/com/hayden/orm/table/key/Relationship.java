package com.hayden.orm.table.key;

import com.hayden.orm.table.mapper.Direction;

public enum Relationship {

    ONETOONEUNI(Direction.UNI),
    MANYTOMANYUNI(Direction.UNI),
    ONETOONEBI(Direction.BI),
    MANYTOMANYBI(Direction.BI),
    ONETOMANYUNI(Direction.UNI),
    ONETOMANYBI(Direction.BI),
    PRIMITIVE(Direction.NONE);

    public Direction direction;

    private Relationship(Direction direction){
        this.direction = direction;
    }

}
