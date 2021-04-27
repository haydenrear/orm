package com.hayden.orm.table.key;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.annotation.Annotation;

@AllArgsConstructor
@Data
public class SqlKey {

    String primaryKey;
    String foreignKey;
    KeyType keyType;
    Relationship relationship;

    public static SqlKey NewSqlKey(KeyType keyType, Relationship relationship, String foreign, String primary){
        return new SqlKey(primary, foreign, keyType, relationship);
    }

    public static SqlKey NewSqlKey(Relationship relationship, String foreign, String primary){
        KeyType keyType = foreign.split("\\,").length >= 1 ? KeyType.FOREIGN_COMPOSITE: KeyType.FOREIGN_SINGLE;
        return new SqlKey(primary, foreign, keyType, relationship);
    }

    public static SqlKey PrimitiveSqlKey(String primaryKey){
        return new SqlKey(primaryKey, null, KeyType.PRIMITIVE, Relationship.PRIMITIVE);
    }

    public static SqlKey PrimitiveCollection(Annotation annotation, String primaryKey){
        return new SqlKey(primaryKey, null, KeyType.PRIMITIVE, Relationship.ONETOMANYUNI);
    }

    public static SqlKey TablePrimarySqlKey(String primaryKey) {
        return new SqlKey(primaryKey, null, KeyType.PRIMARY, Relationship.PRIMITIVE);
    }
}