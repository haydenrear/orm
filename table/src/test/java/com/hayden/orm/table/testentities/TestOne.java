package com.hayden.orm.table.testentities;

import com.hayden.orm.table.annotations.PrimaryKey;
import com.hayden.orm.table.annotations.TableName;

@TableName(tableName = "TestOne")
public class TestOne {

    @PrimaryKey(primaryKey = "id")
    int id;

}
