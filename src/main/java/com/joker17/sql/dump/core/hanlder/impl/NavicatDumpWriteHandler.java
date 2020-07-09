package com.joker17.sql.dump.core.hanlder.impl;

import com.joker17.sql.dump.model.TableStructureModel;
import com.joker17.sql.dump.support.StringUtils;

import java.util.List;

public class NavicatDumpWriteHandler extends BaseWriteHandler {


    public NavicatDumpWriteHandler() {
        super(new TableStructureModel("-- Table structure for ", 3, 1, 1));
    }

    @Override
    public boolean match(List<String> contents) {

        if (!contents.isEmpty()) {
            boolean match = false;
            int size = contents.size();
            for (int i = 1; i < size; i++) {
                match = StringUtils.contains(contents.get(i), getType());
                if (match) {
                    break;
                }
            }
            return match;
        }

        return false;
    }

    @Override
    public boolean isEndTag(String text) {
        return StringUtils.equals(text, "SET FOREIGN_KEY_CHECKS = 1;");
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public String getType() {
        return "Navicat";
    }
}
