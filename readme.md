# sql-dump-split

*可从mysql数据导出工具导出的sql文件中导出指定表/排除指定表及转换编码集*

## 支持导出工具:

    MySQL dump 、 Navicat

## 命令

``` 

#查看帮助
java -jar sql-dump-split-1.0-SNAPSHOT.jar --help

#转换UTF-8 -> GBK
java -jar sql-dump-split-1.0-SNAPSHOT.jar -in ../db/ds_export-dump.sql -out "../db/ds_export-dump-result(gbk).sql" -charset "UTF-8 GBK"

#只导出user
java -jar sql-dump-split-1.0-SNAPSHOT.jar -in ../db/ds_export-dump.sql -out "../db/ds_export-dump-result(user).sql" -table user

#导出user和sys_config
java -jar sql-dump-split-1.0-SNAPSHOT.jar -in ../db/ds_export-dump.sql -out "../db/ds_export-dump-result(user-sys_config).sql" -table "user sys_config"

#排除导出user
java -jar sql-dump-split-1.0-SNAPSHOT.jar -in ../db/ds_export-dump.sql -out "../db/ds_export-dump-result(-exclude-user).sql" -table user -exclude

``` 

