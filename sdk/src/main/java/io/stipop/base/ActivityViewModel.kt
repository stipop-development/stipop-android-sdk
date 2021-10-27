package io.stipop.base

import androidx.lifecycle.ViewModel
import io.stipop.data.BaseRepository

internal open class ActivityViewModel(val baseRepository: BaseRepository) : ViewModel()