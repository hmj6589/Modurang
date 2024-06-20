package com.example.projectapplication

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.preference.PreferenceFragmentCompat

class MySettingsFragment : PreferenceFragmentCompat() {

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // 레이아웃이 아니라 R(리소스)
        setPreferencesFromResource(R.xml.root_preferences, rootKey)


    }
}