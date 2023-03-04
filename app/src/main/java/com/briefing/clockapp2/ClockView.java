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

    private Paint paint;
    private Paint hourHandPaint;
    private Paint minuteHandPaint;
    private Paint secondHandPaint;

    private int centerX;
    private int centerY;
    private int radius;

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
        hourHandPaint = new Paint();
        hourHandPaint.setColor(Color.BLACK);
        hourHandPaint.setStrokeWidth(radius - 120);

        minuteHandPaint = new Paint();
        minuteHandPaint.setColor(Color.BLACK);
        minuteHandPaint.setStrokeWidth(radius - 80);

        secondHandPaint = new Paint();
        secondHandPaint.setColor(Color.RED);
        secondHandPaint.setStrokeWidth(radius - 40);
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

        paint = new Paint();
        paint.setColor(Color.argb(0, 0, 0, 0));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        canvas.drawCircle(centerX, centerY, radius, paint);

        // Рисуем стрелки часов
        canvas.save();
        canvas.rotate(90);
        canvas.rotate(30 * hour + 0.5f * minute, centerX, centerY);
        canvas.drawLine(centerX, centerY, centerX, centerY - radius / 2, hourHandPaint);
        canvas.restore();

        canvas.save();
        canvas.rotate(90);
        canvas.rotate(6 * minute + 0.1f * second, centerX, centerY);
        canvas.drawLine(centerX, centerY, centerX, centerY - radius * 0.7f, minuteHandPaint);
        canvas.restore();

        canvas.save();
        canvas.rotate(90);
        canvas.rotate(6 * second, centerX, centerY);
        canvas.drawLine(centerX, centerY, centerX, centerY - radius * 0.9f, secondHandPaint);
        canvas.restore();
    }
}