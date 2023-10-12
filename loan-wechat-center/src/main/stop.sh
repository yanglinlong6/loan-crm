#!/bin/bash

WORKDIR=$(cd `dirname $0`; pwd);
PID=`ps -ef|grep $WORKDIR |grep -v grep |awk '{print($2)}'`

if [ "X$PID" != "X" ]
then
	
	kill -9 `ps -ef|grep $WORKDIR |grep java|grep -v grep |awk '{print($2)}'`

fi


echo "Stop Application DONE"
