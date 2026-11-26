package com.migvidal.materialwiki.feature.page.article

import com.migvidal.materialwiki.core.network.api.common_model.ImageDescriptor
import com.migvidal.materialwiki.core.ui.model.DataResponse
import com.migvidal.materialwiki.core.ui.model.RequestStatus
import com.migvidal.materialwiki.feature.page.common.PageModel
import com.migvidal.materialwiki.feature.page.common.PageRepository
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.Instant
import javax.inject.Inject

class ArticleProvider @Inject constructor(private val pageRepository: PageRepository) {
    private val _response = MutableStateFlow(ArticleResponse())
    val response = _response.asStateFlow()

    suspend fun fetchByTitle(title: String) {
        fetch { pageRepository.getByTitle(title) }
    }

    suspend fun fetchById(id: Int) {
        fetch { pageRepository.getById(id) }
    }

    private suspend fun fetch(method: suspend () -> PageModel) {
        _response.update { ArticleResponse(status = RequestStatus.Loading) }
        runCatching { method() }
            .onSuccess { handleSuccess(it) }
            .onFailure { handleFailure(it) }
    }

    private suspend fun handleSuccess(pageModel: PageModel) {
        val page = pageModel.query.pages.firstOrNull()
        if (page == null) {
            _response.update {
                ArticleResponse(status = RequestStatus.Failure())
            }
            return
        }
        val imagesData = page.images?.map { getImage(it) } ?: emptyList()
        val images = imagesData.mapNotNull {
            val img = it.query.pages.firstOrNull()?.imageInfo?.firstOrNull()
                ?: return@mapNotNull null
            img.toDescriptor()
        }

        _response.update {
            ArticleResponse(
                data = Article(
                    title = page.title,
                    summary = page.pageprops?.wikibaseShortDesc ?: "",
                    images = images,
                ),
            )
        }    }

    private suspend fun getImage(imageRef: PageModel.PageWithPropsAndImages.ImageReference) =
        pageRepository.getByTitle(imageRef.title)

    private fun handleFailure(throwable: Throwable) {
        if (throwable is CancellationException) throw throwable
        _response.update {
            ArticleResponse(
                status = RequestStatus.Failure(
                    message = "Could not load article",
                    throwable = throwable,
                )
            )
        }
    }
}

data class ArticleResponse(
    override val data: Article? = null,
    override val lastUpdatedAt: Instant = Instant.now(),
    override val status: RequestStatus = RequestStatus.Success
) : DataResponse<Article?>