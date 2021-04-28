package com.hayden.orm.table.mapper;

import com.hayden.orm.table.annotations.PrimaryKey;
import com.hayden.orm.table.annotations.R2Column;
import com.hayden.orm.table.exception.LackOfPrimaryKey;
import com.hayden.orm.table.key.KeyType;
import com.hayden.orm.table.key.SqlKey;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.Arrays;

@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
public class SqlColumn{

    SqlKey sqlKey;
    Class<?> fieldType;
    DataType dataType;
    DataType foreignKeyDataType;

    public SqlColumn(SqlKey sqlKey, Class<?> fieldType) {
        this.sqlKey = sqlKey;
        this.fieldType = fieldType;
    }

    public DataType dataType() throws LackOfPrimaryKey {
        if(sqlKey.getKeyType().equals(KeyType.PRIMITIVE)){
            return DataType.getDataType(fieldType);
        }
        return Arrays
                .stream(fieldType.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(PrimaryKey.class))
                .map(Field::getType)
                .map(DataType::getDataType)
                .filter(dataType1 -> dataType1!=DataType.COMPLEX)
                .findFirst()
                .orElseThrow(() -> new LackOfPrimaryKey("primary key may only be of primitive type"));
    }

    public SqlKey getSqlKey() {
        return sqlKey;
    }

    public void setSqlKey(SqlKey sqlKey) {
        this.sqlKey = sqlKey;
    }

    public Class<?> getFieldType() {
        return fieldType;
    }

    public void setFieldType(Class<?> fieldType) {
        this.fieldType = fieldType;
    }

    public DataType getForeignKeyDataType() {
        return foreignKeyDataType;
    }

    public void setForeignKeyDataType(DataType foreignKeyDataType) {
        this.foreignKeyDataType = foreignKeyDataType;
    }
}
