package com.shj.eids.domain;

/**
 * @ClassName: DataItem
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-12 16:22
 **/
public class DataItem{
    String name;
    Integer value;

    public DataItem(String name, Integer value) {
        this.name = name;
        this.value = value;
    }
    public DataItem() {
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}