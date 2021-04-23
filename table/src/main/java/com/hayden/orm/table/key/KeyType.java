package com.hayden.orm.table.key;

public enum KeyType {
    PRIMARY_SINGLE{
        @Override
        public String toString() {
            return "PRIMARY KEY";
        }
    },
    PRIMARY_COMPOSITE{
        @Override
        public String toString() {
            return "PRIMARY KEY";
        }
    },
    PRIMARY{
        @Override
        public String toString() {
            return "PRIMARY KEY";
        }
    },
    FOREIGN_COMPOSITE{
        @Override
        public String toString() {
            return "FOREIGN KEY";
        }
    },
    FOREIGN_SINGLE{
        @Override
        public String toString() {
            return "FOREIGN KEY";
        }
    },
    FOREIGN{
        @Override
        public String toString() {
            return "FOREIGN KEY";
        }
    },
    PRIMITIVE{
        @Override
        public String toString() {
            return "";
        }
    };
}