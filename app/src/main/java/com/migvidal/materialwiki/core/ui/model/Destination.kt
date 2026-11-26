package com.migvidal.materialwiki.core.ui.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.migvidal.materialwiki.R

enum class Destination(
    @StringRes val label: Int,
    @DrawableRes val icon: Int,
    @StringRes val contentDescription: Int,
) {
    Home(
        label = R.string.home,
        icon = R.drawable.sharp_home_24,
        contentDescription = R.string.home,
    ),
    Search(
        label = R.string.search,
        icon = R.drawable.sharp_search_24,
        contentDescription = R.string.search,
    ),
}