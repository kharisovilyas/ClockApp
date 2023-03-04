package com.briefing.clockapp2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.time.LocalTime;
import java.util.Calendar;


public class ClockView extends View {

    private final Paint paint;
    private final Paint hourHandPaint;
    private final Paint minuteHandPaint;
    private final Paint secondHandPaint;

    private float centerX;
    private float centerY;
    private float radius;

    private final Clock clock;
    private final Thread clockThread;
    private final Paint textPaint;
    private float strokeOfSecond;
    private float strokeOfMinute;
    private float textSize;
    private float stroke;
    private float strokeOfHour;

    //в зависимости от версии получаем данные о времени
    void sendData(Clock clock) {
        int hours;
        int minutes;
        int seconds;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalTime currentTime = LocalTime.now();
            hours = currentTime.getHour();
            minutes = currentTime.getMinute();
            seconds = currentTime.getSecond();
        } else {
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
        paint = new Paint();
        hourHandPaint = new Paint();
        minuteHandPaint = new Paint();
        secondHandPaint = new Paint();
        textPaint = new Paint();
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
        radius = Math.min(w, h) / 2 - 80;
        stroke = radius / 10;
        strokeOfHour = radius / 12;
        strokeOfMinute = radius / 18;
        strokeOfSecond = radius / 26;
        textSize = radius / 7;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Получаем текущее время из объекта Clock
        int hour = clock.getHours();
        int minute = clock.getMinutes();
        int second = clock.getSeconds();
        float secondHandLength = radius - (radius/30);
        float minuteHandLength = radius - (radius/4);
        float hourHandLength = radius - (radius/2);

        paint.setColor(Color.LTGRAY);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(stroke);
        paint.setShadowLayer(10, 0, 0, Color.BLACK);
        canvas.drawCircle(centerX, centerY, radius + (radius/6), paint);
        paint.setColor(Color.DKGRAY);
        canvas.drawCircle(centerX, centerY, radius/20, paint);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(centerX, centerY, radius + (radius/6), paint);

        textPaint.setTextSize(textSize);
        textPaint.setColor(Color.WHITE);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setShadowLayer(10, 0, 0, Color.BLACK);
        for (int i = 1; i <= 12; i++) {
            float numberX = (float) (centerX + (radius/1.12f) * Math.cos(Math.toRadians(i * 30 - 90)));
            float numberY = (float) (centerY + (radius/1.12f) * Math.sin(Math.toRadians(i * 30 - 90)));
            canvas.drawText(String.valueOf(i), numberX, numberY, textPaint);
        }
        // Рисуем стрелки часов
        hourHandPaint.setColor(Color.DKGRAY);
        hourHandPaint.setStrokeWidth(strokeOfHour);
        hourHandPaint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(centerX, centerY, centerX + hourHandLength * (float) Math.cos(Math.toRadians(hour * 30 - 90)),
                centerY + hourHandLength * (float) Math.sin(Math.toRadians(hour * 30 - 90)), hourHandPaint);

        minuteHandPaint.setColor(Color.DKGRAY);
        minuteHandPaint.setStrokeWidth(strokeOfMinute);
        minuteHandPaint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(centerX, centerY, centerX + minuteHandLength * (float) Math.cos(Math.toRadians(minute * 6 - 90)),
                centerY + minuteHandLength * (float) Math.sin(Math.toRadians(minute * 6 - 90)), minuteHandPaint);

        secondHandPaint.setColor(Color.DKGRAY);
        secondHandPaint.setStrokeWidth(strokeOfSecond);
        secondHandPaint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(centerX, centerY, centerX + secondHandLength * (float) Math.cos(Math.toRadians(second * 6 - 90)),
                centerY + secondHandLength * (float) Math.sin(Math.toRadians(second * 6 - 90)), secondHandPaint);

    }
}