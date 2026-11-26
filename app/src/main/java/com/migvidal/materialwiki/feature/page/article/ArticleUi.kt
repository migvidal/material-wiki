package com.migvidal.materialwiki.feature.page.article

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.migvidal.materialwiki.R
import com.migvidal.materialwiki.core.network.api.common_model.ImageDescriptor
import com.migvidal.materialwiki.core.ui.components.CustomAsyncImage
import com.migvidal.materialwiki.core.ui.components.CustomText
import com.migvidal.materialwiki.core.ui.components.MostReadInfo
import com.migvidal.materialwiki.core.ui.model.IddPageDto
import com.migvidal.materialwiki.core.ui.model.PageNavDto
import com.migvidal.materialwiki.core.ui.model.RequestStatus
import com.migvidal.materialwiki.core.ui.model.TitledPageDto

@Composable
fun ArticleUi(
    pageNavDto: PageNavDto,
    modifier: Modifier = Modifier,
    eventSink: (ArticleEvent) -> Unit,
) {
    Surface(modifier = modifier) {
        val key = when (pageNavDto) {
            is IddPageDto -> pageNavDto.pageId.toString()
            is TitledPageDto -> pageNavDto.titles.canonical
        }
        val viewModel = hiltViewModel<ArticleViewModel, ArticleViewModel.Factory>(
            key = key,
            creationCallback = { factory ->
                factory.create(pageNavDto)
            }
        )
        val state = viewModel.state.collectAsStateWithLifecycle(initialValue = ArticleState()).value
        val connected = state.connected
        val status = state.response?.status
        when {
            !connected -> Text(text = stringResource(R.string.no_internet))
            status is RequestStatus.Failure -> Text(text = status.message)
            else -> ArticleBody(state = state, eventSink = eventSink)
        }
    }
}

@Composable
private fun ArticleBody(
    state: ArticleState,
    eventSink: (ArticleEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        val response = state.response

        state.mostReadInfo?.let {
            MostReadInfo(mostRead = it, isLoading = false)
        }

        val article = response?.data
        val summary = article?.summary

        CustomText(
            textOrNull = summary,
            isLoading = false,
            fontWeight = FontWeight.Bold,
        )

        val status = response?.status

        ImagesSection(
            isLoading = status is RequestStatus.Loading,
            article = response?.data,
            mainImage = state.mainImage,
            onImageClick = { eventSink(ArticleEvent.ImageClicked(it)) },
        )
    }
}

@Composable
private fun ImagesSection(
    isLoading: Boolean,
    article: Article?,
    mainImage: ImageDescriptor?,
    onImageClick: (ImageDescriptor) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        val gridState = rememberLazyStaggeredGridState()

        val mainImgAspectRatio by animateFloatAsState(
            targetValue = when {
                gridState.canScrollBackward -> 8f
                !gridState.canScrollBackward && gridState.isScrollInProgress -> 16 / 10f
                else -> 16 / 9f
            }
        )

        val mainImageRadius by animateDpAsState(
            targetValue = when {
                gridState.canScrollBackward -> 800.dp
                else -> 0.dp
            }
        )

        CustomAsyncImage(
            modifier = Modifier
                .clip(RoundedCornerShape(mainImageRadius))
                .clickable {
                    onImageClick(mainImage ?: return@clickable)
                },
            urlOrNull = mainImage?.url,
            isDataLoading = false,
            aspectRatio = mainImgAspectRatio,
        )

        val images = article?.images
        LazyVerticalStaggeredGrid(
            modifier = Modifier.fillMaxWidth(),
            columns = StaggeredGridCells.Fixed(2),
            state = gridState,
        ) {
            val default = 5
            items(count = images?.size ?: default) { index ->
                val img = images?.get(index) ?: return@items
                CustomAsyncImage(modifier = Modifier.clickable {
                    onImageClick(img)
                }, urlOrNull = img.url, isDataLoading = isLoading)
            }
        }
    }
}