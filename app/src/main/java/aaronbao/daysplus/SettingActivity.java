package aaronbao.daysplus;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;

public class SettingActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private RadioButton rbGlobal, rbWinnipeg,
                        rb3, rb5, rb10,
                        rbSmall, rbMedium, rbLarge;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");

        sharedPreferences = getSharedPreferences("mainPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        rbGlobal = (RadioButton)findViewById(R.id.rbGlobal);
        rbWinnipeg = (RadioButton)findViewById(R.id.rbWinnipeg);

        rb3 = (RadioButton)findViewById(R.id.rb3);
        rb5 = (RadioButton)findViewById(R.id.rb5);
        rb10 = (RadioButton)findViewById(R.id.rb10);

        rbSmall = (RadioButton)findViewById(R.id.rbSmall);
        rbMedium = (RadioButton)findViewById(R.id.rbMedium);
        rbLarge = (RadioButton)findViewById(R.id.rbLarge);

        if (sharedPreferences.getBoolean("is_global", true)) {
            rbGlobal.setChecked(true);
        } else if (sharedPreferences.getBoolean("is_winnipeg", true)) {
            rbWinnipeg.setChecked(true);
        }

        if (sharedPreferences.getBoolean("is_3", true)){
            rb3.setChecked(true);
        } else if (sharedPreferences.getBoolean("is_5", true)){
            rb5.setChecked(true);
        } else if (sharedPreferences.getBoolean("is_10", true)) {
            rb10.setChecked(true);
        }

        if (sharedPreferences.getBoolean("is_large", true)){
            rbLarge.setChecked(true);
        } else if (sharedPreferences.getBoolean("is_medium", true)) {
            rbMedium.setChecked(true);
        } else if (sharedPreferences.getBoolean("is_small", true)) {
            rbSmall.setChecked(true);
        }
    }

    public void onRadioButtonClicked(View v) {
        sharedPreferences = getSharedPreferences("mainPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        boolean checked = ((RadioButton) v).isChecked();

        switch (v.getId()) {
            //Category
            case R.id.rbGlobal:
                if (checked) {
                    editor.putBoolean("is_global", true);
                    editor.putBoolean("is_winnipeg", false);
                    editor.commit();
                }
                break;
            case R.id.rbWinnipeg:
                if (checked) {
                    editor.putBoolean("is_global", false);
                    editor.putBoolean("is_winnipeg", true);
                    editor.commit();
                }
                break;

            //Number of items to display
            case R.id.rb3:
                if (checked) {
                    editor.putBoolean("is_3", true);
                    editor.putBoolean("is_5", false);
                    editor.putBoolean("is_10", false);
                    editor.commit();
                }
                break;
            case R.id.rb5:
                if (checked) {
                    editor.putBoolean("is_3", false);
                    editor.putBoolean("is_5", true);
                    editor.putBoolean("is_10", false);
                    editor.commit();
                }
                break;
            case R.id.rb10:
                if (checked) {
                    editor.putBoolean("is_3", false);
                    editor.putBoolean("is_5", false);
                    editor.putBoolean("is_10", true);
                    editor.commit();
                }
                break;

            //Font Size
            case R.id.rbSmall:
                if (checked) {
                    editor.putBoolean("is_small", true);
                    editor.putBoolean("is_medium", false);
                    editor.putBoolean("is_large", false);
                    editor.commit();
                }
                break;
            case R.id.rbMedium:
                if (checked) {
                    editor.putBoolean("is_small", false);
                    editor.putBoolean("is_medium", true);
                    editor.putBoolean("is_large", false);
                    editor.commit();
                }
                break;
            case R.id.rbLarge:
                if (checked) {
                    editor.putBoolean("is_small", false);
                    editor.putBoolean("is_medium", false);
                    editor.putBoolean("is_large", true);
                    editor.commit();
                }
                break;
        }
    }
}
