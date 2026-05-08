package com.robingebert.boxy.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Location(val id: Long, val name: String, val parentId: Long?, val picture: String?, val updated: String)

@Serializable
data class LocationNode(
    val location: Location,
    val children: List<LocationNode>
)