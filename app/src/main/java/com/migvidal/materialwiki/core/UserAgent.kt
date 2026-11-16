package com.migvidal.materialwiki.core

import android.content.Context
import android.os.Build
import com.migvidal.materialwiki.BuildConfig
import com.migvidal.materialwiki.R

fun getUserAgent(context: Context): String {
    val appName = context.getString(R.string.app_name)
    return "$appName/${BuildConfig.VERSION_NAME} (Android ${Build.VERSION.RELEASE}; ${Build.MODEL} Build/${Build.ID})"
}

const val USER_AGENT_KEY = "User-Agent"