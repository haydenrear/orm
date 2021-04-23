package com.hayden.orm.table.key;

import com.hayden.orm.table.annotations.R2JoinType;
import lombok.AllArgsConstructor;
import lombok.Data;

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

    public static SqlKey NewSqlKey(String foreignKey, String primaryKey) {
        return null;
    }

    public static SqlKey NewSqlKey(R2JoinType annotation) {
        KeyType keyType = annotation.foreignKey().split("\\,").length > 1 ? KeyType.FOREIGN_COMPOSITE : KeyType.FOREIGN_SINGLE;
        return SqlKey.NewSqlKey(keyType, annotation.relationship(), annotation.foreignKey(), annotation.primaryKey());
    }
 }