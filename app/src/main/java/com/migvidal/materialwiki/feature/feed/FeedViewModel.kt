package com.migvidal.materialwiki.feature.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.migvidal.materialwiki.core.network.NetworkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val feedProvider: FeedProvider,
    networkManager: NetworkManager,
) : ViewModel() {
    val feedState = combine(
        flow = feedProvider.response,
        flow2 = networkManager.isConnected
    ) { response, connected ->
        FeedState(response = response, connected = connected)
    }

    fun fetchFeed() {
        viewModelScope.launch { feedProvider.fetchFeed() }
    }

    init {
        fetchFeed()
    }
}

data class FeedState(
    val response: FeedResponse,
    val connected: Boolean,
)