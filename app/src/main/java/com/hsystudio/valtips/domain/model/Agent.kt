package com.hsystudio.valtips.domain.model

data class Agent(
    val uuid: String,
    val displayName: String,
    val description: String,
    val displayIcon: String?,
    val fullPortrait: String?,
    val roleName: String?,
    val abilities: List<Ability>
)

data class Ability(
    val slot: String,
    val displayName: String,
    val description: String,
    val displayIcon: String?
)
