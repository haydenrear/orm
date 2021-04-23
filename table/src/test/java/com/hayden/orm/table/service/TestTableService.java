package com.hayden.orm.table.service;

import com.hayden.orm.table.exception.LackOfPrimaryKey;
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
            try {
                assertThat(column.dataType()).isEqualTo(DataType.INT);
            } catch (LackOfPrimaryKey lackOfPrimaryKey) {
                lackOfPrimaryKey.printStackTrace();
            }
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
                        try {
                            System.out.println((column.dataType()));
                        } catch (LackOfPrimaryKey lackOfPrimaryKey) {
                            lackOfPrimaryKey.printStackTrace();
                        }
                        System.out.println(column.getSqlKey().getForeignKey());
                    });
                    return table;
                });
    }

    @Test
    public void testCreationDatabase(){
        tableService.getTables();
        System.out.println(tableService.createDatabaseCreationScript());
    }


}
