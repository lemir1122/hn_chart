package com.hl.hn_chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;

import androidx.annotation.Nullable;

import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/*
 * 柱状图控件
 * 描述：控件通过传递AxisXPara、AxisYPara、List<ChartData>三个参数进行绘制。
 * Y轴支持double类型和自定义小数点位数，参数可自定义最大和最小值，也可以通过传输的数据自行设置
 * X轴支持时间、字符串列、数字。
 *    时间类型：通过AxisXPara.setDatePara指定显示方式，并可以通过AxisXPara.setDateDefault指定初始X长度，
 *             如果显示数据内的时间超过指定的初始最大时间，自动以数据时间为准。
 *    字符串列类型：字符串自动进行文字排序，在柱状图内，多条数据将并排放置，未限制数据的总数量，但需要考虑过多的数据会导致重叠。
 * hzl add for 2020/04/23
 */
public class ColumnChartView extends View {

    AxisXPara axis_x;
    AxisYPara axis_y;

    List<ChartData> datas;

    double value_max = 0;
    double value_min = 0;

    long date_max = 0;
    long date_min = 0;

    //x轴时间类型数量
    int x_d_count = 5;

    //x轴字符串类型集合
    List<String> x_node = new ArrayList<>();

    //y轴数量
    int y_count = 5;

    //数值圆点半径
    float radius = 5;

    Paint paint;
    Paint dottedpaint;
    Paint textpaint;

    boolean init = false;

    public ColumnChartView(Context context) {
        super(context);
        init();
    }

    public ColumnChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ColumnChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){

        //绘制线条、刻度、曲线参数
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);  //绘制空心圆或 空心矩形
        paint.setAntiAlias(true); //消除锯齿
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(3.0f);

        //绘制刻度数值、曲线数值参数
        textpaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);// 设置画笔
        textpaint.setTextSize(16.0f);// 字体大小
        textpaint.setTypeface(Typeface.DEFAULT_BOLD);// 采用默认的宽度
        textpaint.setColor(Color.DKGRAY);// 采用的颜色
        textpaint.setTextAlign(Paint.Align.CENTER);

        //绘制虚线参数
        dottedpaint = new Paint();
        dottedpaint.setAntiAlias(true); //消除锯齿
        dottedpaint.setStyle(Paint.Style.STROKE);
        dottedpaint.setColor(Color.LTGRAY);
        dottedpaint.setStrokeWidth(1.0f);
        DashPathEffect pathEffect = new DashPathEffect(new float[] { 10,10 }, 0);
        dottedpaint.setPathEffect(pathEffect);
    }

    public void initView(AxisXPara axis_x,AxisYPara axis_y,List<ChartData> datas){
        this.axis_x = axis_x;
        this.axis_y = axis_y;
        this.datas = datas;
        init = true;
        invalidate();
    }

    public void updateData(){
        if(init){
            try{
                x_node.clear();
                for(ChartData data : datas){
                    //定义是否需要替换x_node
                    for(Pair<Object, Object> pair : data.getData()){
                        double value = (double)pair.second;
                        if(value_max == 0 && value_min == 0){
                            value_max = value;
                            value_min = value;
                        }else{
                            value_max = value > value_max ? value : value_max;
                            value_min = value < value_min ? value : value_min;
                        }

                        if(axis_x.getAxisType() == AxisXPara.AxisType.DATE) {
                            long date = (long)pair.first;
                            if(date_max == 0 && date_min == 0){
                                date_max = date;
                                date_min = date;
                            }else{
                                date_max = date > date_max ? date : date_max;
                                date_min = date < date_min ? date : date_min;
                            }
                        }else{
                            if(!x_node.contains(String.valueOf(pair.first))){
                                x_node.add(String.valueOf(pair.first));
                            }
                        }
                    }

                    //按汉字排序
                    Collections.sort(data.getData(), new Comparator<Pair<Object, Object>>() {

                        public int compare(Pair<Object, Object> o1, Pair<Object, Object> o2) {
                            String name1 = String.valueOf(o1.first);
                            String name2 = String.valueOf(o2.first);
                            Collator instance = Collator.getInstance(Locale.CHINA);
                            return instance.compare(name1, name2);

                        }
                    });
                }

                //按汉字排序
                Collections.sort(x_node, new Comparator<String>() {
                    public int compare(String o1, String o2) {
                        Collator instance = Collator.getInstance(Locale.CHINA);
                        return instance.compare(o1, o2);
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        invalidate();
    }

    public void clear(){
        this.axis_x = null;
        this.axis_y = null;
        this.datas = null;
        init = false;
        invalidate();
    }

    public boolean isInit(){
        return init;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        canvas.drawColor(Color.WHITE);

        if(this.getWidth() == 0 || axis_x == null || axis_y == null || !init){
            super.onDraw(canvas);
            return;
        }

        int padding = 60;
        int scalewidth = 10;
        int textheight = 16;
        int startx = padding;
        int starty = this.getHeight() - padding;
        int x_lenght = this.getWidth() - padding * 2;
        int y_lenght = this.getHeight() - padding * 2;
        int textmargins = (int)((double)scalewidth * 0.5);

        paint.setColor(Color.GRAY);
        //y轴
        canvas.drawLine(startx,padding,startx,starty,paint);

        //x轴
        canvas.drawLine(startx,starty,this.getWidth() - padding,starty,paint);


        ///////////////////////////绘制y轴//////////////////////////////////
        if(axis_y.isLimit()){
            value_max = axis_y.getMaxNumber();
            value_min = axis_y.getMinNumber();
        }
        //将整体值长度按节点数进行分割
        //每个节点之间间隔的值长度
        double interval_y = (value_max - value_min) / (double)(y_count - 1);
        //每个节点之间间隔的坐标长度
        int y_lenght_interval = y_lenght / (y_count - 1);
        //获取单位值所换算的y轴长度
        double unit_lenght_y = (double)y_lenght_interval / interval_y;

        for(int i = 0;i<y_count;i++){
            double value = value_max - interval_y * i;
            String text = String.format("%."+axis_y.getDecimalPlaces()+"f",value);

            //绘制刻度
            int drawy = y_lenght_interval * i + padding;
            paint.setColor(Color.GRAY);
            canvas.drawLine(padding - scalewidth,drawy,padding,drawy,paint);

            //绘制虚线
            if(i != y_count - 1){
                Path dottedpath = new Path();
                dottedpath.moveTo(padding,drawy);
                dottedpath.lineTo(getWidth() - padding,drawy);
                canvas.drawPath(dottedpath,dottedpaint);
            }

            //设置文字绘制范围
            int text_top = drawy - textheight / 2;
            int text_bottom = text_top + textheight;
            //文字居中绘制
            Paint.FontMetricsInt fontMetrics = textpaint.getFontMetricsInt();
            int baseline = (text_top + text_bottom - fontMetrics.bottom - fontMetrics.top) / 2;
            canvas.drawText(text,padding / 2,baseline ,textpaint);
        }

        ///////////////////////////绘制x轴和参数//////////////////////////////////
        if(axis_x.getAxisType() == AxisXPara.AxisType.DATE){
            if(axis_x.getMaxDate() > date_max || axis_x.getMinDate() < date_min){
                date_max = axis_x.getMaxDate();
                date_min = axis_x.getMinDate();
            }
            //将整体时间长度按节点数进行分割
            //每个节点之间间隔的时间戳长度
            long interval = (date_max - date_min) / (x_d_count - 1);
            //每个节点之间间隔的坐标长度
            int x_lenght_interval = x_lenght / (x_d_count - 1);
            //获取单位时间所换算的x轴长度
            double unit_lenght_x = (double)x_lenght_interval / (double)interval;

            for(int i = 0;i<x_d_count;i++){
                long time = date_min + interval * i;
                String text = String.valueOf(time);

                if(!axis_x.getFirstDateFormat().isEmpty()){
                    SimpleDateFormat ff = new SimpleDateFormat(axis_x.getFirstDateFormat());
                    text = ff.format(new Date(time));
                }

                //绘制刻度
                int drawx = x_lenght_interval * i + padding;
                paint.setColor(Color.GRAY);
                canvas.drawLine(drawx,getHeight() - padding,drawx,getHeight() - padding + scalewidth,paint);

                //设置文字绘制范围
                int text_top = getHeight() - padding + scalewidth + textmargins;
                int text_bottom = text_top + textheight;
                //文字居中绘制
                Paint.FontMetricsInt fontMetrics = textpaint.getFontMetricsInt();
                int baseline = (text_top + text_bottom - fontMetrics.bottom - fontMetrics.top) / 2;
                canvas.drawText(text,drawx,baseline ,textpaint);

                //绘制第二行文字
                if(!axis_x.getSecondDateFormat().isEmpty()){
                    SimpleDateFormat ff = new SimpleDateFormat(axis_x.getSecondDateFormat());
                    text = ff.format(new Date(time));

                    //设置文字绘制范围
                    text_top = text_bottom + textmargins;
                    text_bottom = text_top + textheight;
                    //文字居中绘制
                    baseline = (text_top + text_bottom - fontMetrics.bottom - fontMetrics.top) / 2;
                    canvas.drawText(text,drawx,baseline ,textpaint);
                }

            }


            if(datas != null){
                List<Point> points = new ArrayList<>();
                int title_width = 0;
                int max_y = 0;

                for(ChartData data : datas){
                    points.clear();
                    for(Pair<Object, Object> pair : data.getData()){
                        long date = (long)pair.first;
                        double value = (double)pair.second;

                        int x = (int)((double)(date - date_min) * unit_lenght_x);
                        int y = (int)((value - value_min) * unit_lenght_y);

                        //将xy转换为绝对坐标系
                        x = startx + x;
                        y = starty - y;

                        points.add(new Point(x,y));

                        max_y = y > max_y ? y : max_y;
                    }

                    Rect rect = new Rect();
                    paint.getTextBounds(data.getTitle(), 0, data.getTitle().length(), rect);
                    //长度长度包括标题文字长度 + 文字与线条间隔 + 线条长度 + 与下一个标题的距离
                    title_width += rect.width() + scalewidth + padding + padding;

                    LinearGradient linearGradient=new LinearGradient(padding,padding,padding,starty,new int[]{
                            data.getColor() + (200 << 24),
                            data.getColor() + (100 << 24)},
                            new float[]{0,1F}, Shader.TileMode.CLAMP);
                    paint.setShader(linearGradient);
                    for(int i = 0;i<points.size();i++){
                        Point point = points.get(i);
                        canvas.drawLine(point.x,point.y,point.x,starty,paint);
                    }
                }

                paint.setShader(null);
                paint.setStrokeWidth(3f);

                if(title_width != 0){
                    //如果有数据，则减掉最后一个“标题之间的距离”
                    title_width -= padding;

                    int title_startx = (this.getWidth() - title_width) / 2;
                    int title_starty = padding / 2;

                    for(ChartData data : datas){
                        paint.setShader(null);
                        paint.setColor(data.getColor());

                        Rect rect = new Rect();
                        paint.getTextBounds(data.getTitle(), 0, data.getTitle().length(), rect);

                        //设置文字绘制范围
                        int text_top = title_starty - textheight / 2;
                        int text_bottom = text_top + textheight;
                        //文字居中绘制
                        Paint.FontMetricsInt fontMetrics = textpaint.getFontMetricsInt();
                        int baseline = (text_top + text_bottom - fontMetrics.bottom - fontMetrics.top) / 2;
                        canvas.drawText(data.getTitle(),title_startx,baseline ,textpaint);

                        title_startx += rect.width() + scalewidth;
                        canvas.drawLine(title_startx,title_starty,title_startx + padding,title_starty,paint);
                        title_startx += padding * 2;

                    }
                }

            }

        }else{
            if(x_node.size() != 0){
                //每个节点之间间隔的坐标长度
                //考虑到显示效果，x轴起点和终点不进行绘制，,x_node.size()需要加二，所以此处不减一，并加一
                int x_lenght_interval = x_lenght / (x_node.size() + 1);
                for(int i = 0;i<x_node.size();i++){
                    String text = x_node.get(i);

                    int count = i + 1;
                    //绘制刻度
                    int drawx = x_lenght_interval * count + padding;
                    paint.setColor(Color.GRAY);
                    canvas.drawLine(drawx,getHeight() - padding,drawx,getHeight() - padding + scalewidth,paint);

                    //设置文字绘制范围
                    int text_top = getHeight() - padding + scalewidth + textmargins;
                    int text_bottom = text_top + textheight;
                    //文字居中绘制
                    Paint.FontMetricsInt fontMetrics = textpaint.getFontMetricsInt();
                    int baseline = (text_top + text_bottom - fontMetrics.bottom - fontMetrics.top) / 2;
                    canvas.drawText(text,drawx,baseline ,textpaint);
                }


                if(datas != null){

                    //单个柱状宽度
                    float unit = 8f;
                    paint.setStrokeWidth(unit);
                    //统计所有柱状的总宽度，然后计算做偏移量。由于获得的坐标是中心坐标，因此初始偏移量为 总宽度 / 2 * -1
                    int offset = (int)(unit * (float)datas.size() / 2f * -1f);

                    List<Point> points = new ArrayList<>();
                    int title_width = 0;
                    int max_y = 0;
                    for(ChartData data : datas){
                        points.clear();
                        for(Pair<Object, Object> pair : data.getData()){
                            String str = String.valueOf(pair.first);
                            double value = (double)pair.second;

                            int count = x_node.indexOf(str) + 1;
                            ;
                            int x = x_lenght_interval * count;
                            int y = (int)((value - value_min) * unit_lenght_y);

                            //将xy转换为绝对坐标系
                            x = startx + x;
                            y = starty - y;

                            points.add(new Point(x,y));

                            max_y = y > max_y ? y : max_y;
                        }

                        Rect rect = new Rect();
                        paint.getTextBounds(data.getTitle(), 0, data.getTitle().length(), rect);
                        //长度长度包括标题文字长度 + 文字与线条间隔 + 线条长度 + 与下一个标题的距离
                        title_width += rect.width() + scalewidth + padding + padding;

                        LinearGradient linearGradient=new LinearGradient(padding,padding,padding,starty,new int[]{
                                data.getColor() + (200 << 24),
                                data.getColor() + (100 << 24)},
                                new float[]{0,1F}, Shader.TileMode.CLAMP);
                        paint.setShader(linearGradient);
                        for(int i = 0;i<points.size();i++){
                            Point point = points.get(i);
                            canvas.drawLine(point.x + offset,point.y,point.x + offset,starty,paint);
                        }

                        offset += unit;
                    }

                    paint.setShader(null);
                    paint.setStrokeWidth(3f);

                    if(title_width != 0){
                        //如果有数据，则减掉最后一个“标题之间的距离”
                        title_width -= padding;

                        int title_startx = (this.getWidth() - title_width) / 2;
                        int title_starty = padding / 2;

                        for(ChartData data : datas){
                            paint.setShader(null);
                            paint.setColor(data.getColor());

                            Rect rect = new Rect();
                            paint.getTextBounds(data.getTitle(), 0, data.getTitle().length(), rect);

                            //设置文字绘制范围
                            int text_top = title_starty - textheight / 2;
                            int text_bottom = text_top + textheight;
                            //文字居中绘制
                            Paint.FontMetricsInt fontMetrics = textpaint.getFontMetricsInt();
                            int baseline = (text_top + text_bottom - fontMetrics.bottom - fontMetrics.top) / 2;
                            canvas.drawText(data.getTitle(),title_startx,baseline ,textpaint);

                            title_startx += rect.width() + scalewidth;
                            canvas.drawLine(title_startx,title_starty,title_startx + padding,title_starty,paint);
                            title_startx += padding * 2;

                        }
                    }

                }
            }

        }

        super.onDraw(canvas);

    }

}
