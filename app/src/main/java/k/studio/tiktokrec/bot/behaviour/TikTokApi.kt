package k.studio.tiktokrec.bot.behaviour

import android.view.accessibility.AccessibilityNodeInfo
import k.studio.screen_driver.screeninteract.ScreenDriver

interface TikTokApi : ScreenDriver {
    fun foundLikeButton(): AccessibilityNodeInfo?
}
