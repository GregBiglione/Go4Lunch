package com.greg.go4lunch;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.Locale;

public class LanguagesDialog extends AppCompatDialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        loadLocale();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.select_language))
                .setSingleChoiceItems(R.array.country_array, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0){
                            setLocale("fr");
                            changeLang();
                        }
                        else if (which == 1){
                            setLocale("en");
                            changeLang();
                        }
                        else if (which == 2){
                            setLocale("it");
                            changeLang();
                        }
                        else if (which == 3){
                            setLocale("es");
                            getActivity().recreate();
                        }
                        else if (which == 4){
                            setLocale("pt");
                            getActivity().recreate();
                        }
                        else if (which == 5){
                            setLocale("ru");
                            getActivity().recreate();
                        }

                        dismiss();
                    }
                });

        return builder.create();
    }

    // ---------------------------- Language selection ---------------------------------------------
    private void setLocale(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getContext().getResources().updateConfiguration(configuration, getContext().getResources().getDisplayMetrics());

        // ---------------------------- Save language  ---------------------------------------------
        SharedPreferences.Editor editor = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE).edit();
        editor.putString("My language", language);
        editor.apply();
    }

    // ---------------------------- Load language  -------------------------------------------------
    private void loadLocale(){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        String lang = sharedPreferences.getString("My language", "");
        setLocale(lang);
    }

    private void changeLang(){
        getActivity().recreate();
    }
}
