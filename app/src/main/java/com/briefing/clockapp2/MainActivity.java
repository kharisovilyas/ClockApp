package com.briefing.clockapp2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.ViewGroup;

import java.time.LocalTime;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ClockView clockView = new ClockView(this, null);
        setContentView(clockView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}