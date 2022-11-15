package k.studio.screen_driver.screeninteract

import android.content.Context
import android.content.res.Resources
import android.view.accessibility.AccessibilityNodeInfo

interface ScreenDriver {

    fun findNodeVisibleByContentDescription(
        contentDescription: String,
        className: ScreenDriverDelegate.ClassName
    ): AccessibilityNodeInfo?

    fun openUrl(url: String): Boolean
    fun click(nodeInfo: AccessibilityNodeInfo): Boolean

    interface Interact {
        fun getContext(): Context
        fun getRootInActiveWindow(): AccessibilityNodeInfo
        fun getResources(): Resources
        //fun onStop()
    }
}