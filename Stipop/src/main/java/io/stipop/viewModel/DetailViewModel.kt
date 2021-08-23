package io.stipop.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.stipop.APIClient
import io.stipop.Stipop
import io.stipop.model.SPPackage
import org.json.JSONObject
import java.io.IOException

class DetailViewModel : ViewModel(), DetailViewModelProtocol {

    private var _selectedPackage: MutableLiveData<SPPackage> = MutableLiveData()

    override val selectedPackage: LiveData<SPPackage>
        get() = _selectedPackage

    fun loadPackage(packageId: Int) {
        val params = JSONObject()
        params.put("userId", Stipop.userId)

        APIClient.get(
            APIClient.APIPath.PACKAGE.rawValue + "/${packageId}",
            params
        ) { response: JSONObject?, e: IOException? ->

            e?.run {
                throw this
            }

            response?.run {
                this.getJSONObject("body").run {
                    this.getJSONObject("package").run {
                        _selectedPackage.postValue(SPPackage(this))
                    }
                }
            }
        }
    }
}

interface DetailViewModelProtocol {
    val selectedPackage: LiveData<SPPackage>
}
