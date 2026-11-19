package com.migvidal.materialwiki.feature.feed

import android.util.Log
import com.migvidal.materialwiki.core.network.api.api_service.ApiService
import com.migvidal.materialwiki.core.ui.DataResponse
import com.migvidal.materialwiki.core.ui.RequestStatus
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedProvider @Inject constructor(val api: ApiService) {
    private val _response = MutableStateFlow(FeedResponse())
    val response = _response.asStateFlow()

    suspend fun fetchFeed() {
        _response.update {
            it.copy(status = if (it.data == null) RequestStatus.Loading else it.status)
        }
        runCatching { api.getFeed(LocalDate.now()) }
            .onSuccess { data ->
                _response.update {
                    FeedResponse(data = data)
                }
            }
            .onFailure { throwable ->
                val message = "Error fetching feed"
                Log.e("FeedRepository", message, throwable)
                if (throwable is CancellationException) throw throwable
                _response.update {
                    it.copy(
                        status = RequestStatus.Failure(
                            throwable = throwable,
                            message = message,
                        )
                    )
                }
            }
    }
}

data class FeedResponse(
    override val data: FeedModel? = null,
    override val lastUpdatedAt: Instant = Instant.now(),
    override val status: RequestStatus = RequestStatus.Success,
): DataResponse<FeedModel?>