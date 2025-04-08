@echo off
chcp 65001 > nul
java -Dfile.encoding=UTF-8 -jar target/im-generator-basic-1.0-SNAPSHOT-jar-with-dependencies.jar %*