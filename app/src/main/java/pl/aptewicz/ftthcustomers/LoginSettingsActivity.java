package pl.aptewicz.ftthcustomers;

import android.os.Bundle;
import android.preference.PreferenceActivity;


public class LoginSettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.login_settings);
    }
}
