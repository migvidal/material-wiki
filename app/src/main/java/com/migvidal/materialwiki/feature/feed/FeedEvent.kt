package com.migvidal.materialwiki.feature.feed

import com.migvidal.materialwiki.core.network.api.common_model.ApiImage
import com.migvidal.materialwiki.core.network.api.common_model.ImageDescriptor
import com.migvidal.materialwiki.core.ui.components.MostRead

sealed interface FeedEvent {
    data class ItemClicked(
        val titles: Titles,
        val mainImage: ImageDescriptor? = null,
        val mostRead: MostRead? = null,
    ) : FeedEvent

    data class ImageClicked(val image: ImageDescriptor) : FeedEvent
}