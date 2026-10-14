package com.bvic.followmycrypto.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bvic.followmycrypto.data.CryptoRepository
import com.bvic.followmycrypto.model.Crypto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CryptoListViewModel(
    private val repository: CryptoRepository,
) : ViewModel() {

    // Version modifiable, privée : seul le ViewModel peut changer l'état
    private val _uiState = MutableStateFlow<CryptoListUiState>(CryptoListUiState.Loading)
    private var allCryptos: List<Crypto> = emptyList()
    private var selectedSort = CryptoSort.NAME
    private var searchQuery = ""

    // Version en lecture seule, exposée à l'interface
    val uiState: StateFlow<CryptoListUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun refresh() = load()

    fun onSortChange(sort: CryptoSort) {
        selectedSort = sort
        updateDisplayedCryptos()
    }

    fun onQueryChange(query: String) {
        searchQuery = query
        updateDisplayedCryptos()
    }

    private fun updateDisplayedCryptos() {
        val query = searchQuery.trim()

        val filtered = allCryptos.filter { crypto ->
            crypto.name.contains(query, ignoreCase = true) ||
                    crypto.symbol.contains(query, ignoreCase = true)
        }

        val sorted = when (selectedSort) {
            CryptoSort.NAME -> filtered.sortedBy { it.name.lowercase() }
            CryptoSort.PRICE -> filtered.sortedByDescending { it.price }
            CryptoSort.CHANGE -> filtered.sortedByDescending { it.changePercent }
        }

        _uiState.value = CryptoListUiState.Success(
            cryptos = sorted,
            sort = selectedSort,
            query = searchQuery,
        )
    }

    private fun load() {
        _uiState.value = CryptoListUiState.Loading
        viewModelScope.launch {
            try {
                allCryptos = repository.getCryptos()
                updateDisplayedCryptos()
            } catch (e: Exception) {
                _uiState.value = CryptoListUiState.Error("Impossible de charger les cours.")
            }
        }
    }
}
