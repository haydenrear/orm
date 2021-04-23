package com.hayden.orm.table.service;

import com.hayden.orm.table.key.KeyType;
import com.hayden.orm.table.mapper.DataType;
import com.hayden.orm.table.mapper.SqlTable;
import com.hayden.orm.table.testentities.TestOne;
import com.hayden.orm.table.testentities.TestTwo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
public class TestTableService {

    TableService tableService = new TableService();

    @Test
    public void testTableService(){
        assertThat(tableService.findEntities().size()).isEqualTo(2);
        Optional<SqlTable> sqlTable = tableService.getSqlTable(TestTwo.class);
        sqlTable.get().getColumnList().forEach(column -> {
            assertThat(column.getSqlKey().getPrimaryKey()).isEqualTo("id");
            assertThat(column.getSqlKey().getKeyType()).isEqualTo(KeyType.PRIMITIVE);
            assertThat(column.getDataType()).isEqualTo(DataType.INT);
            assertThat(column.getFieldType()).isEqualTo(int.class);
        });
    }

    @Test
    public void testForeignKey(){
        tableService.getSqlTable(TestOne.class)
                .map(table-> {
                    table.getColumnList().forEach(column -> {
                        System.out.println((column.getSqlKey().getPrimaryKey()));
//                        System.out.println((column.getSqlKey().getKeyType()));
//                        System.out.println((column.getDataType()));
//                        System.out.println((column.getFieldType()));
                        System.out.println((column.getDataType()));
                        System.out.println(column.getSqlKey().getForeignKey());
                    });
                    return table;
                });
    }


}
