package k.studio.screen_driver.screeninteract

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.view.accessibility.AccessibilityNodeInfo
import android.webkit.URLUtil
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import k.studio.screen_driver.utils.MiUiTools
import k.studio.screendriverktx.logD
import k.studio.screendriverktx.logW
import java.util.*

class ScreenDriverDelegate(
    private val interact: ScreenDriver.Interact
) : ScreenDriver,
    ScreenDriver.Interact by interact {

    private var dialogLayout: FrameLayout? = null
    private var statusBarHeight: Int = 0

    init {
        initDelegateFields(getResources())
    }

    override fun findNodeVisibleByContentDescription(
        contentDescription: String,
        className: ClassName
    ): AccessibilityNodeInfo? {
        val root = getRootInActiveWindow()

        var foundNode: AccessibilityNodeInfo? = null
        try {
            val nodes = parseNode(root)
            run loop@{
                nodes.forEach { nodeInfo ->
                    if (nodeInfo.contentDescription?.toString()?.lowercase()
                        == contentDescription.lowercase()
                        && nodeInfo.className.equals(className.value)
                        && nodeInfo.isVisibleToUser
                    ) {
                        "AccessibilityServiceAPI.findNodeVisibleByContentDescription, foundNode id:${nodeInfo.viewIdResourceName}".logD()
                        foundNode = nodeInfo
                        return@loop
                    }
                }
            }

            if (foundNode == null)
                "AccessibilityServiceAPI.findNodeVisibleByContentDescription, contentDescription:$contentDescription foundNode == null".logD()

        } catch (e: Exception) {
            "AccessibilityServiceAPI.findNodeVisibleByContentDescription, contentDescription:$contentDescription Exception:${e.message}".logD()
        }

        return foundNode
    }

    override fun openUrl(url: String): Boolean {
        "start openUrl:$url".logD()
        if (URLUtil.isValidUrl(url)) {
            try {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                browserIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                browserIntent.setPackage("com.zhiliaoapp.musically")
                ContextCompat.startActivity(getContext(), browserIntent, null)
                if (MiUiTools.isMiUi()
                    && !MiUiTools.isIntentAvailable(browserIntent, getContext())
                ) {
                    "AccessibilityServiceAPI.openUrl, url:$url intentAvailable==false".logW()
                    return false
                }
            } catch (e: ActivityNotFoundException) {
                //TODO: tiktok not installed
                "AccessibilityServiceAPI.openUrl, url:$url ActivityNotFoundException ${e.message}".logW()
                return false
            }
        } else {
            //TODO: url not installed
            "AccessibilityServiceAPI.openUrl, url:$url is not valid".logW()
            return false
        }
        "AccessibilityServiceAPI.openUrl url:$url success".logD()
        return true
    }

    override fun click(nodeInfo: AccessibilityNodeInfo): Boolean {
        "AccessibilityServiceAPI.clickNode, viewIdResourceName: ${nodeInfo.viewIdResourceName}".logD()
        try {
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            if (!nodeInfo.isVisibleToUser) {
                val id = try {
                    nodeInfo.viewIdResourceName
                } catch (e: Exception) {
                    null
                }

                val parentId = try {
                    nodeInfo.parent?.viewIdResourceName
                } catch (e: Exception) {
                    null
                }

                "AccessibilityServiceAPI.clickNode, node invisible node: $id, parent: $parentId".logD()
            }
            return true
        } catch (e: IllegalStateException) {
            "AccessibilityServiceAPI.clickNode, IllegalStateException: ${e.message}".logD()
            return false
        }
    }

    private fun initDelegateFields(resources: Resources) {
        statusBarHeight = getStatusBarHeight(resources)
    }

    private fun findNode(
        viewId: String,
        className: ClassName
    ): AccessibilityNodeInfo? {
        val root = getRootInActiveWindow()

        var foundNode: AccessibilityNodeInfo? = null
        try {
            val nodes = parseNode(root)
            run loop@{
                nodes.forEach { nodeInfo ->
                    if (nodeInfo.viewIdResourceName == viewId
                        && nodeInfo.className.equals(className.value)
                    ) {
                        foundNode = nodeInfo
                        return@loop
                    }
                }
            }

            if (foundNode == null)
                "AccessibilityServiceAPI.findNode, viewId:$viewId foundNode == null".logD()

        } catch (e: Exception) {
            "AccessibilityServiceAPI.findNode, viewId:$viewId Exception:${e.message}".logD()
        }

        return foundNode
    }

    private fun findNodeVisible(
        viewId: String,
        className: ClassName
    ): AccessibilityNodeInfo? {
        val root = getRootInActiveWindow()

        var foundNode: AccessibilityNodeInfo? = null
        try {
            val nodes = parseNode(root)
            run loop@{
                nodes.forEach { nodeInfo ->
                    if (nodeInfo.viewIdResourceName == viewId
                        && nodeInfo.className.equals(className.value)
                        && nodeInfo.isVisibleToUser
                    ) {
                        foundNode = nodeInfo
                        return@loop
                    }
                }
            }

            if (foundNode == null)
                "AccessibilityServiceAPI.findNodeVisible, viewId:$viewId foundNode == null".logD()

        } catch (e: Exception) {
            "AccessibilityServiceAPI.findNodeVisible, viewId:$viewId Exception:${e.message}".logD()
        }

        return foundNode
    }

    private fun findNodeVisible(
        viewIds: List<String>,
        className: ClassName
    ): AccessibilityNodeInfo? {
        val root = getRootInActiveWindow()

        var foundNode: AccessibilityNodeInfo? = null
        try {
            val nodes = parseNode(root)
            run loop@{
                nodes.forEach { nodeInfo ->
                    if (viewIds.contains(nodeInfo.viewIdResourceName)
                        && nodeInfo.className.equals(className.value)
                        && nodeInfo.isVisibleToUser
                    ) {
                        "AccessibilityServiceAPI.findNodeVisible, foundNode id:${nodeInfo.viewIdResourceName}".logD()
                        foundNode = nodeInfo
                        return@loop
                    }
                }
            }

            if (foundNode == null)
                "AccessibilityServiceAPI.findNodeVisible, viewId:$viewIds foundNode == null".logD()

        } catch (e: Exception) {
            "AccessibilityServiceAPI.findNodeVisible, viewId:$viewIds Exception:${e.message}".logD()
        }

        return foundNode
    }

    private fun getStatusBarHeight(resources: Resources): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    private fun findNodeInParent(
        parentNode: AccessibilityNodeInfo,
        viewId: String,
        className: ClassName
    ): AccessibilityNodeInfo? {
        "AccessibilityServiceAPI.findNodeInParent, viewId:$viewId, className:".logD()
        var foundNode: AccessibilityNodeInfo? = null
        try {
            val nodes = parseNode(parentNode)
            run loop@{
                nodes.forEach { nodeInfo ->
                    if (nodeInfo.viewIdResourceName == viewId
                        && nodeInfo.className.equals(className.value)
                    ) {
                        foundNode = nodeInfo
                        return@loop
                    }
                }
            }

            if (foundNode == null)
                "AccessibilityServiceAPI.findNodeInParent, viewId:$viewId, foundNode == null".logD()

        } catch (e: Exception) {
            "AccessibilityServiceAPI.findNodeInParent, viewId:$viewId, Exception:${e.message}".logD()
        }

        return foundNode
    }

    @Throws(IllegalStateException::class, NullPointerException::class)
    private fun parseNode(
        nodeInfo: AccessibilityNodeInfo,
        level: Int = 0,
    ): List<AccessibilityNodeInfo> {
        val list = LinkedList<AccessibilityNodeInfo>()
        repeat(nodeInfo.childCount) { number ->
            val node = nodeInfo.getChild(number)
            node?.let { nodeInfo ->
                list.add(nodeInfo)
                val childParsedNodes = parseNode(node, level + 1)
                list.addAll(childParsedNodes)
            }
        }
        return list
    }

    private fun insertText(node: AccessibilityNodeInfo, text: String): Boolean {
        return try {
            val arguments = Bundle()
            arguments.putString(
                AccessibilityNodeInfoCompat.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                text
            )
            node.performAction(AccessibilityNodeInfoCompat.ACTION_SET_TEXT, arguments)
            true
        } catch (e: IllegalStateException) {
            "AccessibilityServiceAPI.startActions, IllegalStateException: ${e.message}".logD()
            false
        }
    }

    enum class ClassName(val value: String) {
        TextView("android.widget.TextView"),
        EditText("android.widget.EditText"),
        Button("android.widget.Button"),
        ImageView("android.widget.ImageView"),
        ViewGroup("android.view.ViewGroup"),
        FrameLayout("android.widget.FrameLayout"),
        LinearLayout("android.widget.LinearLayout")
    }
}