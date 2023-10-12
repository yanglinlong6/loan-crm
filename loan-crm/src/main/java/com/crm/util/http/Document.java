package com.crm.util.http;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * http传输文件对象
 *
 * @author kongzhimin
 */
public class Document {
    /**
     * text文本内容
     */
    private Map<String, String> textMap;

    /**
     * file数组内容
     */
    private Map<String, List<File>> file;

    public Map<String, String> getTextMap() {
        return textMap;
    }

    public void setTextMap(Map<String, String> textMap) {
        this.textMap = textMap;
    }

    public Map<String, List<File>> getFile() {
        return file;
    }

    public void setFile(Map<String, List<File>> file) {
        this.file = file;
    }

}
