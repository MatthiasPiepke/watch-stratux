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
    private var isAppPreferenceFileNew = false

    init {
        appPreferences = context.getSharedPreferences(preferenceFile, Context.MODE_PRIVATE)
        if (appPreferences.getInt("Init_KEY", 0) == 1) {
            isAppPreferenceFileNew = false
            if(BuildConfig.DEBUG) Log.i("AppPreferenceHandler:", "Init_KEY found ...")
        } else {
            isAppPreferenceFileNew = true
            if(BuildConfig.DEBUG) Log.i("AppPreferenceHandler:", "Init_KEY not found ...")
            if (savePreference(AppPreference("Init_KEY", 1))) {
                Log.i("AppPreferenceHandler:", "Init_KEY created ...")
            }
        }
    }

    constructor(context: Context, preferenceFile: String, defaultPreferences: Array<AppPreference>): this(context, preferenceFile){
        if ( isAppPreferenceFileNew == true) {
            if (clearAllPrefences() == true) {
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
            savePreference(AppPreference("Init_KEY", 1))
            if(BuildConfig.DEBUG) Log.i("AppPreferenceHandler:", "Clear OKAY")
        } else if(BuildConfig.DEBUG) Log.i("AppPreferenceHandler:", "Clear FAILED")
        return cleared
    }

    fun loadPreference(pref: AppPreference): Boolean {
        var loadIssue = false

        val value: Int
        value = appPreferences.getInt(pref.key, -1)

        if (value != -1) {
            pref.value = value
            if(BuildConfig.DEBUG) Log.i("AppPreferenceHandler:", pref.key + " load " + pref.value)
        } else {
            if(BuildConfig.DEBUG) Log.i("AppPreferenceHandler:", pref.key + "load operation FAILED")
            loadIssue = true
        }

        return !loadIssue
    }

    fun savePreference(pref: AppPreference): Boolean {
        var saveIssue = false
        if (appPreferences.edit().putInt(pref.key, pref.value).commit() == true) {
            if(BuildConfig.DEBUG) Log.i("AppPreferenceHandler:", pref.key + " saved " + pref.value)
        } else {
            if(BuildConfig.DEBUG) Log.i("AppPreferenceHandler:", pref.key + "save operation FAILED")
            saveIssue = true
        }
        return !saveIssue
    }

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

    fun loadPreferences(prefArray: Array<AppPreference>): Boolean {
        var loadIssue = false
        for (pref in prefArray) {
            if (loadPreference(pref) == false) loadIssue = true
        }
        return !loadIssue
    }

}