package com.joker17.sql.dump.core.hanlder.impl;

import com.joker17.sql.dump.model.TableStructureModel;
import com.joker17.sql.dump.support.StringUtils;

import java.util.List;

public class MysqlDumpWriteHandler extends BaseWriteHandler {


    public MysqlDumpWriteHandler() {
        super(new TableStructureModel("-- Table structure for table ", 3, 1, 1));
    }

    @Override
    public boolean match(List<String> contents) {

        if (!contents.isEmpty()) {
            boolean match = StringUtils.contains(contents.get(0), getType());
            if (!match) {
                int size = contents.size();
                for (int i = 1; i < size; i++) {
                    match = StringUtils.contains(contents.get(i), getType());
                    if (match) {
                        break;
                    }
                }
            }
            return match;
        }

        return false;
    }

    @Override
    public boolean isEndTag(String text) {
        return StringUtils.equals(text, "/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;");
    }

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public String getType() {
        return "MySQL dump";
    }
}
