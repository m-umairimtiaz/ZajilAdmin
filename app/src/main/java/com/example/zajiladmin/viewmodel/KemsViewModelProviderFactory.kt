package com.gicproject.pdadeviceappkotlin.ui.viewmodel

import KemsViewModel
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.zajiladmin.repository.KemsRepository


class KemsViewModelProviderFactory(
    val app: Application,
    private val kemsRepository: KemsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return KemsViewModel(
            app,
            kemsRepository
        ) as T
    }
}