package com.joker17.sql.dump.support;

import com.joker17.sql.dump.core.hanlder.WriteHandler;

import java.io.Writer;

public class WriteHandlerHolder {

    private static final ThreadLocal<WriteHandler> writeHandlerHolder = new ThreadLocal<>();

    private static final ThreadLocal<Writer> writerHolder = new ThreadLocal<>();

    public static void reset() {
        writeHandlerHolder.remove();
        writerHolder.remove();
    }

    public static void setWriteHandler(WriteHandler writeHandler) {
        if (writeHandler == null) {
            writeHandlerHolder.remove();
        } else {
            writeHandlerHolder.set(writeHandler);
        }
    }

    public static WriteHandler getWriteHandler() {
        return writeHandlerHolder.get();
    }

    public static void setWriter(Writer writer) {
        if (writer == null) {
            writerHolder.remove();
        } else {
            writerHolder.set(writer);
        }
    }

    public static Writer getWriter() {
        return writerHolder.get();
    }

}
