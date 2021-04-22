package com.hayden.orm.table.key;

public enum KeyType {
    PRIMARY_SINGLE,
    PRIMARY_COMPOSITE,
    PRIMARY{
        @Override
        public String toString() {
            return "PRIMARY KEY";
        }
    },
    FOREIGN_COMPOSITE,
    FOREIGN_SINGLE,
    FOREIGN{
        @Override
        public String toString() {
            return "FOREIGN KEY";
        }
    },
    PRIMITIVE;
}
