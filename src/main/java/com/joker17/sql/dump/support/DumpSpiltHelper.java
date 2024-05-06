package com.joker17.sql.dump.support;


import com.joker17.sql.dump.core.hanlder.WriteHandler;
import com.joker17.sql.dump.core.hanlder.WriteHandlerFactory;
import com.joker17.sql.dump.core.hanlder.impl.BaseWriteHandler;
import com.joker17.sql.dump.core.hanlder.impl.MysqlDumpWriteHandler;
import com.joker17.sql.dump.core.hanlder.impl.NavicatDumpWriteHandler;
import com.joker17.sql.dump.model.DumpParam;
import com.joker17.sql.dump.model.TableStructureModel;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class DumpSpiltHelper {

    /**
     * 是否初始化过
     */
    private static volatile AtomicBoolean INITIALIZED = new AtomicBoolean(false);

    private DumpSpiltHelper() {

    }


    /**
     * 初始化writeHandlers(仅支持调用一次)
     *
     * @param writeHandlers
     */
    public static void init(WriteHandler... writeHandlers) {
        if (!INITIALIZED.compareAndSet(false, true)) {
            throw new RuntimeException("DumpSpiltHelper has initialized!");
        }

        WriteHandlerFactory.registerWriteHandler(new BaseWriteHandler());
        WriteHandlerFactory.registerWriteHandler(new MysqlDumpWriteHandler());
        WriteHandlerFactory.registerWriteHandler(new NavicatDumpWriteHandler());

        if (writeHandlers != null) {
            for (WriteHandler writeHandler : writeHandlers) {
                WriteHandlerFactory.registerWriteHandler(writeHandler);
            }
        }
        WriteHandlerFactory.registerWriteHandlerComplete();
    }


    public static void doWork(DumpParam dumpParam) throws IOException {
        Objects.requireNonNull(dumpParam, "param must be not null");

        String inPath = dumpParam.getInPath();
        String outPath = dumpParam.getOutPath();

        Objects.requireNonNull(inPath, "in path must be not null");
        Objects.requireNonNull(outPath, "out path must be not null");

        File inFile = new File(inPath);
        if (!inFile.isFile() || !inFile.exists()) {
            throw new FileNotFoundException("Unable to found " + inPath);
        }

        String charset = StringUtils.defaultIfEmpty(dumpParam.getCharset(), StandardCharsets.UTF_8.name());
        String[] charsets = StringUtils.split(charset, " ");
        Charset inCharset, outCharset;
        if (charsets.length == 1) {
            inCharset = Charset.forName(charsets[0]);
            outCharset = inCharset;
        } else {
            inCharset = Charset.forName(charsets[0]);
            outCharset = Charset.forName(charsets[1]);
        }

        String table = dumpParam.getTable();
        List<String> tables = Arrays.asList(table == null || table.length() == 0 ? new String[]{} : StringUtils.split(table, " "));

        int bufferSize = dumpParam.getBufferSize();

        InputStreamReader reader = new InputStreamReader(new FileInputStream(inFile), inCharset);
        BufferedReader bufferedReader;
        if (bufferSize > 0) {
            bufferedReader = new BufferedReader(reader, bufferSize);
        } else {
            bufferedReader = new BufferedReader(reader);
        }

        WriteHandler currentWriteHandler = WriteHandlerFactory.getDefaultWriteHandler();

        List<String> startContents = new ArrayList<>(16);

        List<String> endContents = new ArrayList<>(16);

        List<String> bufferLineList = new ArrayList<>(16);

        try {

            boolean excludeMode = dumpParam.isExcludeMode();

            boolean startHasFindTable = false;

            int startHasFindTableEndLine = -1;

            DbTag currentDbTag = DbTag.START;

            TableStructureModel tableStructureModel = null;

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (currentDbTag == DbTag.START) {
                    //添加到开始的内容列表中
                    startContents.add(line);

                    if (!startHasFindTable && TableStructureUtils.isMatch(line)) {
                        //解析到第一个表时(匹配到table structure语句)
                        startHasFindTable = true;

                        //获取匹配的writeHandler
                        WriteHandler matchWriteHandler = WriteHandlerFactory.getMatchWriteHandler(startContents);
                        if (matchWriteHandler != null) {
                            currentWriteHandler = matchWriteHandler;
                        } else {
                            currentWriteHandler = WriteHandlerFactory.getDefaultWriteHandler();
                        }

                        tableStructureModel = currentWriteHandler.getTableStructureModel();
                        startHasFindTableEndLine = tableStructureModel.getEndLine();

                        //设置当前writeHandler
                        WriteHandlerHolder.setWriteHandler(currentWriteHandler);

                        //初始化当前writer
                        currentWriteHandler.initCurrentWriter(outPath, outCharset);
                    }


                    if (startHasFindTable && startHasFindTableEndLine-- == 0) {
                        //处理开始内容
                        int size = startContents.size();

                        List<String> firstTableStructureContents = new ArrayList<>(8);
                        for (int i = 0; i < tableStructureModel.getTotal(); i++) {
                            firstTableStructureContents.add(0, startContents.remove(size - i - 1));
                        }

                        //输出开始内容
                        currentWriteHandler.write(startContents, inCharset, outCharset);

                        String tableStructureText = firstTableStructureContents.get(tableStructureModel.getValueIndex());

                        //判断是否匹配到对应的表
                        boolean isMatchTable = currentWriteHandler.isMatchTable(tables, TableStructureUtils.getTable(tableStructureText), excludeMode);

                        if (isMatchTable) {
                            currentDbTag = DbTag.MATCH_TABLE;
                            currentWriteHandler.writeTableStructure(firstTableStructureContents, inCharset, outCharset);
                        } else {
                            currentDbTag = DbTag.NOT_MATCH_TABLE;
                        }
                    }

                } else if (currentDbTag == DbTag.MATCH_TABLE) {
                    //匹配table时

                    if (currentWriteHandler.isEndTag(line)) {
                        currentDbTag = DbTag.END;
                        endContents.add(line);
                    } else {
                        if (currentWriteHandler.isPossibleTableTag(line)) {
                            bufferLineList.add(line);
                        }

                        if (bufferLineList.isEmpty()) {
                            //输出内容
                            currentWriteHandler.write(line, inCharset, outCharset);
                        } else {

                            if (bufferLineList.size() == tableStructureModel.getTotal()) {
                                String tableStructureText = bufferLineList.get(tableStructureModel.getValueIndex());

                                if (TableStructureUtils.isMatch(tableStructureText)) {
                                    boolean isMatchTable = currentWriteHandler.isMatchTable(tables, TableStructureUtils.getTable(tableStructureText), excludeMode);
                                    if (isMatchTable) {
                                        currentDbTag = DbTag.MATCH_TABLE;
                                        currentWriteHandler.writeTableStructure(bufferLineList, inCharset, outCharset);
                                    } else {
                                        currentDbTag = DbTag.NOT_MATCH_TABLE;
                                    }
                                } else {
                                    //直接输出内容
                                    currentWriteHandler.write(bufferLineList, inCharset, outCharset);
                                }
                                //清空buffer
                                bufferLineList.clear();
                            }
                        }
                    }

                } else if (currentDbTag == DbTag.NOT_MATCH_TABLE) {

                    if (currentWriteHandler.isEndTag(line)) {
                        currentDbTag = DbTag.END;
                        endContents.add(line);
                    } else {
                        if (currentWriteHandler.isPossibleTableTag(line)) {
                            bufferLineList.add(line);
                        }

                        if (bufferLineList.size() == tableStructureModel.getTotal()) {
                            String tableStructureText = bufferLineList.get(tableStructureModel.getValueIndex());
                            if (TableStructureUtils.isMatch(tableStructureText)) {
                                boolean isMatchTable = currentWriteHandler.isMatchTable(tables, TableStructureUtils.getTable(tableStructureText), excludeMode);
                                if (isMatchTable) {
                                    currentDbTag = DbTag.MATCH_TABLE;
                                    currentWriteHandler.writeTableStructure(bufferLineList, inCharset, outCharset);
                                } else {
                                    currentDbTag = DbTag.NOT_MATCH_TABLE;
                                }
                            }
                            //清空buffer
                            bufferLineList.clear();
                        }
                    }

                } else if (currentDbTag == DbTag.END) {
                    endContents.add(line);
                }
            }

            //处理最后的内容
            if (currentDbTag == DbTag.START) {
                //初始化当前writer(避免未初始化)
                currentWriteHandler.initCurrentWriter(outPath, outCharset);

                currentWriteHandler.write(startContents, inCharset, outCharset);
            } else if (currentDbTag == DbTag.MATCH_TABLE || currentDbTag == DbTag.NOT_MATCH_TABLE) {
                //输出最后一行
                if (line != null) {
                    currentWriteHandler.write(line, inCharset, outCharset);
                }
            } else if (currentDbTag == DbTag.END) {
                currentWriteHandler.write(endContents, inCharset, outCharset);
            }
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
            }

            try {
                currentWriteHandler.writeFinish();
            } catch (Exception e) {
            }

            WriteHandlerHolder.reset();
            WriterHolder.reset();

            startContents.clear();
            endContents.clear();
            bufferLineList.clear();

        }
    }


}
