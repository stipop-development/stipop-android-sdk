package io.stipop.delegate

import io.stipop.models.enums.ComponentEnum
import io.stipop.models.enums.LifeCycleEnum

interface SPComponentLifeCycleDelegate {
    fun spComponentLifeCycle(componentEnum: ComponentEnum, lifeCycleEnum: LifeCycleEnum)
}