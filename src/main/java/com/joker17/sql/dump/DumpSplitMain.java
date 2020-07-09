package com.joker17.sql.dump;


import com.beust.jcommander.JCommander;
import com.joker17.sql.dump.model.DumpParam;
import com.joker17.sql.dump.support.*;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DumpSplitMain {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public static void main(String[] args) throws IOException {
        DumpSpiltHelper.init();

        //args = StringUtils.split("-in ../db/ds_export-dump.sql -out ../db/ds_export-dump-result(-exclude:user).sql -table \"user\" -exclude", " ");


        if (args.length == 0) {
            args = new String[] {"--help"};
        }

        DumpParam dumpParam = new DumpParam();
        JCommander jCommander = JCommander.newBuilder()
                .addObject(dumpParam)
                .build();
        jCommander.parse(args);

        if (dumpParam.isHelp()) {
            jCommander.usage();
            return;
        }

        if (dumpParam.isDetail()) {
            System.out.println("当前配置:" + dumpParam);
        }

        LocalDateTime start = LocalDateTime.now();
        System.out.println("开始时间:" + start.format(DATE_TIME_FORMATTER));

        DumpSpiltHelper.doWork(dumpParam);

        LocalDateTime end = LocalDateTime.now();
        Duration duration = Duration.between(start, end);
        BigDecimal result = BigDecimal.valueOf(duration.toMillis()).divide(BigDecimal.valueOf(1000)).setScale(2, RoundingMode.HALF_UP);

        System.out.println("结束时间:" + end.format(DATE_TIME_FORMATTER));
        System.out.println("执行耗时:" + result + "s");
    }

}
