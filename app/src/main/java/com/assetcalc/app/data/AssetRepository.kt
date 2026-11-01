package com.assetcalc.app.data

import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

class AssetRepository(private val dao: AssetDao) {

    val items: Flow<List<AssetItem>> = dao.observeAll()

    suspend fun add(name: String, purchaseDate: LocalDate, amount: Double): Long =
        dao.insert(
            AssetItem(
                name = name.trim(),
                purchaseEpochDay = purchaseDate.toEpochDay(),
                amount = amount
            )
        )

    suspend fun removeById(id: Long) = dao.deleteById(id)

    suspend fun clear() = dao.deleteAll()
}
