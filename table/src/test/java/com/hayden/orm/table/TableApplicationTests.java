package com.hayden.orm.table;

import com.hayden.orm.table.mapper.DataType;
import com.hayden.orm.table.mapper.MetaMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TableApplicationTests {

    public int field;

    @Test
    void contextLoads() {
    }

    @Test
    void testPrimitives(){
        Class<Integer> intClass = Integer.class;
        assertThat(intClass.isPrimitive()).isTrue();
    }
}
