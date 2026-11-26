package com.migvidal.materialwiki.feature.page.article

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.migvidal.materialwiki.core.network.NetworkManager
import com.migvidal.materialwiki.core.network.api.common_model.ImageDescriptor
import com.migvidal.materialwiki.core.ui.components.MostRead
import com.migvidal.materialwiki.core.ui.model.IddPageDto
import com.migvidal.materialwiki.core.ui.model.ImageDto
import com.migvidal.materialwiki.core.ui.model.PageNavDto
import com.migvidal.materialwiki.core.ui.model.TitledPageDto
import com.migvidal.materialwiki.feature.page.common.PageRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel(assistedFactory = ArticleViewModel.Factory::class)
class ArticleViewModel @AssistedInject constructor(
    private val articleProvider: ArticleProvider,
    networkManager: NetworkManager,
    @Assisted private val pageNavDto: PageNavDto,
) : ViewModel() {
    val state = combine(
        flow = articleProvider.response,
        flow2 = networkManager.isConnected,
    ) { response, connected ->
        val title = when (pageNavDto) {
            is IddPageDto -> "Article"
            is TitledPageDto -> pageNavDto.titles.normalized
        }
        ArticleState(
            connected = connected,
            title = title,
            response = response,
            mainImage = pageNavDto.mainImage,
            mostReadInfo = pageNavDto.mostReadInfo,
        )
    }

    fun fetch() {
        viewModelScope.launch {
            when (pageNavDto) {
                is IddPageDto -> articleProvider.fetchById(pageNavDto.pageId)
                is TitledPageDto -> articleProvider.fetchByTitle(pageNavDto.titles.canonical)
            }
        }
    }

    init {
        fetch()
    }

    @AssistedFactory
    interface Factory {
        fun create(pageNavDto: PageNavDto): ArticleViewModel
    }
}

data class ArticleState(
    val connected: Boolean = true,
    val title: String = "Article",
    val response: ArticleResponse? = null,
    val mainImage: ImageDescriptor? = null,
    val mostReadInfo: MostRead? = null,
)