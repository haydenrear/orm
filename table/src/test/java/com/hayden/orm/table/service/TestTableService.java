package com.hayden.orm.table.service;

import com.hayden.orm.table.mapper.SqlTable;
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
            assertThat(column.getSqlKey()).isEqualTo("id");
            assertThat(column.getDataType()).isEqualTo("integer");
            assertThat(column.getFieldType()).isEqualTo("int");
        });
    }

}
