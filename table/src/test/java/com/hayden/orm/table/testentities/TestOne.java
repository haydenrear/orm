package com.hayden.orm.table.testentities;

import com.hayden.orm.table.annotations.PrimaryKey;
import com.hayden.orm.table.annotations.R2Column;
import com.hayden.orm.table.annotations.R2JoinType;
import com.hayden.orm.table.annotations.TableName;
import com.hayden.orm.table.key.Relationship;

@TableName(tableName = "TestOne")
public class TestOne {

    @PrimaryKey(primaryKey = "id")
    int id;

//    @R2Column @R2JoinType(primaryKey = "id", foreignKey = "id", relationship = Relationship.ONETOONEBI)
    TestOne testOne;

}
