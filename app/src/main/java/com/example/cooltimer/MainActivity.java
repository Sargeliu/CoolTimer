package com.example.cooltimer;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private SeekBar seekBar;
    private TextView textView;
    private boolean isTimerOn; //создаём переменную для таймера
    private Button button; // чтоб изменять текст на кнопке

    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBar = findViewById(R.id.seekBar); // привязываем наши переменные
        textView = findViewById(R.id.textView);

        seekBar.setMax(600); // устанавливаем max значение (в нашем случае 10 минут = 600 секунд)
        seekBar.setProgress(30); // устанавливаем таймер на 30 сек.
        isTimerOn = false; // ставим что изначально не вкл.

        button = findViewById(R.id.button);
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

        if (!isTimerOn) { // делаем проверку таймера. Знак ! говорит о притивоположности. Таймер не вкл.
            button.setText("Stop"); //изменяем текст кнопки
            seekBar.setEnabled(false); //откл. возможность двигать ползунок
            isTimerOn = true;

            // перенесли код запуска таймера. Запускаем таймер если он не вкл.
            countDownTimer = new CountDownTimer(seekBar.getProgress() * 1000, 1000) { // передаем значение от ползунка к TextView
                @Override
                public void onTick(long millisUntilFinished) { // данный метод обновляет каждую секунду
                    updateTimer(millisUntilFinished);
                }

                @Override
                public void onFinish() {
                    MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bell_sound); // добавляем звук колокольчика при окончании таймера
                    mediaPlayer.start(); // запускает звук при окончании таймера
                }
            };
            countDownTimer.start();
        } else {
            countDownTimer.cancel(); // остановить переменную
            textView.setText("00:30"); //возвращаем цифры по умолчанию
            button.setText("Start"); //возвращаем текст по умолчанию
            seekBar.setEnabled(true); //вкл. возможность двигать ползунок
            seekBar.setProgress(30);
            isTimerOn = false;
        }
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