package com.hl.hn_chart;

import android.util.Pair;

import java.util.List;

/*
 * 趋势图数据源
 * hzl add for 2020/04/23
 */
public class ChartData {
    //数据标题，在趋势图的上方显示
    private String title;
    //控制数据绘制的线条颜色
    private int color;
    //数据组元素，Pair.first写入X轴对应数据，Pair.second写入y轴对应数据
    private List<Pair<Object,Object>> data;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public List<Pair<Object, Object>> getData() {
        return data;
    }

    public void setData(List<Pair<Object, Object>> data) {
        this.data = data;
    }
}
