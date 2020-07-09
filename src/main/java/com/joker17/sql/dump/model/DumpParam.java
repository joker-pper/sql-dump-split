package com.joker17.sql.dump.model;

import com.beust.jcommander.Parameter;

public class DumpParam {

    @Parameter(names = {"-in"}, required = true, description = "in file path")
    private String inPath;

    @Parameter(names = {"-out"}, required = true, description = "out file path")
    private String outPath;

    @Parameter(names = {"-buffer", "-buffer-size"}, description = "buffer size")
    private int bufferSize;

    /**
     * 编码(输入/输出) 以空格分割 可为单个
     */
    @Parameter(names = "-charset", description = "in and out charset (optional), default value utf-8, more should be separated by a space")
    private String charset;

    /**
     * 要筛选的table 以空格分隔 可为空(全部)
     */
    @Parameter(names = "-table", description = "optional, more should be separated by a space")
    private String table;

    /**
     * 是否为排除模式
     */
    @Parameter(names = "-exclude", description = "exclude table mode")
    private boolean excludeMode;

    @Parameter(names = "-detail", description = "show option detail")
    private boolean detail;

    @Parameter(names = {"--help", "--h"}, help = true, order = 5)
    private boolean help;

    public String getInPath() {
        return inPath;
    }

    public void setInPath(String inPath) {
        this.inPath = inPath;
    }

    public String getOutPath() {
        return outPath;
    }

    public void setOutPath(String outPath) {
        this.outPath = outPath;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public boolean isExcludeMode() {
        return excludeMode;
    }

    public void setExcludeMode(boolean excludeMode) {
        this.excludeMode = excludeMode;
    }

    public boolean isDetail() {
        return detail;
    }

    public void setDetail(boolean detail) {
        this.detail = detail;
    }

    public boolean isHelp() {
        return help;
    }

    public void setHelp(boolean help) {
        this.help = help;
    }

    @Override
    public String toString() {
        return "DumpParam{" +
                "inPath='" + inPath + '\'' +
                ", outPath='" + outPath + '\'' +
                ", bufferSize=" + bufferSize +
                ", charset='" + charset + '\'' +
                ", table='" + table + '\'' +
                ", excludeMode=" + excludeMode +
                ", detail=" + detail +
                ", help=" + help +
                '}';
    }
}
