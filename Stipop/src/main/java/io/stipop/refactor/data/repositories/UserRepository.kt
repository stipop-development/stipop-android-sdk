package io.stipop.refactor.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.Module
import io.stipop.refactor.data.models.SPUser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor() {

    private val _user: MutableLiveData<SPUser?> = MutableLiveData()

    val userChanges: LiveData<SPUser?> get() = _user
    val user: SPUser? get() = _user.value

    fun setUser(user: SPUser?) {
        _user.value.let {
            if (it == null || it != user) {
                _user.postValue(user)
            }
        }
    }
}
