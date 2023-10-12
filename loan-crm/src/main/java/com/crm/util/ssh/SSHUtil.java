package com.crm.util.ssh;

import ch.ethz.ssh2.*;
import com.alibaba.fastjson.util.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class SSHUtil {

    private static final Logger log = LoggerFactory.getLogger(SSHUtil.class);


    public static boolean scp(String filePath,String target,String ip,String username,String password) throws IOException {
        Connection conn = new Connection(ip);
        try {
            conn.connect();
            boolean isAuthenticated = conn.authenticateWithPassword(username,password);
            log.info("ssh远程复制文件:file-{},远程ip-{},用户名-{},密码-{},目标目录-{},执行结果:{}",filePath,ip,username,password,target,isAuthenticated);
            if(isAuthenticated){
                SCPClient client = new SCPClient(conn);
                client.put(filePath,target);
                return true;
            }
            return false;
        }catch (Exception e){
            log.error("ssh远程复制文件失败:file-{},远程ip-{},用户名-{},密码-{},目标目录-{},异常信息:{},{}",filePath,ip,username,password,target,e.getMessage(),e);
            return false;
        }finally {
            conn.close();
        }
    }


    public static boolean excute(String ip, String userName, String password,String cmd){
        String result = "";
        Session session = null;
        Connection conn = login(ip, userName, password);
        try {
            if(conn != null){
                session = conn.openSession();  // 打开一个会话
                session.execCommand(cmd);      // 执行命令
                result = processStdout(session.getStdout(), "UTF-8");
                //如果为得到标准输出为空，说明脚本执行出错了
                if(StringUtils.isBlank(result)){
                    log.info("【得到标准输出为空】执行的命令如下:{}",cmd);
                    return false;
                }else{
                    log.info("【执行命令成功】执行的命令如下:{},结果:{}",cmd,result);
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            log.info("【执行命令异常】执行的命令如下:{}",cmd,e);
           return false;
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (session != null) {
                session.close();
            }
        }
    }

    public static Connection login(String ip, String userName, String password){
        boolean isAuthenticated = false;
        Connection conn = null;
        try {
            conn = new Connection(ip);
            conn.connect(); // 连接主机
            isAuthenticated = conn.authenticateWithPassword(userName, password); // 认证
            if(isAuthenticated){
                log.info("ip-{},user-{},password-{}-->认证成功",ip,userName,password);
                return conn;
            } else {
                log.info("ip-{},user-{},password-{}-->认证失败",ip,userName,password);
                return conn;
            }
        } catch (IOException e) {
            log.error("ip-{},user-{},password-{}-->认证异常",ip,userName,password,e);
            return conn;
        }
    }

    private static String processStdout(InputStream in, String charset){
        InputStream stdout = new StreamGobbler(in);
        StringBuffer buffer = new StringBuffer();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout, charset));
            String line = null;
            while((line = br.readLine()) != null){
                buffer.append(line + "\n");
            }
        } catch (UnsupportedEncodingException e) {
            log.error("执行脚本:出错了",e);
        } catch (IOException e) {
            log.error("执行脚本:出错了",e);
        }
        return buffer.toString();
    }
}
