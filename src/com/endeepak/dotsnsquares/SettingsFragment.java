package com.endeepak.dotsnsquares;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import com.endeepak.dotsnsquares.domain.BotDrawingSpeed;
import com.endeepak.dotsnsquares.domain.PlayerTurn;
import com.endeepak.dotsnsquares.util.EnumUtil;
import com.endeepak.dotsnsquares.domain.GameOptions;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.settings);
        bindPreferences();
        configureListPreference(R.string.bot_drawing_speed_preference_key, EnumUtil.getNames(BotDrawingSpeed.class), EnumUtil.getNames(BotDrawingSpeed.class));
        configureListPreference(R.string.player_turn_preference_key, EnumUtil.getStrings(PlayerTurn.class), EnumUtil.getNames(PlayerTurn.class));
        configureResetSettings();
    }

    private void configureResetSettings() {
        Preference resetSettings = findPreference("reset_settings");
        resetSettings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                ConfirmationDialog.show(getActivity(), getString(R.string.confirm_reset_settings), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        resetSettings();
                    }
                });
                return true;
            }
        });
    }

    private void resetSettings() {
        GameOptions.resetPreferences(getPreferenceScreen().getSharedPreferences(), getResources());
        restartActivity();
    }

    private void restartActivity() {
        getActivity().finish();
        getActivity().startActivity(getActivity().getIntent());
    }

    private void configureListPreference(int preferenceKey, String[] entries, String[] entryValues) {
        ListPreference listPreference = (ListPreference) findPreference(getResources().getString(preferenceKey));
        listPreference.setEntries(entries);
        listPreference.setEntryValues(entryValues);
    }

    private void bindPreferences() {
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        for (String key : sharedPreferences.getAll().keySet()) {
            setTitle(key);
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        setTitle(key);
    }

    private void setTitle(String key) {
        Preference pref = findPreference(key);
        if (pref instanceof EditTextPreference) {
            EditTextPreference editTextPreference = (EditTextPreference) pref;
            pref.setTitle(editTextPreference.getText());
        }
        else if (pref instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) pref;
            String value = listPreference.getValue();
            if(key.equals(getResources().getString(R.string.player_turn_preference_key))) {
                pref.setTitle(EnumUtil.valueOrDefault(value, GameOptions.Defaults.PLAYER_TURN).toString());
            } else {
                pref.setTitle(value);
            }
        }
    }

}
