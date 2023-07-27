// Copyright (c) 2023 Matthias Piepke
//
// Redistribution and use under the terms of The "BSD New" License,
// which can be found in the LICENSE file, herein included as part of this header.

package com.example.watchstratux

object DefaultPreferences {
    var ip_1: AppPreference = AppPreference("IP_1_KEY", 192)
    var ip_2: AppPreference = AppPreference("IP_2_KEY", 168)
    var ip_3: AppPreference = AppPreference("IP_3_KEY", 178)
    var ip_4: AppPreference = AppPreference("IP_4_KEY", 53)
    var ip_port: AppPreference = AppPreference("IP_PORT_KEY", 10000)

    var lower_vertical_limit: AppPreference = AppPreference("LOWER_VERT_LIMIT_KEY", 0)
    var upper_vertical_limit: AppPreference = AppPreference("UPPER_VERT_LIMIT_KEY", 0)

    var show_tracks: AppPreference = AppPreference("SHOW_TRACKS_KEY", 1)            // 1 show, 0 don't show
    var vibration_alarm: AppPreference = AppPreference("ALARM_VIBRATION_KEY", 1)    // 1 vibrate, 0 don't vibrate at alert
    var distance_in_km: AppPreference = AppPreference("DISTANCE_IN_KM_KEY", 1)      // 1 in KM, 0 in NM
    var altitude_in_ft: AppPreference = AppPreference("ALTITUDE_IN_FT_KEY", 1)      // 1 in Feet, 0 in Meter

    var defaultPreferences: Array<AppPreference> =
        arrayOf<AppPreference>(
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