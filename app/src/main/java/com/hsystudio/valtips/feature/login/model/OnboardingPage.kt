package com.hsystudio.valtips.feature.login.model

import androidx.annotation.DrawableRes
import com.hsystudio.valtips.R

data class OnboardingPage(
    @DrawableRes val imageRes: Int
)

val onboardingPages = listOf(
    OnboardingPage(R.drawable.onboarding_1),
    OnboardingPage(R.drawable.onboarding_2),
    OnboardingPage(R.drawable.onboarding_3),
    OnboardingPage(R.drawable.onboarding_4),
)
