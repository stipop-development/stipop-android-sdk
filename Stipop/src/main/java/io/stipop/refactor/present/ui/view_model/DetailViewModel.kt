package io.stipop.refactor.present.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.stipop.refactor.data.models.SPPackage
import io.stipop.refactor.data.repositories.StickerStoreRepository
import io.stipop.refactor.data.repositories.UserRepository
import javax.inject.Inject

class DetailViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val stickerStoreRepository: StickerStoreRepository
) : DetailViewModelProtocol {

    private var _selectedPackage: MutableLiveData<SPPackage> = MutableLiveData()

    override val selectedPackage: LiveData<SPPackage>
        get() = _selectedPackage

    fun loadPackage(packageId: Int) {
        /*

        // TODO refactor

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
        */
    }
}

interface DetailViewModelProtocol {
    val selectedPackage: LiveData<SPPackage>
}
