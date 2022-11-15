package k.studio.screen_driver.vo

import android.graphics.Rect

/**
 * Accessibility service view data
 */
data class Node(
    val text: String?,
    val contentDescription: String?,
    val windowId: Int,
    val viewIdResourceName: String?,
    val className: String?,
    val childCount: Int?,
    val children: MutableList<Node>,
    val depth: Int,
    val rect: Rect,
    var error: String? = null
)