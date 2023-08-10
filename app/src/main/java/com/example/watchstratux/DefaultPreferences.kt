// Copyright (c) 2023 Matthias Piepke
//
// Redistribution and use under the terms of The "BSD New" License,
// which can be found in the LICENSE file, herein included as part of this header.

package com.example.watchstratux

object DefaultPreferences {
    var ip_1 = AppPreference<Int>("IP_1_KEY", 192)
    var ip_2 = AppPreference<Int>("IP_2_KEY", 168)
    var ip_3 = AppPreference<Int>("IP_3_KEY", BuildConfig.IP_3)
    var ip_4 = AppPreference<Int>("IP_4_KEY", BuildConfig.IP_4)
    var ip_port = AppPreference<Int>("IP_PORT_KEY", 2000)

    var lower_vertical_limit = AppPreference<Int>("LOWER_VERT_LIMIT_KEY", 0)
    var upper_vertical_limit = AppPreference<Int>("UPPER_VERT_LIMIT_KEY", 0)

    var show_tracks = AppPreference("SHOW_TRACKS_KEY", true)
    var vibration_alarm = AppPreference<Int>("ALARM_VIBRATION_KEY", 1)    // 1 vibrate, 0 don't vibrate at alert
    var distance_in_km = AppPreference<Int>("DISTANCE_IN_KM_KEY", 1)      // 1 in KM, 0 in NM
    var altitude_in_ft = AppPreference<Int>("ALTITUDE_IN_FT_KEY", 1)      // 1 in Feet, 0 in Meter

    var defaultPreferences =
        arrayOf(
            ip_1,
            ip_2,
            ip_3,
            ip_4,
            ip_port,
            lower_vertical_limit,
            upper_vertical_limit,
            show_tracks,
            vibration_alarm,
            distance_in_km,
            altitude_in_ft
        )
}