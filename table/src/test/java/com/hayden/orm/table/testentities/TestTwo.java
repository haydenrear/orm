package com.hayden.orm.table.testentities;

import com.hayden.orm.table.annotations.PrimaryKey;
import com.hayden.orm.table.annotations.R2Column;
import com.hayden.orm.table.annotations.TableName;

@TableName(tableName = "TestTwo")
public class TestTwo {

    @PrimaryKey(primaryKey = "id")
    long id;

    @R2Column
    int testColumn;

}
