package com.hayden.orm.table.testentities;

import com.hayden.orm.table.annotations.PrimaryKey;
import com.hayden.orm.table.annotations.R2Column;
import com.hayden.orm.table.annotations.R2JoinType;
import com.hayden.orm.table.annotations.TableName;
import com.hayden.orm.table.key.Relationship;

@TableName(tableName = "TestOne")
public class TestOne {

    @PrimaryKey(primaryKey = "testOneId")
    int testOneId;

    @R2Column @R2JoinType(foreignKey = "testTwoId", relationship = Relationship.ONETOONEBI)
    TestOne testOne;

    @R2Column
    int another;

}
