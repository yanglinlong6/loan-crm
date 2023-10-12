package com.loan.cps.entity;

import java.util.HashSet;
import java.util.Set;

public class Session {

    private String userId;

    private Integer nodeId;

    private String sessionId;

    private String domain2;

    private String city;

    private Integer aiCount;

    private Long upstream_time;

    private Integer house;

    private Integer settlement;

    private Set<String> sendFristSet = new HashSet<String>();

    private Set<String> sendSecondSet = new HashSet<String>();

    private Integer location;

    private Integer finish;

    private Long down;

    public Long getDown() {
        return down;
    }

    public void setDown(Long down) {
        this.down = down;
    }

    public Integer getFinish() {
        return finish;
    }

    public void setFinish(Integer finish) {
        this.finish = finish;
    }

    public String getDomain2() {
        return domain2;
    }

    public void setDomain2(String domain2) {
        this.domain2 = domain2;
    }

    public Long getUpstream_time() {
        return upstream_time;
    }

    public void setUpstream_time(Long upstream_time) {
        this.upstream_time = upstream_time;
    }

    public Integer getAiCount() {
        return aiCount;
    }

    public void setAiCount(Integer aiCount) {
        this.aiCount = aiCount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getNodeId() {
        return nodeId;
    }

    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Set<String> getSendFristSet() {
        return sendFristSet;
    }

    public void setSendFristSet(Set<String> sendFristSet) {
        this.sendFristSet = sendFristSet;
    }

    public Set<String> getSendSecondSet() {
        return sendSecondSet;
    }

    public void setSendSecondSet(Set<String> sendSecondSet) {
        this.sendSecondSet = sendSecondSet;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getHouse() {
        return house;
    }

    public void setHouse(Integer house) {
        this.house = house;
    }

    public Integer getSettlement() {
        return settlement;
    }

    public void setSettlement(Integer settlement) {
        this.settlement = settlement;
    }

    public Integer getLocation() {
        return location;
    }

    public void setLocation(Integer location) {
        this.location = location;
    }

}
