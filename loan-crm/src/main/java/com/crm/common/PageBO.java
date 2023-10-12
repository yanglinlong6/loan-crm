package com.crm.common;


import java.util.List;


public class PageBO<T> {

    private int page = 1;// page:表示第几页

    private int start=0;

    private int size=10;// 每页大小

    private int pageCount;// 分页数量

    private int totalCount;// 总数量

    T paramObject;

    List<T> dataList;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getStart() {
        this.start = (this.page-1)*this.size;
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {

        this.pageCount = pageCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
        if(totalCount < 0)
            return;
        if(totalCount == 0){
            setPageCount(0);
        }else if(totalCount%this.getSize() == 0){
            setPageCount(totalCount/this.getSize());
        }else
            this.setPageCount(totalCount/this.getSize()+1);
    }

    public T getParamObject() {
        return paramObject;
    }

    public void setParamObject(T paramObject) {
        this.paramObject = paramObject;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }
}
