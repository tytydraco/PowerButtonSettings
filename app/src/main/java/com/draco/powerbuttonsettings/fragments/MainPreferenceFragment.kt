package com.draco.powerbuttonsettings.fragments

import android.content.*
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.preference.*
import com.draco.powerbuttonsettings.R
import com.draco.powerbuttonsettings.repositories.constants.SettingsConstants
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.snackbar.Snackbar

class MainPreferenceFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var longPress: ListPreference
    private lateinit var veryLongPress: ListPreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main, rootKey)
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        longPress = findPreference(getString(R.string.pref_long_press_key))!!
        veryLongPress = findPreference(getString(R.string.pref_very_long_press_key))!!

        refreshSettings()
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        when (preference.key) {
            getString(R.string.pref_developer_key) -> openURL(getString(R.string.developer_url))
            getString(R.string.pref_source_key) -> openURL(getString(R.string.source_url))
            getString(R.string.pref_contact_key) -> openURL(getString(R.string.contact_url))
            getString(R.string.pref_licenses_key) -> {
                val intent = Intent(requireContext(), OssLicensesMenuActivity::class.java)
                startActivity(intent)
            }
            else -> return super.onPreferenceTreeClick(preference)
        }
        return true
    }

    /**
     * Update the UI to show the new constants
     */
    private fun refreshSettings() {
        longPress.value = Settings.Global.getString(requireContext().contentResolver, SettingsConstants.POWER_BUTTON_LONG_PRESS) ?: "null"
        veryLongPress.value = Settings.Global.getString(requireContext().contentResolver, SettingsConstants.POWER_BUTTON_VERY_LONG_PRESS) ?: "null"
    }

    /**
     * Take the UI settings and apply them as constants
     */
    private fun applySettings() {
        Settings.Global.putString(requireContext().contentResolver, SettingsConstants.POWER_BUTTON_LONG_PRESS, longPress.value)
        Settings.Global.putString(requireContext().contentResolver, SettingsConstants.POWER_BUTTON_VERY_LONG_PRESS, veryLongPress.value)
    }

    /**
     * Open a URL for the user
     */
    private fun openURL(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        try {
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Snackbar.make(requireView(), getString(R.string.snackbar_intent_failed), Snackbar.LENGTH_SHORT).show()
        }
    }

    /**
     * When settings are changed, apply the new config
     */
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        applySettings()
    }
}