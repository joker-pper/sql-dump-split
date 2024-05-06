package com.joker17.sql.dump.support;

import java.io.Writer;

public final class WriterHolder {

    private static final ThreadLocal<Writer> writerHolder = new ThreadLocal<>();

    private WriterHolder() {

    }

    /**
     * Reset the Writer for the current thread.
     */
    public static void reset() {
        writerHolder.remove();
    }

    /**
     * 设置当前线程的Writer
     *
     * @param writer
     */
    public static void setWriter(Writer writer) {
        if (writer == null) {
            writerHolder.remove();
        } else {
            writerHolder.set(writer);
        }
    }

    /**
     * 获取当前线程的Writer
     *
     * @return
     */
    public static Writer getWriter() {
        return writerHolder.get();
    }

}
