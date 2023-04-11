package com.example.cooltimer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private SeekBar seekBar;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBar = findViewById(R.id.seekBar); // привязываем наши переменные
        textView = findViewById(R.id.textView);

        seekBar.setMax(600); // устанавливаем max значение (в нашем случае 10 минут = 600 секунд)
        seekBar.setProgress(30); // устанавливаем таймер на 30 сек.

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // привязывем TextView и SeedBar
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                long progessInMillis = progress * 1000; // создали переменну для переводи милисекунд в секунды т.к. progress в секундах
                updateTimer(progessInMillis);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void start(View view) { // создаём таймер
        CountDownTimer countDownTimer = new CountDownTimer(seekBar.getProgress() * 1000, 1000) { // передаем значение от ползунка к TextView
            @Override
            public void onTick(long millisUntilFinished) { // данный метод обновляет каждую секунду
                updateTimer(millisUntilFinished);
            }

            @Override
            public void onFinish() {

            }
        };
        countDownTimer.start();
    }

    private void updateTimer(long millisUntilFinished) { // создали переменную для рефакторинга кода
        int minutes = (int) millisUntilFinished/1000/60;
        int seconds = (int) millisUntilFinished/1000 - (minutes * 60);

        String minutesString = "";
        String secondsString = "";

        if (minutes < 10) { // 10 ставим чтоб были нули впереди (например 05:09)
            minutesString = "0" + minutes;
        } else {
            minutesString = String.valueOf(minutes);
        }

        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = String.valueOf(seconds);
        }

        textView.setText(minutesString + ":" + secondsString); // устанавливаем наши значения в TextView
    }
}