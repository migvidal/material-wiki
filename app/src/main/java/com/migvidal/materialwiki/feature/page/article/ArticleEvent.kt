package com.migvidal.materialwiki.feature.page.article

import com.migvidal.materialwiki.core.network.api.common_model.ImageDescriptor

sealed interface ArticleEvent {
    data class ImageClicked(val imageDescriptor: ImageDescriptor) : ArticleEvent
}