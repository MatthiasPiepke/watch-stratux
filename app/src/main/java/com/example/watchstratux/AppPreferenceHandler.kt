// Copyright (c) 2023 Matthias Piepke
//
// Redistribution and use under the terms of The "BSD New" License,
// which can be found in the LICENSE file, herein included as part of this header.

package com.example.watchstratux

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class AppPreferenceHandler(context: Context, preferenceFile: String) {
    private var appPreferences: SharedPreferences

    init {
        appPreferences = context.getSharedPreferences(preferenceFile, Context.MODE_PRIVATE)
        if (appPreferences.getInt("Init_KEY", 0) == 1) {
            if(BuildConfig.DEBUG) Log.i("AppPreferenceHandler:", "Init_KEY found ...")
        } else {
            if(BuildConfig.DEBUG) Log.i("AppPreferenceHandler:", "Init_KEY not found ...")
            if (savePreference(AppPreference("Init_KEY", 1))) {
                Log.i("AppPreferenceHandler:", "Init_KEY created ...")
                for (pref in DefaultPreferences.defaultPreferences) {
                    savePreference(pref)
                }
                if(BuildConfig.DEBUG) Log.i("AppPreferenceHandler:", "Default Preferences stored")
            }
        }
    }

    fun clearAllPrefences(): Boolean {
        val cleared = appPreferences.edit().clear().commit()
        if (cleared == true)
        {
            if(BuildConfig.DEBUG) Log.i("AppPreferenceHandler:", "Clear OKAY")
        } else if(BuildConfig.DEBUG) Log.i("AppPreferenceHandler:", "Clear FAILED")
        return cleared
    }

    fun <T>loadPreference(pref: AppPreference<T>): Boolean {
        var loadIssue = false

        when( pref.value as Any) {
            is Int -> {
                var value = appPreferences.getInt(pref.key, -1)
                if (value != -1) {
                    pref.value = value as T
                    if(BuildConfig.DEBUG) Log.i("AppPreferenceHandler:", pref.key + " load " + pref.value)
                } else {
                    if(BuildConfig.DEBUG) Log.i("AppPreferenceHandler:", pref.key + " load operation FAILED")
                    loadIssue = true
                }
            }
            is String -> {
                var value = appPreferences.getString(pref.key, "-1")
                if (value != "-1") {
                    pref.value = value as T
                    if(BuildConfig.DEBUG) Log.i("AppPreferenceHandler:", pref.key + " load " + pref.value)
                } else {
                    if(BuildConfig.DEBUG) Log.i("AppPreferenceHandler:", pref.key + " load operation FAILED")
                    loadIssue = true
                }
            }
            is Boolean -> {
                var value = appPreferences.getBoolean(pref.key, false)
                if(BuildConfig.DEBUG) Log.i("AppPreferenceHandler:", pref.key + " load " + pref.value)
            }
        }
        return !loadIssue
    }

    fun <T>savePreference(pref: AppPreference<T>): Boolean {
        var saveIssue = false

        when( pref.value as Any) {
            is Int -> {
                if (appPreferences.edit().putInt(pref.key, pref.value as Int).commit() == true) {
                    if(BuildConfig.DEBUG) Log.i("AppPreferenceHandler:", pref.key + " saved " + pref.value)
                } else {
                    if(BuildConfig.DEBUG) Log.i("AppPreferenceHandler:", pref.key + " save operation FAILED")
                    saveIssue = true
                }
            }
            is String -> {
                if (appPreferences.edit().putString(pref.key, pref.value as String).commit() == true) {
                    if(BuildConfig.DEBUG) Log.i("AppPreferenceHandler:", pref.key + " saved " + pref.value)
                } else {
                    if(BuildConfig.DEBUG) Log.i("AppPreferenceHandler:", pref.key + " save operation FAILED")
                    saveIssue = true
                }
            }
            is Boolean -> {
                if (appPreferences.edit().putBoolean(pref.key, pref.value as Boolean).commit() == true) {
                    if(BuildConfig.DEBUG) Log.i("AppPreferenceHandler:", pref.key + " saved " + pref.value)
                } else {
                    if(BuildConfig.DEBUG) Log.i("AppPreferenceHandler:", pref.key + " save operation FAILED")
                    saveIssue = true
                }
            }
        }

        return !saveIssue
    }
/*
    fun clearPreference(pref: AppPreference): Boolean {
        var clearIssue = false
        if (appPreferences.edit().remove(pref.key).commit() == true) {
            if(BuildConfig.DEBUG) Log.i("AppPreferenceHandler:", pref.key + " cleared " + pref.value)
        } else {
            if(BuildConfig.DEBUG) Log.i("AppPreferenceHandler:", pref.key + "clear operation FAILED")
            clearIssue = true
        }
        return !clearIssue
    }
*/
    fun loadPreferences(prefArray: Array<out AppPreference<out Any>>): Boolean {
        var loadIssue = false
        for (pref in prefArray) {
            if (loadPreference(pref) == false) loadIssue = true
        }
        return !loadIssue
    }

}