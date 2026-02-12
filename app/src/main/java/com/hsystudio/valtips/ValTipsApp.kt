package com.hsystudio.valtips

import android.app.Application
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ValTipsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Google Mobile Ads SDK 초기화
        MobileAds.initialize(this)
    }
}
