package com.joker17.sql.dump.support;

import com.joker17.sql.dump.core.hanlder.WriteHandler;

public final class WriteHandlerHolder {

    private static final ThreadLocal<WriteHandler> writeHandlerHolder = new ThreadLocal<>();


    /**
     * Reset the WriteHandler for the current thread.
     */
    public static void reset() {
        writeHandlerHolder.remove();
    }

    /**
     * 设置当前线程的WriteHandler
     *
     * @param writeHandler
     */
    public static void setWriteHandler(WriteHandler writeHandler) {
        if (writeHandler == null) {
            reset();
        } else {
            writeHandlerHolder.set(writeHandler);
        }
    }

    /**
     * 获取当前线程的WriteHandler
     *
     * @return
     */
    public static WriteHandler getWriteHandler() {
        return writeHandlerHolder.get();
    }

}
