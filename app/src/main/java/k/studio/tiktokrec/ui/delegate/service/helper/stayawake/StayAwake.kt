package k.studio.tiktokrec.ui.delegate.service.helper.stayawake

import android.content.Context

/**
 * Interface for delegate screen stay awake
 */
interface StayAwake {
    fun initView(context: Context)
    fun removeView()
}
