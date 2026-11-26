package com.migvidal.materialwiki.core.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.window.core.layout.WindowSizeClass
import com.migvidal.materialwiki.core.ui.model.Destination
import com.migvidal.materialwiki.core.ui.theme.MaterialWikiTheme
import com.migvidal.materialwiki.screen.HomeScreen
import com.migvidal.materialwiki.screen.SearchScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialWikiTheme {
                MainScreen()
            }
        }
    }
}

@Composable
private fun MainScreen(modifier: Modifier = Modifier) {
    var currentDestination by rememberSaveable { mutableStateOf(Destination.Home) }

    val adaptiveInfo = currentWindowAdaptiveInfoV2()
    val isAtLeastExpanded = adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
    val navType = if (isAtLeastExpanded) {
        NavigationSuiteType.WideNavigationRailExpanded
    } else {
        NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(adaptiveInfo)
    }

    NavigationSuiteScaffold(
        modifier = modifier,
        layoutType = navType,
        navigationSuiteItems = {
            Destination.entries.forEach {
                item(
                    selected = it == currentDestination,
                    onClick = { currentDestination = it },
                    icon = {
                        Icon(
                            painter = painterResource(it.icon),
                            contentDescription = stringResource(it.contentDescription),
                        )
                    },
                    label = {
                        Text(text = stringResource(it.label))
                    }
                )
            }
        }
    ) {
        when (currentDestination) {
            Destination.Home -> HomeScreen()
            Destination.Search -> SearchScreen()
        }
    }
}