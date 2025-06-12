package com.eneko.gastospersonalesaplicacion.activities;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.eneko.gastospersonalesaplicacion.R;

import java.util.Locale;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        ListPreference idiomaPref = findPreference("idioma");
        if (idiomaPref != null) {
            idiomaPref.setOnPreferenceChangeListener((preference, newValue) -> {
                cambiarIdioma(newValue.toString());
                return true;
            });
        }
    }

    private void cambiarIdioma(String idioma) {
        Locale nuevaLocale = new Locale(idioma);
        Locale.setDefault(nuevaLocale);

        Configuration config = new Configuration();
        config.setLocale(nuevaLocale);

        requireActivity().getBaseContext().getResources().updateConfiguration(
                config,
                requireActivity().getBaseContext().getResources().getDisplayMetrics()
        );

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        editor.putString("idioma", idioma);
        editor.apply();

        requireActivity().recreate();
    }
}
