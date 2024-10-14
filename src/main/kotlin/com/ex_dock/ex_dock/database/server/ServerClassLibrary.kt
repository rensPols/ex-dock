package com.ex_dock.ex_dock.database.server

data class ServerDataData(val key: String, var value: String)

data class ServerVersionData(val major: Int, val minor: Int, val patch: Int,
                             var versionName: String, var versionDescription: String)
