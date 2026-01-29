package com.hsystudio.valtips.ui.component.ad

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.hsystudio.valtips.R

@SuppressLint("InflateParams")
@Composable
fun NativeAdBanner(
    nativeAd: NativeAd,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsStateWithLifecycle()
    val isResumed = lifecycleState == Lifecycle.State.RESUMED

    Box(modifier = modifier) {
        // 실제 광고 뷰
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                // XML 레이아웃 inflate
                val adView = LayoutInflater.from(context)
                    .inflate(R.layout.ad_native_banner, null) as NativeAdView

                // 각 뷰 연결
                val headlineView = adView.findViewById<TextView>(R.id.ad_headline)
                val bodyView = adView.findViewById<TextView>(R.id.ad_body)
                val iconView = adView.findViewById<ImageView>(R.id.ad_app_icon)
                val ctaView = adView.findViewById<Button>(R.id.ad_call_to_action)

                adView.headlineView = headlineView
                adView.bodyView = bodyView
                adView.iconView = iconView
                adView.callToActionView = ctaView

                adView
            },
            update = { adView ->
                // 데이터 바인딩
                adView.setNativeAd(nativeAd)

                // 제목 설정
                (adView.headlineView as? TextView)?.text = nativeAd.headline

                // 본문 설정
                (adView.bodyView as? TextView)?.text = nativeAd.body

                // 아이콘 설정
                val icon = nativeAd.icon
                if (icon != null) {
                    adView.iconView?.visibility = View.VISIBLE
                    (adView.iconView as? ImageView)?.setImageDrawable(icon.drawable)
                } else {
                    adView.iconView?.visibility = View.GONE
                }

                // 버튼 설정
                if (nativeAd.callToAction != null) {
                    adView.callToActionView?.visibility = View.VISIBLE
                    (adView.callToActionView as? Button)?.text = nativeAd.callToAction
                } else {
                    adView.callToActionView?.visibility = View.INVISIBLE
                }
            }
        )

        // 터치 방지용 투명 박스
        if (!isResumed) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        // 클릭 무시
                    }
            )
        }
    }
}
