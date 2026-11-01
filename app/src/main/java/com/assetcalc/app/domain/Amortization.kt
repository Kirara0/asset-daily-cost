package com.assetcalc.app.domain

import com.assetcalc.app.data.AssetItem
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Locale

/**
 * 一件物品在某一天的平摊结果。
 */
data class ItemCost(
    val id: Long,
    val name: String,
    val purchaseDate: LocalDate,
    val amount: Double,
    val usedDays: Int,
    val dailyCost: Double
)

/**
 * 平摊计算的核心逻辑。纯 Kotlin，不依赖 Android，方便单元测试。
 *
 * 规则：
 *  - 已用天数 = 购买当天到今天的自然日差 + 1，最少 1 天（购买当天即视为第 1 天）
 *  - 每日平摊 = 购买金额 / 已用天数
 */
object Amortization {

    fun usedDays(purchaseDate: LocalDate, today: LocalDate): Int {
        val elapsed = ChronoUnit.DAYS.between(purchaseDate, today)
        return (elapsed + 1L).coerceAtLeast(1L).toInt()
    }

    fun dailyCost(amount: Double, usedDays: Int): Double {
        if (usedDays <= 0) return amount
        return amount / usedDays
    }

    fun costOf(item: AssetItem, today: LocalDate): ItemCost {
        val days = usedDays(item.purchaseDate, today)
        return ItemCost(
            id = item.id,
            name = item.name,
            purchaseDate = item.purchaseDate,
            amount = item.amount,
            usedDays = days,
            dailyCost = dailyCost(item.amount, days)
        )
    }

    fun costsOf(items: List<AssetItem>, today: LocalDate): List<ItemCost> =
        items.map { costOf(it, today) }

    fun totalAmount(costs: List<ItemCost>): Double = costs.sumOf { it.amount }

    fun totalDailyCost(costs: List<ItemCost>): Double = costs.sumOf { it.dailyCost }

    fun averageUsedDays(costs: List<ItemCost>): Int =
        if (costs.isEmpty()) 0
        else Math.round(costs.sumOf { it.usedDays.toDouble() } / costs.size).toInt()

    /** ¥1,234.56 */
    fun money(value: Double): String = String.format(Locale.CHINA, "¥%,.2f", value)
}
