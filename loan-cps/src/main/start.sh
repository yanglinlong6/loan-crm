#!/bin/bash

JAVA="/usr/java/jdk1.8.0_45/jre/bin/java"

WORKDIR=$(cd `dirname $0`; pwd);

PROPERTIES_PATH="$WORKDIR/etc"

#会覆盖默认启动端口
SERVER_PORT="8081"

$JAVA -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 -Dloader.path=$WORKDIR/lib -jar $WORKDIR/loan-cps*.jar  --server.port=$SERVER_PORT --properties.path=$PROPERTIES_PATH --logging.config=$PROPERTIES_PATH/logback.xml >/dev/null 2>&1 &

#$JAVA -Dloader.path=$WORKDIR/lib -jar $WORKDIR/efinancial*.jar  --server.port=$SERVER_PORT --properties.path=$PROPERTIES_PATH

echo "Start Application DONE"
