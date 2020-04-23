package com.hl.hn_chart;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    LineChartView lc_linegraph;
    ColumnChartView cc_histogram;

    RadioGroup rg_axis;

    Button bt_clear;
    Button bt_add;

    LinearLayout ll_info;

    //y轴类型，0：时间   1：字符串
    int type_y = 0;

    //随机数范围
    int value_min = 0;
    int value_max = 100;

    //默认时间范围，10分钟
    long default_date_range = 1 * 60 * 1000;


    int number = 0;
    int[] colors = new int[]{Color.RED,Color.GREEN,Color.MAGENTA,Color.CYAN,Color.BLUE};

    List<ChartData> datas = new ArrayList<>();

    List<String> defaultitem = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lc_linegraph = findViewById(R.id.lc_linegraph);
        cc_histogram = findViewById(R.id.cc_histogram);

        rg_axis = findViewById(R.id.rg_axis);

        bt_clear = findViewById(R.id.bt_clear);
        bt_add = findViewById(R.id.bt_add);

        ll_info = findViewById(R.id.ll_info);

        init();
        setListener();
    }

    private void init(){
        defaultitem.add("软件工程师");
        defaultitem.add("测试工程师");
        defaultitem.add("系统架构师");
        defaultitem.add("数据库构建师");
        defaultitem.add("UI、UE工程师");
        defaultitem.add("售后服务");
        defaultitem.add("算法工程师");
    }

    private void setListener(){
        rg_axis.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                type_y = checkedId == R.id.rb_date ? 0 : 1;
                lc_linegraph.clear();
                cc_histogram.clear();
                ll_info.removeAllViews();
                number = 0;
            }
        });

        bt_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lc_linegraph.clear();
                cc_histogram.clear();
                ll_info.removeAllViews();
                number = 0;
            }
        });

        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!lc_linegraph.isInit()){
                    initLineChartView();
                }
                if(!cc_histogram.isInit()){
                    initColumnChartView();
                }
                addItem();
            }
        });
    }

    private void addItem(){
        if(number == 5){
            return;
        }
        LinearLayout linearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(params);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        ChartData chartData = new ChartData();
        chartData.setTitle("测试" + number);
        chartData.setColor(colors[number]);
        chartData.setData(new ArrayList<Pair<Object, Object>>());
        number += 1;
        datas.add(chartData);

        Button addnode = new Button(this);
        LinearLayout.LayoutParams addparams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT,1);
        addnode.setLayoutParams(addparams);
        addnode.setText("添加");
        addnode.setTag(chartData);

        Button removenode = new Button(this);
        LinearLayout.LayoutParams removeparams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT,1);
        removenode.setLayoutParams(removeparams);
        removenode.setText("移除上一个");
        removenode.setTag(chartData);

        Button deleteitem = new Button(this);
        LinearLayout.LayoutParams deleteparams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT,1);
        deleteitem.setLayoutParams(deleteparams);
        deleteitem.setText("删除记录");
        deleteitem.setTag(chartData);

        addnode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getTag() == null){
                    return;
                }
                ChartData chartData = (ChartData)v.getTag();
                Random ra = new Random();
                double value = (double)ra.nextInt(value_max);
                value += ((double)ra.nextInt(value_max)) / (double) 100;

                if(type_y == 0){
                    Pair<Object, Object> pair = new Pair<>((Object) System.currentTimeMillis(),(Object)value);
                    chartData.getData().add(pair);
                }else{
                    List<String> item = deepCopy(defaultitem);
                    for(Pair<Object, Object> pair : chartData.getData()){
                        item.remove(String.valueOf(pair.first));
                    }
                    if(item.size() > 0){
                        Pair<Object, Object> pair = new Pair<>((Object) item.get(ra.nextInt(item.size())),(Object)value);
                        chartData.getData().add(pair);

                        //按汉字排序
                        Collections.sort(chartData.getData(), new Comparator<Pair<Object, Object>>() {

                            public int compare(Pair<Object, Object> o1, Pair<Object, Object> o2) {
                                String name1 = String.valueOf(o1.first);
                                String name2 = String.valueOf(o2.first);
                                Collator instance = Collator.getInstance(Locale.CHINA);
                                return instance.compare(name1, name2);

                            }
                        });
                    }

                }
                lc_linegraph.updateData();
                cc_histogram.updateData();
            }
        });

        removenode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getTag() == null){
                    return;
                }
                ChartData chartData = (ChartData)v.getTag();
                if(chartData.getData().size() > 0){
                    chartData.getData().remove(chartData.getData().size() - 1);
                }
                lc_linegraph.updateData();
                cc_histogram.updateData();
            }
        });

        deleteitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getTag() == null){
                    return;
                }
                datas.remove((ChartData)v.getTag());
                lc_linegraph.updateData();
                cc_histogram.updateData();
                if(v.getParent() != null && v.getParent() instanceof View){
                    ll_info.removeView((View) v.getParent());
                }
            }
        });

        linearLayout.addView(addnode);
        linearLayout.addView(removenode);
        linearLayout.addView(deleteitem);

        ll_info.addView(linearLayout);
    }

    private void initLineChartView(){
        datas.clear();
        if(type_y == 0){
            AxisXPara axisXPara = new AxisXPara(AxisXPara.AxisType.DATE);
            axisXPara.setDateDefault(System.currentTimeMillis() + default_date_range ,System.currentTimeMillis());
            axisXPara.setDatePara("HH:mm:ss","");

            AxisYPara axisYPara = new AxisYPara();
            axisYPara.setNumberPara(value_max,value_min,2);

            lc_linegraph.initView(axisXPara,axisYPara,datas);
            lc_linegraph.updateData();
        }else{
            AxisXPara axisXPara = new AxisXPara(AxisXPara.AxisType.STRING);

            AxisYPara axisYPara = new AxisYPara();
            axisYPara.setNumberPara(value_max,value_min,2);

            lc_linegraph.initView(axisXPara,axisYPara,datas);
            lc_linegraph.updateData();
        }
    }

    private void initColumnChartView(){
        datas.clear();
        if(type_y == 0){
            AxisXPara axisXPara = new AxisXPara(AxisXPara.AxisType.DATE);
            axisXPara.setDateDefault(System.currentTimeMillis() + default_date_range ,System.currentTimeMillis());
            axisXPara.setDatePara("HH:mm:ss","");

            AxisYPara axisYPara = new AxisYPara();
            axisYPara.setNumberPara(value_max,value_min,2);

            cc_histogram.initView(axisXPara,axisYPara,datas);
            cc_histogram.updateData();
        }else{
            AxisXPara axisXPara = new AxisXPara(AxisXPara.AxisType.STRING);

            AxisYPara axisYPara = new AxisYPara();
            axisYPara.setNumberPara(value_max,value_min,2);

            cc_histogram.initView(axisXPara,axisYPara,datas);
            cc_histogram.updateData();
        }
    }

    public static <E> List<E> deepCopy(List<E> src) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(src);

            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            @SuppressWarnings("unchecked")
            List<E> dest = (List<E>) in.readObject();
            return dest;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<E>();
        }
    }
}
