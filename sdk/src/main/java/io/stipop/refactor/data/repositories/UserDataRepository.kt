package io.stipop.refactor.data.repositories

import android.util.Log
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.ReplaySubject
import io.stipop.refactor.domain.entities.SPUser
import io.stipop.refactor.domain.repositories.UserRepository
import javax.inject.Inject


class UserDataRepository @Inject constructor() : UserRepository {

    private val _userChanges: ReplaySubject<SPUser> = ReplaySubject.create()

    override val user: Observable<SPUser> get() = _userChanges
    private  var _currentUser: SPUser? = null
    override val currentUser: SPUser?
        get() = _currentUser

    override fun setUser(user: SPUser?) {
        Log.d(
            this::class.simpleName, "setUser : \n " +
                    "user -> $user"
        )
        _currentUser = user
        _userChanges.onNext(user)
    }
}
