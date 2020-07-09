package com.joker17.sql.dump.support;

import com.joker17.sql.dump.core.hanlder.WriteHandler;
import com.joker17.sql.dump.core.hanlder.WriteHandlerFactory;

public class TableStructureUtils {

    /**
     * 获取是否为table structure语句
     * @param text
     * @return
     */
    public static boolean isMatch(String text) {

        WriteHandler writeHandler = WriteHandlerHolder.getWriteHandler();
        if (writeHandler != null) {
            return writeHandler.isMatchTableStructure(text);
        }

        boolean[] results = new boolean[] {false};

        WriteHandlerFactory.doWithWriteHandler(new WriteHandlerFactory.LoopCallback() {

            @Override
            public void execute(WriteHandler writeHandler) {
                results[0] = writeHandler.isMatchTableStructure(text);
            }

            @Override
            public boolean isBreak(WriteHandler writeHandler) {
                return results[0];
            }
        });

        return results[0];
    }


    /**
     * 获取table name
     * @param text
     * @return
     */
    public static String getTable(String text) {

        WriteHandler writeHandler = WriteHandlerHolder.getWriteHandler();
        if (writeHandler != null) {
            return writeHandler.getTableName(text);
        }

        String[] results = new String[] {null};

        WriteHandlerFactory.doWithWriteHandler(new WriteHandlerFactory.LoopCallback() {

            @Override
            public void execute(WriteHandler writeHandler) {
                results[0] = writeHandler.getTableName(text);
            }

            @Override
            public boolean isBreak(WriteHandler writeHandler) {
                return results[0] != null;
            }
        });

        return results[0];
    }

}
