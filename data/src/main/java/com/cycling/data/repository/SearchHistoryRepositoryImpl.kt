package com.cycling.data.repository

import com.cycling.data.local.dao.SearchHistoryDao
import com.cycling.data.local.entity.SearchHistoryEntity
import com.cycling.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchHistoryRepositoryImpl @Inject constructor(
    private val searchHistoryDao: SearchHistoryDao
) : SearchHistoryRepository {

    override fun getAllHistory(): Flow<List<String>> {
        return searchHistoryDao.getAllHistory().map { entities ->
            entities.map { it.query }.distinct()
        }
    }

    override suspend fun saveSearchQuery(query: String) {
        searchHistoryDao.insertHistory(
            SearchHistoryEntity(query = query.trim())
        )
    }

    override suspend fun clearHistory() {
        searchHistoryDao.clearAllHistory()
    }

    override fun getSuggestions(query: String): Flow<List<String>> {
        return searchHistoryDao.getSuggestions(query)
    }
}
