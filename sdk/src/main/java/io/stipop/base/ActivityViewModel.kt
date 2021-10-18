package io.stipop.base

import androidx.lifecycle.ViewModel
import io.stipop.data.BaseRepository

open class ActivityViewModel(val baseRepository: BaseRepository) : ViewModel()