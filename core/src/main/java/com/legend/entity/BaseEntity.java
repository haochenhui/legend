package com.legend.entity;

import com.alibaba.fastjson.JSONObject;
import com.legend.util.DateTimeUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by hch on 2017/9/22.
 */
public abstract class BaseEntity implements Serializable {
    private static final long serialVersionUID = -920462846232446923L;
    protected String id;
    protected Integer status;
    protected String statusDesc;
    protected Date createTime;
    protected String createTimeStr;
    protected Date updateTime;
    protected String updateTimeStr;
    protected String createBy;
    protected String updateBy;

    public BaseEntity() {
    }

    public String getCreateTimeStr() {
        return this.createTimeStr;
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
        this.createTime = DateTimeUtils.string2Date(createTimeStr, "yyyy-MM-dd HH:mm:ss");
    }

    public String getUpdateTimeStr() {
        return this.updateTimeStr;
    }

    public void setUpdateTimeStr(String updateTimeStr) {
        this.updateTimeStr = updateTimeStr;
        this.updateTime = DateTimeUtils.string2Date(updateTimeStr, "yyyy-MM-dd HH:mm:ss");
    }

    public String getStatusDesc() {
        return this.statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return this.createTime == null?new Date():this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
        this.createTimeStr = DateTimeUtils.date2String(createTime, "yyyy-MM-dd HH:mm:ss");
    }

    public Date getUpdateTime() {
        return this.updateTime == null?new Date():this.updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        this.updateTimeStr = DateTimeUtils.date2String(updateTime, "yyyy-MM-dd HH:mm:ss");
    }

    public String getCreateBy() {
        return this.createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getUpdateBy() {
        return this.updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public String toString() {
        return JSONObject.toJSONString(this);
    }
    }