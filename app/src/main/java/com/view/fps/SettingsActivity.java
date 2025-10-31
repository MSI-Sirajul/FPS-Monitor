package com.view.fps;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

public class SettingsActivity extends Activity {

    private CheckBox checkBoxFPS, checkBoxRAM;
    private SeekBar seekBarSize, seekBarOpacity;
    private TextView sizeValue, opacityValue;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences("fps_prefs", MODE_PRIVATE);

        checkBoxFPS = findViewById(R.id.checkbox_fps);
        checkBoxRAM = findViewById(R.id.checkbox_ram);
        seekBarSize = findViewById(R.id.seekbar_size);
        seekBarOpacity = findViewById(R.id.seekbar_opacity);
        sizeValue = findViewById(R.id.text_size_value);
        opacityValue = findViewById(R.id.text_opacity_value);

        // প্রাথমিক মান লোড করো
        checkBoxFPS.setChecked(prefs.getBoolean("show_fps", true));
        checkBoxRAM.setChecked(prefs.getBoolean("show_ram", true));
        seekBarSize.setProgress(prefs.getInt("overlay_size", 14));
        seekBarOpacity.setProgress(prefs.getInt("overlay_opacity", 80));

        sizeValue.setText(String.valueOf(seekBarSize.getProgress()));
        opacityValue.setText(seekBarOpacity.getProgress() + "%");

        checkBoxFPS.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("show_fps", isChecked).apply();
        });

        checkBoxRAM.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("show_ram", isChecked).apply();
        });

        seekBarSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sizeValue.setText(String.valueOf(progress));
                prefs.edit().putInt("overlay_size", progress).apply();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBarOpacity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                opacityValue.setText(progress + "%");
                prefs.edit().putInt("overlay_opacity", progress).apply();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
}
