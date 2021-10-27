package io.stipop.event

import androidx.lifecycle.MutableLiveData

object PackageDownloadEvent {

    var liveData: MutableLiveData<Int> = MutableLiveData()

    fun publishEvent(packageId: Int){
        liveData.postValue(packageId)
    }

    fun onDestroy(){
        liveData = MutableLiveData()
    }
}