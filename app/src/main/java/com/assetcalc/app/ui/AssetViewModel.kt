package com.assetcalc.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.assetcalc.app.data.AssetRepository
import com.assetcalc.app.domain.Amortization
import com.assetcalc.app.domain.ItemCost
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AssetViewModel(private val repository: AssetRepository) : ViewModel() {

    /** 「截止到哪一天」：默认今天，点右上角刷新可以重新取一次系统日期。 */
    private val today = MutableStateFlow(LocalDate.now())

    val todayDate: StateFlow<LocalDate> = today.asStateFlow()

    val costs: StateFlow<List<ItemCost>> =
        combine(repository.items, today) { items, date ->
            Amortization.costsOf(items, date)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun add(name: String, purchaseDate: LocalDate, amount: Double) {
        viewModelScope.launch { repository.add(name, purchaseDate, amount) }
    }

    fun remove(id: Long) {
        viewModelScope.launch { repository.removeById(id) }
    }

    fun clearAll() {
        viewModelScope.launch { repository.clear() }
    }

    fun refreshToday() {
        today.value = LocalDate.now()
    }

    companion object {
        fun factory(repository: AssetRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { AssetViewModel(repository) }
        }
    }
}
