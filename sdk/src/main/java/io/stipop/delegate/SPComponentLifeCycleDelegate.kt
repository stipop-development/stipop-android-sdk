package io.stipop.delegate

import io.stipop.models.ComponentEnum
import io.stipop.models.LifeCycleEnum

interface SPComponentLifeCycleDelegate {
    fun spComponentLifeCycle(componentEnum: ComponentEnum, lifeCycleEnum: LifeCycleEnum)
}