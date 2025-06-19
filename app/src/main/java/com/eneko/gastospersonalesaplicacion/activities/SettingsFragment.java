package com.eneko.gastospersonalesaplicacion.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.eneko.gastospersonalesaplicacion.R;

import java.util.Locale;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        // Tema claro / oscuro
        ListPreference temaPref = findPreference("tema");
        if (temaPref != null) {
            temaPref.setOnPreferenceChangeListener((preference, newValue) -> {
                aplicarTema(newValue.toString());
                return true;
            });
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnAplicar = view.findViewById(R.id.btnAplicarIdioma);
        if (btnAplicar != null) {
            btnAplicar.setOnClickListener(v -> {
                String idioma = PreferenceManager.getDefaultSharedPreferences(getContext())
                        .getString("idioma", "es");
                cambiarIdioma(idioma);
                requireActivity().setResult(Activity.RESULT_OK);
                requireActivity().finish();
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

    private void aplicarTema(String valor) {
        if ("oscuro".equals(valor)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        requireActivity().recreate();
    }

}
