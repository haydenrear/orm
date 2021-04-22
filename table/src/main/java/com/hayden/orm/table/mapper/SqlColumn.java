package com.hayden.orm.table.mapper;

import com.hayden.orm.table.key.SqlKey;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SqlColumn{

    SqlKey sqlKey;
    Class<?> fieldType;
    DataType dataType;

    public SqlColumn(SqlKey sqlKey, Class<?> fieldType) {
        this.sqlKey = sqlKey;
        this.fieldType = fieldType;
    }

    public DataType getDataType(){
        return DataType.getDataType(fieldType);
    }

}
