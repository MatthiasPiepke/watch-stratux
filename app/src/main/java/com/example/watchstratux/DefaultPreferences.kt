// Copyright (c) 2023 Matthias Piepke
//
// Redistribution and use under the terms of The "BSD New" License,
// which can be found in the LICENSE file, herein included as part of this header.

package com.example.watchstratux

object DefaultPreferences {
    var ipAddress = AppPreference("IP_ADDRESS_KEY", BuildConfig.IP_ADDRESS)
    var ip_port = AppPreference("IP_PORT_KEY", 2000)

    var lower_vertical_limit = AppPreference("LOWER_VERT_LIMIT_KEY", 0)
    var upper_vertical_limit = AppPreference("UPPER_VERT_LIMIT_KEY", 0)

    var show_tracks = AppPreference("SHOW_TRACKS_KEY", true)
    var vibration_alarm = AppPreference("ALARM_VIBRATION_KEY", true)
    var distance_in_km = AppPreference("DISTANCE_IN_KM_KEY", 1)      // 1 in KM, 0 in NM
    var altitude_in_ft = AppPreference("ALTITUDE_IN_FT_KEY", 1)      // 1 in Feet, 0 in Meter

    var defaultPreferences =
        arrayOf(
            ipAddress,
            ip_port,
            lower_vertical_limit,
            upper_vertical_limit,
            show_tracks,
            vibration_alarm,
            distance_in_km,
            altitude_in_ft
        )
}