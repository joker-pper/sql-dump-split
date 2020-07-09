package com.joker17.sql.dump;

import com.joker17.sql.dump.model.DumpParam;
import com.joker17.sql.dump.support.DumpSpiltHelper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class DumpSplitMainTest {

    @Before
    public void before() {
        DumpSpiltHelper.init();
    }

    @Test
    public void test() {
        String userDir = System.getProperty("user.dir");

        String[] ins = new String[] {
                userDir + "/db/ds_export-dump.sql",
                userDir + "/db/ds_export-navicate.sql",
        };

        MultipleThreadRunner threadRunner = new MultipleThreadRunner();

        int length = ins.length;

        for (int i = 0; i < length; i++) {
            int finalIndex = i;

            threadRunner.addTask(() -> {

                DumpParam dumpParam = new DumpParam();

                dumpParam.setCharset("UTF-8 GBK");

                dumpParam.setInPath(ins[finalIndex]);
                dumpParam.setOutPath(ins[finalIndex].replace(".sql", "-new.sql"));

                dumpParam.setTable("user");

                dumpParam.setExcludeMode(false);

                try {
                    DumpSpiltHelper.doWork(dumpParam);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        threadRunner.doTask();
    }

}