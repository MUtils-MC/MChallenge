package de.miraculixx.mutils.module.perspective

import kotlinx.serialization.Serializable

@Serializable
data class GameProfileData(val id: String, val name: String, val properties: List<ProfileSkinData>)

@Serializable
data class ProfileSkinData(val name: String, val value: String, val signature: String)