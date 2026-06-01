package com.robingebert.boxy.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Asset(val id: Long, val name: String, val parentId: Long?)
