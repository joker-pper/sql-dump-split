package com.joker17.sql.dump.core.hanlder;

import com.joker17.sql.dump.model.TableStructureModel;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

public interface WriteHandler extends Comparable<WriteHandler> {

    default boolean match(List<String> contents) {
        return false;
    }

    TableStructureModel getTableStructureModel();

    boolean isMatchTableStructure(String text);

    /**
     * 获取table
     *
     * @param text
     * @return
     */
    String getTableName(String text);

    /**
     * 是否为要匹配的table
     *
     * @param tables
     * @param table
     * @param exclude 是否为排除模式
     * @return
     */
    boolean isMatchTable(Collection<String> tables, String table, boolean exclude);

    /**
     * 是否可能的table标签
     *
     * @param text
     * @return
     */
    boolean isPossibleTableTag(String text);

    /**
     * 是否为结束标记
     *
     * @param text
     * @return
     */
    boolean isEndTag(String text);

    /**
     * 初始化当前的writer
     *
     * @param outPath
     * @param charset
     * @throws IOException
     */
    void initCurrentWriter(String outPath, Charset charset) throws IOException;

    /**
     * 获取writer
     *
     * @param outFile
     * @param charset
     * @return
     * @throws FileNotFoundException
     */
    Writer getWriter(File outFile, Charset charset) throws FileNotFoundException;

    default void writeTableStructure(List<String> contents, Charset inCharset, Charset outCharset) {
        write(contents, inCharset, outCharset);
    }

    void write(List<String> contents, Charset inCharset, Charset outCharset);

    void write(String content, Charset inCharset, Charset outCharset);

    void writeFinish();


    String getType();

    /**
     * 排序值,值越大越靠前
     *
     * @return
     */
    int getOrder();
}
