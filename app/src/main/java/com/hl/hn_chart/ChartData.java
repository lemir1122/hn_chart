package com.hl.hn_chart;

import android.util.Pair;

import java.util.List;

public class ChartData {
    private String title;
    private int color;
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
