package com.joker17.sql.dump.core.hanlder;

import com.joker17.sql.dump.core.hanlder.impl.BaseWriteHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WriteHandlerFactory {

    private static final List<WriteHandler> WRITE_HANDLER_CACHE = new ArrayList<>(16);

    private WriteHandlerFactory() {

    }

     /**
     * 注册writeHandler
     *
     * @param writeHandler
     */
    public static void registerWriteHandler(WriteHandler writeHandler) {
        WRITE_HANDLER_CACHE.add(writeHandler);
    }

    /**
     * 注册完成
     */
    public static void registerWriteHandlerComplete() {
        Collections.sort(WRITE_HANDLER_CACHE);
    }

    public static WriteHandler getDefaultWriteHandler() {
        return WRITE_HANDLER_CACHE.isEmpty() ? new BaseWriteHandler() : WRITE_HANDLER_CACHE.get(WRITE_HANDLER_CACHE.size() - 1);
    }

    public static WriteHandler getMatchWriteHandler(List<String> startContents) {
        for (WriteHandler writeHandler : WRITE_HANDLER_CACHE) {
            if (writeHandler.match(startContents)) {
                return writeHandler;
            }
        }
        return null;
    }

    /**
     * 遍历所有的WriteHandler进行执行callback逻辑
     *
     * @param callback
     */
    public static void doWithWriteHandler(LoopCallback callback) {
        if (callback == null) {
            return;
        }
        for (WriteHandler writeHandler : WRITE_HANDLER_CACHE) {
            callback.execute(writeHandler);
            if (callback.isBreak(writeHandler)) {
                break;
            }
        }
    }

    public interface LoopCallback {
        /**
         * 执行
         *
         * @param writeHandler
         */
        void execute(WriteHandler writeHandler);

        /**
         * 是否停止
         *
         * @param writeHandler
         * @return
         */
        boolean isBreak(WriteHandler writeHandler);
    }
}
