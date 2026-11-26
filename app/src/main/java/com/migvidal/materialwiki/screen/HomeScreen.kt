package com.migvidal.materialwiki.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.migvidal.materialwiki.R
import com.migvidal.materialwiki.core.ui.components.CustomTopAppBar
import com.migvidal.materialwiki.core.ui.model.IddPageDto
import com.migvidal.materialwiki.core.ui.model.ImageDto
import com.migvidal.materialwiki.core.ui.model.NavDto
import com.migvidal.materialwiki.core.ui.model.PageNavDto
import com.migvidal.materialwiki.core.ui.model.TitledPageDto
import com.migvidal.materialwiki.feature.feed.FeedEvent
import com.migvidal.materialwiki.feature.feed.FeedUi
import com.migvidal.materialwiki.feature.page.article.ArticleUi
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val navigator = rememberListDetailPaneScaffoldNavigator<NavDto>()
    val scope = rememberCoroutineScope()
    NavigableListDetailPaneScaffold(modifier = modifier, navigator = navigator, listPane = {
        AnimatedPane {
            FeedUi { event ->
                val navDto = when (event) {
                    is FeedEvent.ImageClicked -> ImageDto(event.image)
                    is FeedEvent.ItemClicked -> TitledPageDto(
                        mainImage = event.mainImage,
                        mostReadInfo = event.mostRead,
                        titles = event.titles,
                    )
                }
                scope.launch {
                    navigator.navigateTo(
                        pane = ListDetailPaneScaffoldRole.Detail,
                        contentKey = navDto,
                    )
                }
            }
        }
    }, detailPane = {
        val key = navigator.currentDestination?.contentKey
        Scaffold(topBar = tb@{
            if (key == null) return@tb
            val title = when (key) {
                is ImageDto -> stringResource(R.string.image)
                is IddPageDto -> stringResource(R.string.article)
                is TitledPageDto -> key.titles.normalized
            }
            CustomTopAppBar(
                title = title,
                canGoBack = navigator.canNavigateBack(),
                onGoBack = { scope.launch { navigator.navigateBack() } },
            )
        }) { innerPadding ->
            AnimatedPane(modifier = Modifier.padding(innerPadding)) {
                if (key == null) {
                    Text(text = stringResource(R.string.nothing_selected))
                    return@AnimatedPane
                }
                when (key) {
                    is ImageDto -> Text("Image")
                    is PageNavDto -> ArticleUi(pageNavDto = key) { event -> }
                }
            }
        }

    })
}