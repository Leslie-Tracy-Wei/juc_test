package com.xx.juc;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class DateFormatterTest {
    public static void main(String[] args) {


        DateTimeFormatter d = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        TemporalAccessor parse = d.parse("1999-01-01");
        System.out.println(parse);
    }
}
