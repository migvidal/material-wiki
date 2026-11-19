package com.migvidal.materialwiki.feature.feed

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.migvidal.materialwiki.core.ui.RequestStatus

@Composable
fun FeedUi(modifier: Modifier = Modifier, viewModel: FeedViewModel = hiltViewModel()) {
    val state = viewModel.feedState.collectAsStateWithLifecycle(null).value

    val resp = state?.response ?: return
    val m = when (resp.status) {
        is RequestStatus.Failure -> "Failure"
        RequestStatus.Loading -> "Loading"
        RequestStatus.Success -> "Success"
    }

    val title = resp.data?.featuredArticle?.title ?: "-"
    Column(){
        Text(modifier = modifier, text = "$m - $title")
        Button(onClick = viewModel::fetchFeed) {
            Text(text = "Fetch feed")
        }
    }
}