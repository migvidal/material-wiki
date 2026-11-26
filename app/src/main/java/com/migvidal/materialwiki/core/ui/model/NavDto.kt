package com.migvidal.materialwiki.core.ui.model

import android.os.Parcelable
import com.migvidal.materialwiki.core.network.api.common_model.ImageDescriptor
import com.migvidal.materialwiki.core.ui.components.MostRead
import com.migvidal.materialwiki.feature.feed.Titles
import kotlinx.parcelize.Parcelize

sealed interface NavDto : Parcelable

sealed interface PageNavDto : NavDto {
    val mainImage: ImageDescriptor?
    val mostReadInfo: MostRead?
}

@Parcelize
data class TitledPageDto(
    override val mainImage: ImageDescriptor?,
    override val mostReadInfo: MostRead?,
    val titles: Titles,
) : PageNavDto

@Parcelize
data class IddPageDto(
    override val mainImage: ImageDescriptor?,
    override val mostReadInfo: MostRead?,
    val pageId: Int,
) : PageNavDto

@Parcelize
data class ImageDto(
    val imageDescriptor: ImageDescriptor,
) : NavDto
