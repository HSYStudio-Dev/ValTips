package com.hsystudio.valtips.navigation

object Route {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"

    const val HOME = "home"

    const val AGENT = "agent"
    const val AGENT_DETAIL = "agent_detail/{agentUuid}"
    const val MAP_SELECT = "map_select/{agentUuid}"

    const val MAP = "map"
    const val MAP_DETAIL = "map_detail/{mapUuid}"
    const val AGENT_SELECT = "agent_select/{mapUuid}"

    const val SETTING = "setting"
}
