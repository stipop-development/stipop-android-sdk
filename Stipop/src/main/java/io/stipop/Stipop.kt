package io.stipop

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import io.stipop.extend.StipopImageView
import io.stipop.refactor.data.models.SPPackage
import io.stipop.refactor.data.models.SPSticker
import io.stipop.refactor.domain.entities.SPUser
import io.stipop.refactor.domain.repositories.UserRepository
import io.stipop.refactor.present.di.ApplicationComponent
import io.stipop.refactor.present.di.DaggerApplicationComponent
import io.stipop.refactor.present.ui.components.common.SPStickerKeyboard
import io.stipop.refactor.present.ui.components.common.SPStickerKeyboardPopupWindow
import io.stipop.refactor.present.ui.components.common.SPStickerKeyboardPresenter
import io.stipop.refactor.present.ui.pages.search_sticker.SPSearchStickerActivity
import javax.inject.Inject

interface StipopDelegate {
    fun onStickerSelected(sticker: SPSticker): Boolean
    fun canDownload(spPackage: SPPackage): Boolean
}

class Stipop(private val activity: AppCompatActivity, private val stipopButton: StipopImageView, val delegate: StipopDelegate) {
    @Inject
    internal lateinit var userRepository: UserRepository

    companion object {

        @SuppressLint("StaticFieldLeak")
        var instance: Stipop? = null

        internal val appComponent: ApplicationComponent = DaggerApplicationComponent.create()

        var keyboardHeight = 0
            private set

        fun configure(context:Context) {
            Config.configure(context)
        }

        fun connect(activity: AppCompatActivity, stipopButton: StipopImageView, userId: String, lang: String, countryCode: String, delegate: StipopDelegate) {
            instance = Stipop(activity, stipopButton, delegate)
            instance?.connect()
            instance?.userRepository?.setUser(SPUser(userId, countryCode, lang, Config.apikey))
        }

        fun showSearch() {
            if (instance == null) {
                return
            }

            instance?.showSearch()
        }

        fun onToggleKeyboard() {
            if (instance == null) {
                return
            }


            instance?.onToggleKeyboard()
        }
    }

    private var maxTop = 0
    private var maxBottom = 0
    private var _spKeyboardPresenter: SPStickerKeyboard.Presenter? = null

    private lateinit var rootView: View


    private var connected = false
    private var stickerIconEnabled = false

    fun connect() {
        Log.d(this::class.simpleName, "connect")

        appComponent.inject(this)

        this.stipopButton.setImageResource(Config.getStickerIconResourceId(this.activity))

        this.connected = true

        this.rootView = this.activity.window.decorView.findViewById(android.R.id.content) as View

        _spKeyboardPresenter = SPStickerKeyboardPresenter()
        _spKeyboardPresenter?.setView(SPStickerKeyboardPopupWindow(activity))
    }

    private fun showSearch() {
        Log.d(this::class.simpleName, "showSearch")
        if (!this.connected) {
            return
        }

        val intent = Intent(this.activity, SPSearchStickerActivity::class.java)
        this.activity.startActivity(intent)
    }

    private fun onToggleKeyboard() {
        Log.d(this::class.simpleName, "showKeyboard")
        if (!this.connected) {
            return
        }

        _spKeyboardPresenter?.let {
            if (it.isShow) {
                it.onDismiss()
            } else {
                it.onShow()
            }
        }
    }
}
