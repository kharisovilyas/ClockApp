package com.briefing.clockapp2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import java.time.LocalTime;
import java.util.Calendar;


public class ClockView extends View {

    private Paint paint;
    private Paint hourHandPaint;
    private Paint minuteHandPaint;
    private Paint secondHandPaint;

    private float centerX;
    private float centerY;
    private float radius;

    private Clock clock;
    private Thread clockThread;


    public ClockView(Context context) {
        super(context);
    }

    void sendData(Clock clock){
        int hours;
        int minutes;
        int seconds;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalTime currentTime = LocalTime.now();
            hours = currentTime.getHour();
            minutes = currentTime.getMinute();
            seconds = currentTime.getSecond();
        }else{
            Calendar calendar = Calendar.getInstance();
            hours = calendar.get(Calendar.HOUR_OF_DAY);
            minutes = calendar.get(Calendar.MINUTE);
            seconds = calendar.get(Calendar.SECOND);
        }
        clock.setHours(hours);
        clock.setMinutes(minutes);
        clock.setSeconds(seconds);
    }

    public ClockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        clock = new Clock();
        clockThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    sendData(clock);
                    postInvalidate(); // Вызываем метод onDraw для перерисовки View
                    try {
                        Thread.sleep(1000); // Приостанавливаем поток на 1 секунду
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        });

        clockThread.start(); // Запускаем поток
    }

    public ClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clockThread.interrupt(); // Прерываем поток при уничтожении View
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;
        radius = Math.min(w, h) / 2 - 40;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Получаем текущее время из объекта Clock
        int hour = clock.getHours();
        int minute = clock.getMinutes();
        int second = clock.getSeconds();
        float secondHandLength = radius - 100;
        float minuteHandLength = radius - 150;
        float hourHandLength = radius - 200;
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        canvas.drawCircle(centerX, centerY, radius, paint);
        paint.setTextSize(60);
        paint.setTextAlign(Paint.Align.CENTER);
        for (int i = 1; i <= 12; i++) {
            float numberX = (float) (centerX + radius * Math.cos(Math.toRadians(i * 30 - 90)));
            float numberY = (float) (centerY + radius * Math.sin(Math.toRadians(i * 30 - 90)));
            canvas.drawText(String.valueOf(i), numberX, numberY, paint);
        }
        // Рисуем стрелки часов
        hourHandPaint = new Paint();
        hourHandPaint.setColor(Color.BLACK);
        hourHandPaint.setStrokeWidth(15);
        hourHandPaint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(centerX, centerY, centerX + hourHandLength * (float) Math.cos(Math.toRadians(hour * 30 - 90)),
                centerY + hourHandLength * (float) Math.sin(Math.toRadians(hour * 30 - 90)), hourHandPaint);

        minuteHandPaint = new Paint();
        minuteHandPaint.setColor(Color.RED);
        minuteHandPaint.setStrokeWidth(10);
        minuteHandPaint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(centerX, centerY, centerX + minuteHandLength * (float) Math.cos(Math.toRadians(minute * 6 - 90)),
                centerY + minuteHandLength * (float) Math.sin(Math.toRadians(minute * 6 - 90)), minuteHandPaint);



        secondHandPaint = new Paint();
        secondHandPaint.setColor(Color.GREEN);
        secondHandPaint.setStrokeWidth(5);
        secondHandPaint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(centerX, centerY, centerX + secondHandLength * (float) Math.cos(Math.toRadians(second * 6 - 90)),
                centerY + secondHandLength * (float) Math.sin(Math.toRadians(second * 6 - 90)), secondHandPaint);

    }
}