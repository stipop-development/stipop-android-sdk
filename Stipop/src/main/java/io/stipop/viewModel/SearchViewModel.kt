package io.stipop.viewModel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.stipop.model.SPPackage

class SearchViewModel : ViewModel() {

    private val _keyword: MutableLiveData<String> = MutableLiveData()
    private val _packageList: MutableLiveData<List<SPPackage>> = MutableLiveData()

    private val _keywordChanges: MediatorLiveData<String> = MediatorLiveData()

    init {
        _keywordChanges.addSource(_keyword) { value ->
        }
    }

    fun loadPackageList(keyword: String, pageNumber: Int) {

    }
}