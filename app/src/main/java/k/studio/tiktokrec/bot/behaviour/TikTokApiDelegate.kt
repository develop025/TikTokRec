package k.studio.tiktokrec.bot.behaviour

import android.view.accessibility.AccessibilityNodeInfo
import k.studio.screen_driver.screeninteract.ScreenDriver
import k.studio.screen_driver.screeninteract.ScreenDriverDelegate
import k.studio.tiktokrec.R

/**
 * Impl functions for TikTok application screen
 */
class TikTokApiDelegate(
    private val interact: ScreenDriver.Interact
) : TikTokApi,
    ScreenDriver by ScreenDriverDelegate(interact),
    ScreenDriver.Interact by interact {

    override fun foundLikeButton(): AccessibilityNodeInfo? {
        findNodeVisibleByContentDescription(
            getContext().getString(R.string.tiktok_like_image_description),
            ScreenDriverDelegate.ClassName.ImageView
        )?.let { childNode ->
            return childNode.parent
        }
        return null
    }
}