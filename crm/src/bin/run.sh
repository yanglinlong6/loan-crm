#!/bin/bash
yunxin=`echo $0`
work_path=$(cd `dirname $0`; pwd)
base_path=${work_path%/*}
path_name=${base_path##*/}
pid_file=$work_path/$path_name
sh_f=${yunxin##*/}
yml_path="$work_path/etc/"
echo "配置文件路径:$yml_path"


JAVA_OPTS_PRO="
    -Xms3000m
    -Xmx3000m
    -Xss512k
    -XX:MetaspaceSize=10m
    -XX:MaxMetaspaceSize=256m
    -XX:+UseAdaptiveSizePolicy
    -XX:+HeapDumpOnOutOfMemoryError
    -XX:HeapDumpPath=/data/logs/crm/dump/crm_dump.hprof
    -XX:+PrintGCDetails -XX:+PrintGCTimeStamps
    -XX:+PrintGCTimeStamps -Xloggc:/data/logs/crm/dump/crm_gc.log"

#JAVA_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"

start(){
    if [ -f $pid_file ];then
        pid_num=`cat $pid_file`
        a=`ps -ef | grep $pid_num | grep -v grep | wc -l`
        if [ $a -gt 0 ]; then
            echo "$work_path/bin/$sh_f is running!"
        else
            rm -rf $pid_file
            java $JAVA_OPTS_PRO -jar $work_path/crm-*.jar --spring.profiles.active=pro  >/dev/null 2>&1 &
        fi
    else
        java $JAVA_OPTS_PRO -Dloader.path=$WORKDIR/lib -jar $work_path/crm-*.jar  --spring.profiles.active=pro  >/dev/null 2>&1 &
        [ $? -eq 0 ] && echo "$work_path/$sh_f start is successful!!!" ; ps -ef | grep $base_path | grep -Ev "grep|$sh_f" | awk '{print $2}' > $pid_file || echo "$work_path/$sh_f start failed, please check!"
    fi
}
#stop(){
#    if [ -f $pid_file ]; then
#        pid_num=`cat $pid_file`
#        a=`ps -ef | grep $pid_num | grep -v grep | wc -l`
#        if [ $a -gt 0 ]; then
#            kill -s 9 $pid_num
#            [ $? -eq 0 ] && echo "stopping $path_name pid is $pid_num success" || echo "stopping $path_name pid is $pid_num failed"
#            rm -rf $pid_file
#        else
#            rm -rf $pid_file
#        fi
#    else
#        pid_num=`ps -ef | grep $work_path | grep -Ev "grep|$sh_f" | awk '{print $2}'`
#        if [ "X$pid_num" = "X" ]; then
#            echo "$work_path/bin/$sh_f is not running"
#        else
#            kill -s 9 $pid_num
#            [ $? -eq 0 ] && echo "stopping $path_name pid is $pid_num success" || echo "stopping $path_name pid is $pid_num failed"
#        fi
#    fi
#}

stop(){
  pid=`jps | grep crm | awk '{print $1}'`
  kill $pid
  echo "stop $pid sucessfull"
}

case $1 in
start)
    start
    ;;
stop)
    stop
    ;;
restart)
    stop
    start
    ;;
*)
    echo "need input:(start|stop|restart)"
    ;;
esac
