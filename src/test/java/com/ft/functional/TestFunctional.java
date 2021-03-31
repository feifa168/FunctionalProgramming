package com.ft.functional;


import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.function.Function;

public class TestFunctional {
    public static Logger LOGGER = LoggerFactory.getLogger(TestFunctional.class);

    public static class TestClass<T, U> {
        private T field1;
        private U field2;
        public TestClass(T field1, U field2) {
            this.field1 = field1;
            this.field2 = field2;
        }

        @Override
        public String toString() {
            return "field1:" + this.field1 + ", field2:" + this.field2;
        }
    }

    public <T> void funcConsumer(Consumer<T> c, T t) {
        c.accept(t);
    }

    @Test
    public void testConsumer() {
        Consumer<Integer> c1 = new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {
                StringJoiner sj = new StringJoiner(" ", "[", "]");
                sj.add(String.valueOf(integer));
                LOGGER.info(sj.toString());
            }
        };
        Consumer<String> c2 = s -> LOGGER.info(s) ;
        Consumer c3 = System.out::println;
        Consumer<Object> c4 = System.out::println;
        Consumer<String> c5 = s -> LOGGER.info(s.toUpperCase());

        LOGGER.info(new TestClass("tom", 20).toString());   // field1:tom, field2:20
        LOGGER.info(new TestClass(10, "ttt").toString());   // field1:10, field2:ttt

        c1.accept(5);   // [5]
        c1.andThen(c3)          // [9]
                .andThen(c4)    // 9
                .accept(9);   // 9

        funcConsumer(c1, 5);                    // [5]
        funcConsumer(c2, "this is for c2");     // this is for c2
        funcConsumer(c3, "print this message"); // print this message
        funcConsumer(c4, "uUzZ");               // uUzZ
        funcConsumer(c5, "ttmmmzz");            // TTMMMZZ
    }

    public <T, R> R funcFunction(Function<T, R> f, T t) {
        return f.apply(t);
    }

    @Test
    public void testFunction() {
        Function<String, String> f1 = new Function<String, String>() {
            @Override
            public String apply(String s) {
                return s + "_123";
            }
        };
        Function<String, String> f2 = (s) -> s.toUpperCase() + "_upper";
        Function<String, String> f3 = s -> s + "!!";

        String msg = "_abc";
        LOGGER.info(f1.compose(f2).compose(f3).apply(msg));  // _ABC!!_upper_123
        LOGGER.info(f1.andThen(f2).andThen(f3).apply(msg));  // _ABC_123_upper!!
        LOGGER.info(f1.compose(f2).andThen(f3).apply(msg));  // _ABC_upper_123!!
        LOGGER.info(f1.compose(f3).andThen(f2).apply(msg));  // _ABC!!_123_upper

        // string to integer, result is 123
        LOGGER.info("string to integer, result is " + String.valueOf(funcFunction(new Function<String, Integer>() {
            @Override
            public Integer apply(String s) {
                return Integer.valueOf(s);
            }
        }, "123")));

        // integer to string, result is 60
        LOGGER.info(funcFunction((Integer s) -> "integer to string, result is " + String.valueOf(s), 60));

        // true
        LOGGER.info(String.valueOf(funcFunction((TestClass<String, Boolean> tc) -> tc.field1.equals("TOM") ? true : false, new TestClass("TOM", 20) )));

        // false
        LOGGER.info(String.valueOf(funcFunction((TestClass<String, Boolean> tc) -> tc.field1.equals("TOM") ? true : false, new TestClass("BRUCE", 20) )));
    }
}
