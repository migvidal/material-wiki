package com.migvidal.materialwiki.feature.page.article

import com.migvidal.materialwiki.core.network.api.common_model.ImageDto

data class Article(
    val title: String,
    val summary: String,
    val images: List<ImageDto>,
)