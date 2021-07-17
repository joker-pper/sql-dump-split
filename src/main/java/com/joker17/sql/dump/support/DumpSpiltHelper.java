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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DumpSpiltHelper {

    public static void init(WriteHandler... writeHandlers) {

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
            throw new FileNotFoundException(inPath);
        }

        String charset = StringUtils.defaultIfEmpty(dumpParam.getCharset(), "UTF-8");
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
        List<String> tables = Arrays.asList(table == null || table.length() == 0 ? new String[] {} : StringUtils.split(table, " "));

        int bufferSize = dumpParam.getBufferSize();

        InputStreamReader reader = new InputStreamReader(new FileInputStream(inFile), inCharset);
        BufferedReader bufferedReader;
        if (bufferSize > 0) {
            bufferedReader = new BufferedReader(reader, bufferSize);
        } else {
            bufferedReader = new BufferedReader(reader);
        }

        WriteHandler writeHandler = WriteHandlerFactory.getDefaultWriteHandler();

        try {

            List<String> startContents = new ArrayList<>(16);

            List<String> endContents = new ArrayList<>(16);

            List<String> bufferLineList = new ArrayList<>(16);

            boolean excludeMode = dumpParam.isExcludeMode();

            boolean startHasFindTable = false;

            int startHasFindTableEndLine = 1;

            DbTag dbTag = DbTag.START;

            TableStructureModel tableStructureModel = null;

            String line;
            while ((line = bufferedReader.readLine()) != null) {

                if (dbTag == DbTag.START) {

                    startContents.add(line);

                    if (startHasFindTable && --startHasFindTableEndLine == 0) {

                        //处理开始内容
                        int size = startContents.size();

                        List<String> firstTableStructureContents = new ArrayList<>(8);
                        for (int i = 0; i < tableStructureModel.getTotal(); i++) {
                            firstTableStructureContents.add(0, startContents.remove(size - i - 1));
                        }

                        //输出开始内容
                        writeHandler.write(startContents, inCharset, outCharset);

                        //判断是否匹配到对应的表
                        String tableStructureText = firstTableStructureContents.get(tableStructureModel.getValueIndex());

                        boolean isMatchTable = writeHandler.isMatchTable(tables, TableStructureUtils.getTable(tableStructureText), excludeMode);

                        if (isMatchTable) {
                            dbTag = DbTag.MATCH_TABLE;
                            writeHandler.writeTableStructure(firstTableStructureContents, inCharset, outCharset);
                        } else {
                            dbTag = DbTag.NOT_MATCH_TABLE;
                        }

                    } else {
                        if (TableStructureUtils.isMatch(line)) {
                            //第一个表时
                            startHasFindTable = true;

                            //获取匹配的writeHandler
                            WriteHandler matchWriteHandler = WriteHandlerFactory.getMatchWriteHandler(startContents);
                            if (matchWriteHandler != null) {
                                writeHandler = matchWriteHandler;

                                tableStructureModel = writeHandler.getTableStructureModel();

                                startHasFindTableEndLine = tableStructureModel.getEndLine();
                            }

                            WriteHandlerHolder.setWriteHandler(writeHandler);

                            //初始化当前writer
                            writeHandler.initCurrentWriter(outPath, outCharset);

                        }
                    }
                } else if (dbTag == DbTag.MATCH_TABLE) {
                    //匹配table时

                    if (writeHandler.isEndTag(line)) {
                        dbTag = DbTag.END;
                        endContents.add(line);
                    } else {
                        if (writeHandler.isPossibleTableTag(line)) {
                            bufferLineList.add(line);
                        }

                        if (bufferLineList.isEmpty()) {
                            //输出内容
                            writeHandler.write(line, inCharset, outCharset);
                        } else {

                            if (bufferLineList.size() == tableStructureModel.getTotal()) {
                                String tableStructureText = bufferLineList.get(tableStructureModel.getValueIndex());

                                if (TableStructureUtils.isMatch(tableStructureText)) {
                                    boolean isMatchTable = writeHandler.isMatchTable(tables, TableStructureUtils.getTable(tableStructureText), excludeMode);
                                    if (isMatchTable) {
                                        dbTag = DbTag.MATCH_TABLE;
                                        writeHandler.writeTableStructure(bufferLineList, inCharset, outCharset);
                                    } else {
                                        dbTag = DbTag.NOT_MATCH_TABLE;
                                    }
                                } else {
                                    //直接输出内容
                                    writeHandler.write(bufferLineList, inCharset, outCharset);
                                }
                                //清空buffer
                                bufferLineList.clear();
                            }
                        }
                    }

                } else if (dbTag == DbTag.NOT_MATCH_TABLE) {

                    if (writeHandler.isEndTag(line)) {
                        dbTag = DbTag.END;
                        endContents.add(line);
                    } else {
                        if (writeHandler.isPossibleTableTag(line)) {
                            bufferLineList.add(line);
                        }

                        if (bufferLineList.size() == tableStructureModel.getTotal()) {
                            String tableStructureText = bufferLineList.get(tableStructureModel.getValueIndex());
                            if (TableStructureUtils.isMatch(tableStructureText)) {
                                boolean isMatchTable = writeHandler.isMatchTable(tables, TableStructureUtils.getTable(tableStructureText), excludeMode);
                                if (isMatchTable) {
                                    dbTag = DbTag.MATCH_TABLE;
                                    writeHandler.writeTableStructure(bufferLineList, inCharset, outCharset);
                                } else {
                                    dbTag = DbTag.NOT_MATCH_TABLE;
                                }
                            }
                            //清空buffer
                            bufferLineList.clear();
                        }
                    }

                } else if (dbTag == DbTag.END) {
                    endContents.add(line);
                }
            }

            if (dbTag == DbTag.START) {
                writeHandler.initCurrentWriter(outPath, outCharset);

                writeHandler.write(startContents, inCharset, outCharset);
            } else if (dbTag == dbTag.MATCH_TABLE || dbTag == dbTag.NOT_MATCH_TABLE) {
                //输出最后一行
                if (line != null) {
                    writeHandler.write(line, inCharset, outCharset);
                }
            } else if (dbTag == dbTag.END) {
                writeHandler.write(endContents, inCharset, outCharset);
            }
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
            }

            try {
                writeHandler.writeFinish();
            } catch (Exception e) {
            }

            WriteHandlerHolder.reset();
        }


    }


}
