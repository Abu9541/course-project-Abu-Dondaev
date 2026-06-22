package ru.ncfu.autoshow.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

/** Упрощённое создание фабрики ViewModel при ручном внедрении зависимостей. */
inline fun <reified VM : ViewModel> vmFactory(crossinline create: () -> VM): ViewModelProvider.Factory =
    viewModelFactory { initializer { create() } }
