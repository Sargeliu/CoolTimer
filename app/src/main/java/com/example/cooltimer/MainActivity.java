package com.example.cooltimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SeekBar seekBar;
    private TextView textView;
    private boolean isTimerOn; //создаём переменную для таймера
    private Button button; // чтоб изменять текст на кнопке

    private CountDownTimer countDownTimer;
    private int defaultInterval;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBar = findViewById(R.id.seekBar); // привязываем наши переменные
        textView = findViewById(R.id.textView);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        seekBar.setMax(600); // устанавливаем max значение (в нашем случае 10 минут = 600 секунд)
        isTimerOn = false; // ставим что изначально не вкл.
        setIntervalFromSharedPreferences(sharedPreferences);

        button = findViewById(R.id.button);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // привязывем TextView и SeedBar
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                long progressInMillis = progress * 1000; // создали переменну для переводи милисекунд в секунды т.к. progress в секундах
                updateTimer(progressInMillis);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
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

                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    if (sharedPreferences.getBoolean("enable_sound", true)) {

                        String melodyName = sharedPreferences.getString("timer_melody", "bell");
                        if (melodyName.equals("bell")) {
                            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bell_sound); // добавляем звук колокольчика при окончании таймера
                            mediaPlayer.start(); // запускает звук при окончании таймера
                        } else if (melodyName.equals("alarm_siren")) {
                            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alarm_siren_sound);
                            mediaPlayer.start();
                        } else if (melodyName.equals("bip")) {
                            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bip_sound);
                            mediaPlayer.start();
                        }
                    }
                    resetTimer(); // по окончанию вернёт значения по умолчанию
                }
            };
            countDownTimer.start();
        } else {
            resetTimer();
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

    private void resetTimer() { // рефакторинг
        countDownTimer.cancel(); // остановить переменную
        button.setText("Start"); //возвращаем текст по умолчанию
        seekBar.setEnabled(true); //вкл. возможность двигать ползунок
        isTimerOn = false;
        setIntervalFromSharedPreferences(sharedPreferences);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.timer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { // этот метод вызывается когда происходит нажатие на иконке SettingsActivity
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent openSettings = new Intent(this, SettingsActivity.class);
            startActivity(openSettings);
            return true;
        } else if (id == R.id.action_about) {
            Intent openAbout = new Intent(this, AboutActivity.class);
            startActivity(openAbout);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setIntervalFromSharedPreferences(SharedPreferences sharedPreferences) {
        defaultInterval = Integer.valueOf(sharedPreferences.getString("default_interval", "30"));
        long defaultIntervalInMillis = defaultInterval * 1000;
        updateTimer(defaultIntervalInMillis);
        seekBar.setProgress(defaultInterval);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("default_interval")) {
            setIntervalFromSharedPreferences(sharedPreferences);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
}