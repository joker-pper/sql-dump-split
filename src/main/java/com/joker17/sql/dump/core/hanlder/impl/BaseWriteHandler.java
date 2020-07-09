package com.joker17.sql.dump.core.hanlder.impl;

import com.joker17.sql.dump.core.hanlder.WriteHandler;
import com.joker17.sql.dump.model.TableStructureModel;
import com.joker17.sql.dump.support.StringUtils;
import com.joker17.sql.dump.support.WriteHandlerHolder;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

public class BaseWriteHandler implements WriteHandler {

    private TableStructureModel structureModel;

    public BaseWriteHandler() {
        this(new TableStructureModel(null, 3, 1, 1));
    }

    protected BaseWriteHandler(TableStructureModel tableStructureModel) {
        this.structureModel = tableStructureModel;
    }

    @Override
    public boolean isMatchTableStructure(String text) {
        if (text != null && structureModel != null) {
            return isMatchTableStructure(text, structureModel);
        }
        return false;
    }

    protected boolean isMatchTableStructure(String text, TableStructureModel structureModel) {
        String textPrefix = structureModel.getTextPrefix();
        if (textPrefix != null) {
            int index = text.indexOf(textPrefix);
            if (index >= 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getTableName(String text) {
        if (text != null && structureModel != null) {
            return getTableName(text, structureModel);
        }
        return null;
    }

    protected String getTableName(String text, TableStructureModel structureModel) {
        String textPrefix = structureModel.getTextPrefix();
        if (textPrefix != null) {
            int index = text.indexOf(textPrefix);
            if (index >= 0) {
                return StringUtils.remove(text.substring(index + textPrefix.length()), "`");
            }
        }
        return null;
    }

    @Override
    public TableStructureModel getTableStructureModel() {
        return structureModel;
    }


    @Override
    public boolean isMatchTable(Collection<String> tables, String table, boolean exclude) {
        if (table == null || table.length() == 0) {
            return false;
        }

        if (tables == null || tables.isEmpty()) {
            return true;
        }

        boolean contains = tables.contains(table);
        return exclude ? !contains : contains;
    }

    @Override
    public boolean isPossibleTableTag(String text) {
        return StringUtils.startWith(text, "--");
    }


    @Override
    public boolean isEndTag(String text) {
        return false;
    }

    @Override
    public void initCurrentWriter(String outPath, Charset charset) throws IOException {
        if (WriteHandlerHolder.getWriter() != null) {
            //已初始化
            return;
        }
        File outFile = new File(outPath);
        outFile.getParentFile().mkdirs();
        WriteHandlerHolder.setWriter(getWriter(outFile, charset));
    }

    @Override
    public Writer getWriter(File outFile, Charset charset) throws FileNotFoundException {
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), charset));
    }

    @Override
    public void write(List<String> contents, Charset inCharset, Charset outCharset) {
        try {
            for (String content : contents) {
                write(content, inCharset, outCharset);
            }
        } finally {
            contents.clear();
        }
    }

    @Override
    public void write(String content, Charset inCharset, Charset outCharset) {
        Writer writer = WriteHandlerHolder.getWriter();
        if (writer != null) {
            try {
                writer.write(content + "\r\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void writeFinish() {
        Writer writer = WriteHandlerHolder.getWriter();
        if (writer != null) {
            try {
                writer.flush();
            } catch (IOException e) {

            } finally {
                try {
                    writer.close();
                } catch (IOException e) {
                }
            }
        }
    }

    @Override
    public String getType() {
        return "base";
    }

    @Override
    public int getOrder() {
        return -1;
    }


    @Override
    public int compareTo(WriteHandler o) {
        int x = o.getOrder();
        int y = this.getOrder();
        return Integer.compare(x, y);
    }



}
