package com.joker17.sql.dump.model;

public class TableStructureModel {

    /**
     * 前缀
     */
    private String textPrefix;

    /**
     * 总行数
     */
    private int total;

    /**
     * value值所处行索引值
     */
    private int valueIndex;

    /**
     * 结束线在前缀下的距离行数
     */
    private int endLine;

    public TableStructureModel(String textPrefix, int total, int valueIndex, int endLine) {
        this.textPrefix = textPrefix;
        this.total = total;
        this.valueIndex = valueIndex;
        this.endLine = endLine;
    }

    public String getTextPrefix() {
        return textPrefix;
    }

    public void setTextPrefix(String textPrefix) {
        this.textPrefix = textPrefix;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getValueIndex() {
        return valueIndex;
    }

    public void setValueIndex(int valueIndex) {
        this.valueIndex = valueIndex;
    }

    public int getEndLine() {
        return endLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }
}
