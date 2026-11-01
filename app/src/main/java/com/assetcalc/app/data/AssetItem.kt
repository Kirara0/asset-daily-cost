package com.assetcalc.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * 一条物品记录。
 *
 * 购买日期以 epochDay 存储，避免时区问题；金额按元存储。
 */
@Entity(tableName = "asset_items")
data class AssetItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val purchaseEpochDay: Long,
    val amount: Double,
    val createdAt: Long = System.currentTimeMillis()
) {
    /** 仅计算属性，Room 不会把它当成数据库列。 */
    val purchaseDate: LocalDate
        get() = LocalDate.ofEpochDay(purchaseEpochDay)
}
