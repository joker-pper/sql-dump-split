package com.joker17.sql.dump.model;

public class TableStructureModel {

    private String textPrefix;

    private int total;

    private int valueIndex;

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
