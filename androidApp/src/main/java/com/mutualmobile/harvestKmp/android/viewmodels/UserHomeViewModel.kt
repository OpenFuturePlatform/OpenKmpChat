package com.mutualmobile.harvestKmp.android.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserHomeViewModel : ViewModel() {
    var tabIndex by mutableStateOf(0)
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading
}