package com.hayden.orm.table.testentities;

import com.hayden.orm.table.annotations.PrimaryKey;
import com.hayden.orm.table.annotations.R2Column;
import com.hayden.orm.table.annotations.R2JoinType;
import com.hayden.orm.table.annotations.TableName;
import com.hayden.orm.table.key.Relationship;

@TableName(tableName = "TestTwo")
public class TestTwo {

    @PrimaryKey(primaryKey = "testTwoId")
    long testTwoId;

    @R2Column @R2JoinType(foreignKey = "testOneIdOne", relationship = Relationship.ONETOONEBI)
    TestOne testOne;

}