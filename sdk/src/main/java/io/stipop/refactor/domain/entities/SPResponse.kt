package io.stipop.refactor.domain.entities

interface SPResponse<T> {
    val header: SPHeader
    val body: T?
}
