package com.hayden.orm.table.testentities;

import com.hayden.orm.table.annotations.PrimaryKey;
import com.hayden.orm.table.annotations.R2Column;
import com.hayden.orm.table.annotations.R2JoinType;
import com.hayden.orm.table.annotations.TableName;
import com.hayden.orm.table.key.Relationship;

@TableName(tableName = "TestTwo")
public class TestTwo {

    @PrimaryKey(primaryKey = "id")
    long id;

    @R2Column @R2JoinType(primaryKey = "id", foreignKey = "id", relationship = Relationship.ONETOONEBI)
    TestTwo testColumn;

}
