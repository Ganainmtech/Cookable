package com.example.cookable.data.repository

import com.example.cookable.data.local.dao.CupboardDao
import com.example.cookable.data.local.entity.CupboardItemEntity
import kotlinx.coroutines.flow.Flow

class CookableRepository(
    private val cupboardDao: CupboardDao
) {
    val cupboardItems: Flow<List<CupboardItemEntity>> = cupboardDao.getAll()

    suspend fun addCupboardItem(name: String, quantity: String?, unit: String?) {
        if (name.isBlank()) return
        cupboardDao.insert(
            CupboardItemEntity(name = name.trim(), quantity = quantity?.trim(), unit = unit?.trim())
        )
    }

    suspend fun removeCupboardItemById(id: Long) {
        cupboardDao.deleteById(id)
    }
}
