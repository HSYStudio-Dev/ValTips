package com.hsystudio.valtips.feature.login.model

import androidx.annotation.DrawableRes
import com.hsystudio.valtips.R

data class OnboardingPage(
    @DrawableRes val imageRes: Int
)

// Todo: 임시 이미지 - 추후 수정
val onboardingPages = listOf(
    OnboardingPage(R.drawable.onboarding_1),
    OnboardingPage(R.drawable.onboarding_1),
    OnboardingPage(R.drawable.onboarding_1),
    OnboardingPage(R.drawable.onboarding_1),
)
