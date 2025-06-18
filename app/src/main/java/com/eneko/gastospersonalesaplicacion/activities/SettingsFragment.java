package com.eneko.gastospersonalesaplicacion.activities;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.preferences_container, new PreferenceFragmentCompat() {
                    @Override
                    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
                        setPreferencesFromResource(R.xml.preferences, rootKey);
                    }
                })
                .commit();

        Button btnAplicar = root.findViewById(R.id.btnAplicarIdioma);
        btnAplicar.setOnClickListener(v -> {
            String idioma = PreferenceManager.getDefaultSharedPreferences(getContext())
                    .getString("idioma", "es");
            cambiarIdioma(idioma);
        });

        return root;
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
