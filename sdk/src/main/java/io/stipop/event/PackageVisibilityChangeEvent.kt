package io.stipop.event

import androidx.lifecycle.MutableLiveData

object PackageVisibilityChangeEvent {
    var liveData: MutableLiveData<Int> = MutableLiveData()

    fun publishEvent(packageId: Int){
        liveData.postValue(packageId)
    }

    fun onDestroy(){
        liveData = MutableLiveData()
    }
}