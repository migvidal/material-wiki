package com.migvidal.materialwiki.core.network.api.api_service

import com.migvidal.materialwiki.feature.feed.FeedModel
import com.migvidal.materialwiki.feature.page.common.PageModel
import com.migvidal.materialwiki.feature.search.SearchModel
import java.time.LocalDate

interface ApiService {
    suspend fun getPageByTitle(title: String): PageModel
    suspend fun getPageById(pageId: Int): PageModel
    suspend fun getSearch(term: String): SearchModel
    suspend fun getFeed(forDate: LocalDate): FeedModel
}