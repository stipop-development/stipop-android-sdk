package io.stipop.data

import android.content.Context
import android.content.SharedPreferences

internal object UserPref {

    private var mPreferences: SharedPreferences? = null
    private const val NAME = "StipopUserPref"
    private const val LAST_USED_PACKAGE_ID = "LastUsedPackagedId"

    fun init(context: Context) {
        if (mPreferences == null) {
            mPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        }
    }

    fun setUsingPackageId(packageId: Int) {
        mPreferences?.edit()?.putInt(LAST_USED_PACKAGE_ID, packageId)?.apply()
    }

    fun getUsingPackageId(): Int? {
        val response = mPreferences?.getInt(LAST_USED_PACKAGE_ID, -1)
        return if (response == -1) null else response
    }

}