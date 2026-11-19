package com.migvidal.materialwiki.feature.feed

import com.migvidal.materialwiki.core.network.api.common_model.ApiImage
import com.migvidal.materialwiki.core.network.api.common_model.ImageDto
import com.migvidal.materialwiki.core.ui.components.MostRead

sealed interface FeedEvent {
    data class ItemClicked(
        val titles: Titles,
        val mainImage: ApiImage?,
        val mostRead: MostRead? = null,
    ) : FeedEvent

    data class ImageClicked(val image: ImageDto? = null) : FeedEvent
}