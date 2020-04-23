package com.hl.hn_chart;


/*
 * Y轴参数
 * hzl add for 2020/04/23
 */
public class AxisYPara {
    //最大值
    private double maxNumber;
    //最小值
    private double minNumber;
    //保留小数点位数
    private int decimalPlaces = 0;
    //确定使用指定的最大值和最小值
    private boolean limit = false;


    public void setNumberPara(double max,double min,int decimalplaces){
        maxNumber = max;
        minNumber = min;
        decimalPlaces = decimalplaces;
        limit = true;
    }

    public void setDecimalPlaces(int decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }


    public double getMaxNumber() {
        return maxNumber;
    }


    public double getMinNumber() {
        return minNumber;
    }


    public boolean isLimit() {
        return limit;
    }

    public int getDecimalPlaces() {
        return decimalPlaces;
    }

}
