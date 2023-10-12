package com.daofen.admin.basic;


import com.daofen.admin.utils.JSONUtil;

import java.util.List;


public class PageVO<T> {


    private Integer size = 10; // 默认分页大小：10条

    private Integer index = 1;

    private Integer startIndex = 0;// 分页开始位置，默认从第0条数据开始

    private Integer totalCount; // 总数量

    private Integer totalPage; // 总页数

    private T param; // 查询参数

    private List<T> data;

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getStartIndex() {
        if(getIndex() <=0){
            setIndex(1);
        }
        this.startIndex = (getIndex() - 1) * getSize();
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
        getTotalPage();
    }

    public Integer getTotalPage() {
        if(null == this.getTotalCount() || 0 == this.getTotalCount().intValue()){
            this.totalPage = 0;
        }else{
            if(getTotalCount()%getSize() > 0){
                this.totalPage = getTotalCount()/getSize()+1;
            }else{
                this.totalPage = getTotalCount()/getSize();
            }
        }
        return this.totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public T getParam() {
        return param;
    }

    public void setParam(T param) {
        this.param = param;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return JSONUtil.toString(this);
    }
}
