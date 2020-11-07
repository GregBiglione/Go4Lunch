package com.greg.go4lunch.ui.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.greg.go4lunch.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

public class SettingActivity extends AppCompatActivity {

    public static final String NOTIFICATIONS_PREF = "Notifications preferences";
    @BindView(R.id.setting_check_box) CheckBox mCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        clickOnSettingCheckBox();
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Click on checkbox ----------------------------------------------
    //----------------------------------------------------------------------------------------------

    @OnClick(R.id.setting_check_box)
    void clickOnSettingCheckBox(){
        if (mCheckBox.isChecked()){
            Toasty.success(this, getString(R.string.notifications_on), Toasty.LENGTH_SHORT).show();
            restorePreferences();
            mCheckBox.isChecked();
        }
        else{
            Toasty.warning(this, getString(R.string.notifications_off), Toasty.LENGTH_SHORT).show();
            upDateSharedPreferences();
        }
    }
    //@OnClick(R.id.setting_check_box)
    //void clickOnSettingCheckBox(){
    //   mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    //       @Override
    //       public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    //           if (buttonView.isChecked()){
    //               Toasty.success(SettingActivity.this, getString(R.string.notifications_on), Toasty.LENGTH_SHORT).show();
    //               restorePreferences();
    //           }
    //           else{
    //               Toasty.warning(SettingActivity.this, getString(R.string.notifications_off), Toasty.LENGTH_SHORT).show();
    //               upDateSharedPreferences();
    //               //mCheckBox.setChecked(false);
    //           }
    //       }
    //   });
    //}

    //----------------------------------------------------------------------------------------------
    //----------------------------- Shared preferences ---------------------------------------------
    //----------------------------------------------------------------------------------------------

    private void upDateSharedPreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences(NOTIFICATIONS_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("silentMode", false);
        editor.commit();
    }

    public void restorePreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences(NOTIFICATIONS_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("silentMode", true);
        editor.commit();

    }


    //----------------------------------------------------------------------------------------------
    //----------------------------- Back to Main Activity ------------------------------------------
    //----------------------------------------------------------------------------------------------

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
