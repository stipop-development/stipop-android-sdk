package io.stipop

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import io.stipop.extend.StipopImageView
import io.stipop.refactor.data.models.SPPackage
import io.stipop.refactor.data.models.SPSticker
import io.stipop.refactor.domain.entities.SPUser
import io.stipop.refactor.domain.repositories.UserRepository
import io.stipop.refactor.present.di.ApplicationComponent
import io.stipop.refactor.present.di.DaggerApplicationComponent
import io.stipop.refactor.present.ui.components.common.SPStickerKeyboardPopupWindow
import io.stipop.refactor.present.ui.contracts.StipopContract
import io.stipop.refactor.present.ui.pages.search_sticker.SPSearchStickerActivity
import javax.inject.Inject

interface StipopDelegate {
    fun onStickerSelected(sticker: SPSticker): Boolean
    fun canDownload(spPackage: SPPackage): Boolean
}

class Stipop {
    private var _searchStickerIntent: Intent? = null
    private var _searchStickerLauncher: ActivityResultLauncher<Intent>? = null
    private var _keyboardView: StipopContract.View? = null

    @Inject
    internal lateinit var _userRepository: UserRepository

    companion object {

        var instance: Stipop? = null

        internal val _appComponent: ApplicationComponent = DaggerApplicationComponent.create()

        fun configure(context:Context) {
            Config.configure(context)
        }

        fun connect(activity: AppCompatActivity, stipopButton: StipopImageView, userId: String, lang: String, countryCode: String, delegate: StipopDelegate) {
            instance = Stipop()
            instance?.connect(activity, stipopButton, userId, lang, countryCode, delegate)
        }

        fun showSearch() {
            instance?.showSearch()
        }

        fun showKeyboard() {
            instance?.showKeyboard()
        }
    }

    fun connect(activity: AppCompatActivity, stipopButton: StipopImageView, userId: String, lang: String, countryCode: String, delegate: StipopDelegate) {
        Log.d(this::class.simpleName, "connect")

        _appComponent.inject(this)

        _userRepository.setUser(SPUser(userId, countryCode, lang, Config.apikey))

        _keyboardView = SPStickerKeyboardPopupWindow(activity)
        _searchStickerIntent = Intent(activity, SPSearchStickerActivity::class.java)
        _searchStickerLauncher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

        }
    }

    private fun showSearch() {
        Log.d(this::class.simpleName, "showSearch")
        _searchStickerIntent?.let {
            _searchStickerLauncher?.launch(it)
        }
    }

    private fun showKeyboard() {
        Log.d(this::class.simpleName, "showKeyboard")
        _keyboardView?.run {
            if (isShow) {
                onDismiss()
            } else {
                onShow()
            }
        }
    }
}
