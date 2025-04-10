package com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// Enum con las pestañas disponibles.
enum class Tab {
    USER, DRAFT
}

class UserDraftViewModel : ViewModel() {
    private val _selectedTab = MutableLiveData(Tab.USER)
    val selectedTab: LiveData<Tab> = _selectedTab

    fun setSelectedTab(tab: Tab) {
        _selectedTab.value = tab
    }
}
