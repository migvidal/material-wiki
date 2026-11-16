package com.migvidal.materialwiki.core.ui

import java.time.Instant

interface DataResponse <T>{
    val data: T
    val lastUpdatedAt: Instant
    val status: RequestStatus
}

sealed interface RequestStatus {
    data object Success : RequestStatus
    data object Loading : RequestStatus
    data class Failure(val message: String = "Unkown error", val throwable: Throwable? = null) : RequestStatus
}