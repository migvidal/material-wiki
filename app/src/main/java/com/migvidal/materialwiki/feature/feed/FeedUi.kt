package com.migvidal.materialwiki.feature.feed

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.migvidal.materialwiki.R
import com.migvidal.materialwiki.core.ui.components.CardWithImage
import com.migvidal.materialwiki.core.ui.components.CustomAsyncImage
import com.migvidal.materialwiki.core.ui.components.CustomCarouselLayoutCard
import com.migvidal.materialwiki.core.ui.components.CustomElevatedCard
import com.migvidal.materialwiki.core.ui.components.CustomPreHeading
import com.migvidal.materialwiki.core.ui.components.CustomText
import com.migvidal.materialwiki.core.ui.components.MostRead
import com.migvidal.materialwiki.core.ui.components.MostReadInfo
import com.migvidal.materialwiki.core.ui.model.RequestStatus

@Composable
fun FeedUi(
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = hiltViewModel(),
    eventSink: (FeedEvent) -> Unit,
) {
    val state = viewModel.feedState.collectAsStateWithLifecycle(null).value ?: return
    val status = state.response.status
    PullToRefreshBox(
        isRefreshing = status is RequestStatus.Loading,
        onRefresh = viewModel::fetchFeed,
    ) {
        Column(modifier = modifier) {
            val connected = state.connected
            when {
                !connected -> Text(stringResource(R.string.no_internet))
                status is RequestStatus.Failure -> Text(text = status.message)
                else -> FeedBody(state = state, eventSink = eventSink)
            }
        }
    }
}

@Composable
private fun FeedBody(
    state: FeedState,
    eventSink: (FeedEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val response = state.response
    val status = response.status
    val isLoading = status is RequestStatus.Loading
    val feed = response.data
    LazyColumn(modifier = modifier) {

        val cardPadding = Modifier.padding(vertical = 8.dp)

        stickyHeader {
            Text(
                text = "Today",
                style = MaterialTheme.typography.displaySmall,
            )
        }
        item {
            val img = feed?.image
            ImageOfTheDay(
                modifier = Modifier.then(cardPadding),
                imageModel = img,
                isLoading = isLoading,
                onClick = {
                    val imgDescriptor = img?.image?.toDescriptor() ?: return@ImageOfTheDay
                    val event = FeedEvent.ImageClicked(imgDescriptor)
                    eventSink(event)
                })
        }
        item {
            val featured = feed?.featuredArticle
            Featured(
                modifier = Modifier
                    .then(cardPadding)
                    .padding(horizontal = 16.dp),
                featuredArticle = featured,
                isLoading = isLoading,
                onClick = {
                    val imgDescriptor = featured?.originalimage?.toDescriptor() ?: return@Featured
                    val event = FeedEvent.ItemClicked(titles = featured.titles, mainImage = imgDescriptor)
                    eventSink(event)
                })
        }
        item {
            val mostRead = feed?.mostread
            MostReadSection(
                modifier = Modifier.then(cardPadding),
                mostRead = mostRead,
                isLoading = isLoading,
                onItemClicked = { item ->
                    val imgDescriptor = item.originalimage?.toDescriptor() ?: return@MostReadSection
                    val mostReadInfo = MostRead(rank = item.rank, views = item.views)
                    val event = FeedEvent.ItemClicked(titles = item.titles, mainImage = imgDescriptor, mostRead = mostReadInfo)
                    eventSink(event)
                })
        }
        stickyHeader {
            Text(
                text = "On this day",
                style = MaterialTheme.typography.displaySmall,
            )
        }
        val onThisDay = feed?.onThisDay
        items(count = onThisDay?.size ?: 3) {
            val item = onThisDay?.get(it)
            OnThisDayItem(
                modifier = Modifier.then(cardPadding),
                onThisDay = item,
                isLoading = isLoading,
                onitemClicked = { item ->
                    val imgDescriptor = item.originalimage?.toDescriptor() ?: return@OnThisDayItem
                    val event = FeedEvent.ItemClicked(titles = item.titles, mainImage = imgDescriptor)
                    eventSink(event)
                },
            )
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                contentAlignment = Alignment.Center,
            ) { Text(text = "You've reached the end!") }
        }
    }
}

@Composable
private fun ImageOfTheDay(
    imageModel: FeedModel.ImageOfTheDay?,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CustomElevatedCard(
        modifier = modifier,
        shape = RectangleShape,
        onClick = onClick,
    ) {
        val img = imageModel?.image
        val url = img?.run { url ?: source }
        CustomAsyncImage(
            urlOrNull = url,
            isDataLoading = isLoading,
            aspectRatio = img?.run { width / height.toFloat() },
        )
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)) {
            CustomPreHeading(textOrNull = "Image of the day", isLoading = isLoading)
            CustomText(
                textOrNull = imageModel?.description?.text,
                isLoading = isLoading,
                style = MaterialTheme.typography.titleLarge,
            )
            val attribution = buildString {
                append(imageModel?.artist?.text)
                append(" · ")
                append(imageModel?.credit?.text)
            }
            CustomText(textOrNull = attribution, isLoading = isLoading)
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun Featured(
    featuredArticle: FeedModel.FeaturedArticle?,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val title = featuredArticle?.titles?.normalized
    CardWithImage(
        modifier = modifier,
        image = {
            val img = featuredArticle?.originalimage
            CustomAsyncImage(
                urlOrNull = img?.source,
                isDataLoading = isLoading,
            )
        },
        onClick = onClick,
    ) {
        CustomPreHeading(textOrNull = "Featured", isLoading = isLoading)
        CustomText(
            textOrNull = title,
            isLoading = isLoading,
            style = MaterialTheme.typography.titleLarge,
        )

        val description = featuredArticle?.description
        CustomText(
            textOrNull = description,
            isLoading = isLoading,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun MostReadSection(
    mostRead: FeedModel.MostRead?,
    isLoading: Boolean,
    onItemClicked: (FeedModel.MostRead.MostReadArticle) -> Unit,
    modifier: Modifier = Modifier
) {
    CustomCarouselLayoutCard(modifier = modifier, isLoading = isLoading, header = {
        CustomPreHeading(
            textOrNull = "Most read · ${mostRead?.date}",
            isLoading = isLoading,
        )
    }, carouselItems = mostRead?.articles ?: emptyList(), carouselItem = { item ->
        val img = item?.originalimage
        val titles = item?.titles
        val title = titles?.normalized
        CardWithImage(modifier = Modifier.padding(horizontal = 8.dp), onClick = {
            item?.let { onItemClicked(it) }
        }, image = {
            CustomAsyncImage(
                urlOrNull = img?.source,
                isDataLoading = isLoading,
            )
        }) {
            Column(modifier = Modifier.padding(8.dp)) {
                val mostRead = item?.run { MostRead(rank, views) }

                mostRead?.let { MostReadInfo(mostRead = it, isLoading = isLoading) }

                CustomText(
                    textOrNull = title,
                    isLoading = isLoading,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
    })
}

@Composable
private fun OnThisDayItem(
    onThisDay: FeedModel.OnThisDay?,
    isLoading: Boolean,
    onitemClicked: (FeedModel.OnThisDay.OnThisDayPage) -> Unit,
    modifier: Modifier = Modifier,
) {
    CustomCarouselLayoutCard(modifier = modifier, isLoading = isLoading, header = {
        Column {
            CustomText(
                textOrNull = onThisDay?.year?.toString(),
                isLoading = isLoading,
                fontWeight = FontWeight.Bold,
            )
        }
    }, leadIn = {
        CustomText(textOrNull = onThisDay?.text, isLoading = isLoading)
    }, carouselItems = onThisDay?.pages ?: emptyList(), carouselItem = { page ->
        val title = page?.titles?.normalized
        CardWithImage(
            modifier = Modifier.padding(horizontal = 8.dp),
            onClick = { page?.let { onitemClicked(it) } },
            image = {
                val img = page?.originalimage
                CustomAsyncImage(
                    urlOrNull = img?.source,
                    isDataLoading = isLoading,
                )
            }) {
            CustomText(
                textOrNull = title,
                isLoading = isLoading,
                style = MaterialTheme.typography.titleLarge,
            )
        }
    })

}
