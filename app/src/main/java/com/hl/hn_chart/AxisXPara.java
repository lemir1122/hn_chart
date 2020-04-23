package com.hl.hn_chart;


/*
 * X轴参数
 * hzl add for 2020/04/23
 */
public class AxisXPara {

    /**
     * 设置X轴类型
     * DATE：时间类型，可以通过setDatePara设置显示格式，如果要使用yyyy-MM-dd HH:mm:ss，
     *       请将此字符串分离成两个字符串，分别赋给firstformat和secondformat
     * STRING：字符串类型，注意字符串数量，屏幕宽度有限，可能会导致字符串叠加显示，
     *         使用字符串类型时，请确保提供的表数据中，有一行包含所有字段名
     */
    public enum AxisType{
        DATE,
        STRING
    }

    private AxisType axisType;

    //第一排时间显示格式
    private String firstDateFormat;
    //第二排时间显示格式
    private String secondDateFormat;

    //x轴的最大最小时间只是用于初始化使用，如果设置的值大于最大时间和小于最小时间，将直接按设置的值来绘制x轴。
    //默认最大时间
    private long maxDate;
    //默认最小时间
    private long minDate;

    public AxisXPara(AxisType axisType){
        this.axisType = axisType;
    }


    public void setDatePara(String firstformat,String secondformat){
        firstDateFormat = firstformat;
        secondDateFormat = secondformat;
    }

    public void setDateDefault(long max,long min){
        maxDate = max;
        minDate = min;
    }

    public AxisType getAxisType() {
        return axisType;
    }


    public String getFirstDateFormat() {
        return firstDateFormat;
    }

    public String getSecondDateFormat() {
        return secondDateFormat;
    }

    public long getMaxDate() {
        return maxDate;
    }

    public long getMinDate() {
        return minDate;
    }

}
