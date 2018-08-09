package kashish.com.ui.Fragments

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import kashish.com.R

/**
 * Created by Kashish on 09-08-2018.
 */
class SettingsFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_settings)
    }
}