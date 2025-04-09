package edu.pract5.tfgfer.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.pract5.tfgfer.data.Repository
import edu.pract5.tfgfer.model.busqueda.Media
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: Repository) : ViewModel() {

    private val _searchResults = MutableLiveData<List<Media>>()
    val searchResults: LiveData<List<Media>> = _searchResults

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun search(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.searchAnime(query).collect { results ->
                    _searchResults.value = results
                }
            } catch (e: Exception) {
                // Manejar el error de manera apropiada, por ejemplo, mostrando un mensaje
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchWithFilters(
        order: String = "default",
        page: Int = 1,
        types: List<String>? = null,
        genres: List<String>? = null,
        statuses: List<Int>? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.searchByFilter(order, page, types, genres, statuses).collect { filteredResults ->
                    _searchResults.value = filteredResults
                }
            } catch (e: Exception) {
                // Manejar el error de manera apropiada, por ejemplo, mostrando un mensaje
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class SearchViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
