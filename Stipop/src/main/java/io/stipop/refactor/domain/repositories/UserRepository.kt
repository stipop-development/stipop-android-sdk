package io.stipop.refactor.domain.repositories

import io.reactivex.rxjava3.core.Observable
import io.stipop.refactor.domain.entities.SPUser

interface UserRepository {

    val user: Observable<SPUser>
    val currentUser: SPUser?

    fun setUser(user: SPUser?)
}

