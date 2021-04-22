package com.hayden.orm.table.mapper;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class SqlTable {

    List<SqlColumn> columnList;
    Class<?> clzz;
    String tableName;
    String[] primaryKey;
    Class<?> primaryKeyType;

}
