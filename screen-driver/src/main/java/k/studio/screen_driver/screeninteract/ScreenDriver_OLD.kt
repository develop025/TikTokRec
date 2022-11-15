package k.studio.screen_driver.screeninteract

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.Rect
import android.os.Build
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_DOWN
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_UP
import k.studio.screen_driver.R
import k.studio.screen_driver.vo.Node
import k.studio.screendriverktx.logD
import java.util.*
import kotlin.concurrent.thread
import kotlin.random.Random


abstract class ScreenDriver_OLD : AccessibilityService() {

    private var dialogLayout: FrameLayout? = null
    private var statusBarHeight: Int = 0
    private lateinit var root: AccessibilityNodeInfo

    override fun onCreate() {
        super.onCreate()
        statusBarHeight = getStatusBarHeight()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val currentRoot = rootInActiveWindow
        if (currentRoot != null)
            root = currentRoot
    }

    @WorkerThread
    fun checkServiceNeedRestart(feedback: (Boolean) -> Unit) {
        repeat(3) { n ->
            if (rootInActiveWindow != null) {
                feedback.invoke(false)
                return
            }

            if (n < 3)
                Thread.sleep(50)
        }

        feedback.invoke(true)
    }

    fun findNode(
        viewId: String,
        className: ClassName
    ): AccessibilityNodeInfo? {
        val currentRoot = rootInActiveWindow
        val cRoot: AccessibilityNodeInfo =
            when {
                currentRoot != null -> {
                    currentRoot
                }
                ::root.isInitialized -> {
                    root
                }
                else -> {
                    "rootInActiveWindow == null".logD()
                    return null
                }
            }

        var foundNode: AccessibilityNodeInfo? = null
        try {
            val nodes = parseNode(cRoot)
            run loop@{
                nodes.forEach { nodeInfo ->
                    if (nodeInfo.viewIdResourceName == viewId && nodeInfo.className.equals(className.value)) {
                        foundNode = nodeInfo
                        return@loop
                    }
                }
            }

            if (foundNode == null)
                "findNode: $viewId foundNode == null root:${cRoot.hashCode()}".logD()

        } catch (e: Exception) {
            "findNode:$viewId Exception:${e.message} root:${cRoot.hashCode()}".logD()
        }

        return foundNode
    }

    fun findNode(
        viewId: String,
        textList: List<String>,
        className: ClassName
    ): AccessibilityNodeInfo? {
        val currentRoot = rootInActiveWindow
        val cRoot: AccessibilityNodeInfo =
            when {
                currentRoot != null -> {
                    currentRoot
                }
                ::root.isInitialized -> {
                    root
                }
                else -> {
                    "rootInActiveWindow == null".logD()
                    return null
                }
            }

        var foundNode: AccessibilityNodeInfo? = null
        try {
            val nodes = parseNode(cRoot)
            run loop@{
                nodes.forEach { nodeInfo ->
                    if (nodeInfo.viewIdResourceName == viewId && nodeInfo.className.equals(className.value)) {
                        val nodeText = nodeInfo.text.toString().lowercase(Locale.ROOT)
                        textList.forEach { text ->
                            if (nodeText.contains(text.lowercase(Locale.ROOT))) {
                                foundNode = nodeInfo
                                return@loop
                            }
                        }
                    }
                }
            }

            if (foundNode == null)
                "findNode: $viewId foundNode == null root:${cRoot.hashCode()}".logD()

        } catch (e: Exception) {
            "findNode:$viewId Exception:${e.message} root:${cRoot.hashCode()}".logD()
        }

        return foundNode
    }

    fun findNode(viewId: String): AccessibilityNodeInfo? {
        val currentRoot = rootInActiveWindow
        val cRoot: AccessibilityNodeInfo =
            when {
                currentRoot != null -> {
                    currentRoot
                }
                ::root.isInitialized -> {
                    root
                }
                else -> {
                    "rootInActiveWindow == null".logD()
                    return null
                }
            }

        var foundNode: AccessibilityNodeInfo? = null
        try {
            val nodes = parseNode(cRoot)
            run loop@{
                nodes.forEach { nodeInfo ->
                    if (nodeInfo.viewIdResourceName == viewId) {
                        foundNode = nodeInfo
                        return@loop
                    }
                }
            }

            if (foundNode == null)
                "findNode: $viewId foundNode == null root:${cRoot.hashCode()}".logD()

        } catch (e: Exception) {
            "findNode:$viewId Exception:${e.message} root:${cRoot.hashCode()}".logD()
        }

        return foundNode
    }

    fun findNode(viewId: String, nodeNumber: Int): AccessibilityNodeInfo? {
        val currentRoot = rootInActiveWindow
        val cRoot: AccessibilityNodeInfo =
            when {
                currentRoot != null -> {
                    currentRoot
                }
                ::root.isInitialized -> {
                    root
                }
                else -> {
                    "rootInActiveWindow == null".logD()
                    return null
                }
            }

        var countFoundNode = 0
        var foundNode: AccessibilityNodeInfo? = null
        try {
            val nodes = parseNode(cRoot)
            run loop@{
                nodes.forEach { nodeInfo ->
                    if (nodeInfo.viewIdResourceName == viewId) {
                        if (nodeNumber == countFoundNode) {
                            foundNode = nodeInfo
                            return@loop
                        }
                        countFoundNode++
                    }
                }
            }
            if (foundNode == null)
                throw Exception("foundNode == null")
        } catch (e: IllegalStateException) {
            "findNode:$viewId IllegalStateException:${e.message} root:${cRoot.hashCode()}".logD()
        } catch (e: Exception) {
            "findNode:$viewId Exception:${e.message} root:${cRoot.hashCode()}".logD()
        }

        return foundNode
    }

    fun findChild(
        parentNode: AccessibilityNodeInfo,
        viewId: String,
        nodeNumber: Int
    ): AccessibilityNodeInfo? {
        val currentRoot = rootInActiveWindow
        val cRoot: AccessibilityNodeInfo =
            when {
                currentRoot != null -> {
                    currentRoot
                }
                ::root.isInitialized -> {
                    root
                }
                else -> {
                    "rootInActiveWindow == null".logD()
                    return null
                }
            }

        var countFoundNode = 0
        var foundNode: AccessibilityNodeInfo? = null
        try {
            val nodes = parseNode(parentNode)
            run loop@{
                nodes.forEach { nodeInfo ->
                    if (nodeInfo.viewIdResourceName == viewId) {
                        if (nodeNumber == countFoundNode) {
                            foundNode = nodeInfo
                            return@loop
                        }
                        countFoundNode++
                    }
                }
            }
            if (foundNode == null)
                throw Exception("foundNode == null")
        } catch (e: IllegalStateException) {
            "findNode:$viewId IllegalStateException:${e.message} root:${cRoot.hashCode()}".logD()
        } catch (e: Exception) {
            "findNode:$viewId Exception:${e.message} root:${cRoot.hashCode()}".logD()
        }

        return foundNode
    }

    fun findNodeInParent(parentViewId: String, listId: ArrayList<String>): AccessibilityNodeInfo? {
        var childCount = 0
        var foundNode: AccessibilityNodeInfo? = null
        val parentNode = findNode(parentViewId)

        try {
            if (parentNode != null) {
                val nodes = parseNode(parentNode)
                "nodes parsed in parent:${nodes.toList()}".logD()
                run loop@{
                    nodes.forEach { nodeInfo ->
                        listId.forEach { id ->
                            if ((nodeInfo.viewIdResourceName ?: "").contains(id)) {
                                childCount++
                                if (foundNode == null) {
                                    foundNode = nodeInfo
                                    "foundNode:$foundNode".logD()
                                    return@loop
                                }
                            }
                        }
                    }
                }
            } else
                "findNodeInParent:$parentViewId list:$listId parentNode == null".logD()

            if (foundNode == null)
                "findNodeInParent:$parentViewId list:$listId foundNode == null".logD()

        } catch (e: Exception) {
            "findNodeInParent:$parentViewId list:$listId Exception:${e.message}".logD()
        }

        "childCount:$childCount".logD()
        return foundNode
    }

    fun findFirstChild(
        parentId: String,
        className: ClassName,
        childId: String?,
    ): AccessibilityNodeInfo? {
        var targetNode: AccessibilityNodeInfo? = null

        try {
            val parentNode = findNode(parentId)
            if (parentNode != null) {
                run loop@{
                    repeat(parentNode.childCount) { n ->
                        val childNode = parentNode.getChild(n)
                        if (childNode != null) {
                            if (childNode.viewIdResourceName == childId && childNode.className ?: "" == className.value) {
                                targetNode = childNode
                                return@loop
                            }
                        } else
                            throw Exception("getChild n:$n = null")
                    }
                }

                if (targetNode == null)
                    throw Exception("targetNode == null")

            } else
                throw Exception("foundNode == null")

        } catch (e: IllegalStateException) {
            ("findFirstChildWithNullID parentId:$parentId className:$className" +
                    " childId:$childId IllegalStateException:${e.message}").logD()
        } catch (e: Exception) {
            ("findFirstChildWithNullID parentId:$parentId className:$className" +
                    " childId:$childId error:${e.message}").logD()
        }

        return targetNode
    }

    fun findChild(
        parentNode: AccessibilityNodeInfo,
        childId: String,
        childClassName: ClassName,
        textList: List<String>
    ): AccessibilityNodeInfo? {
        var targetNode: AccessibilityNodeInfo? = null

        try {
            run loop@{
                repeat(parentNode.childCount) { n ->
                    val childNode = parentNode.getChild(n)
                    if (childNode != null) {
                        if (childNode.viewIdResourceName == childId
                            && childNode.className.equals(childClassName.value)
                        ) {
                            textList.forEach {
                                if (it.lowercase(Locale.ROOT) == childNode.text.toString()
                                        .lowercase(Locale.ROOT)
                                ) {
                                    targetNode = childNode
                                    return@loop
                                }
                            }
                            return@loop
                        }
                    } else
                        throw Exception("getChild n:$n = null")
                }
            }

            if (targetNode == null)
                throw Exception("targetNode == null")
        } catch (e: IllegalStateException) {
            ("findChild parentId:${parentNode.viewIdResourceName} className:$childClassName" +
                    " childId:$childId IllegalStateException:${e.message}").logD()
        } catch (e: Exception) {
            ("findChild parentId:${parentNode.viewIdResourceName} className:$childClassName" +
                    " childId:$childId error:${e.message}").logD()
        }

        return targetNode
    }

    fun findNodeChildNotEmpty(viewIdResourceName: String): AccessibilityNodeInfo? {
        val currentRoot = rootInActiveWindow
        val cRoot: AccessibilityNodeInfo =
            when {
                currentRoot != null -> {
                    currentRoot
                }
                ::root.isInitialized -> {
                    root
                }
                else -> {
                    "rootInActiveWindow == null".logD()
                    return null
                }
            }

        var countFoundNode = 0
        var foundNode: AccessibilityNodeInfo? = null
        try {
            val nodes = parseNode(cRoot)
            run loop@{
                nodes.forEach { nodeInfo ->
                    if ((nodeInfo.viewIdResourceName ?: "") == viewIdResourceName) {
                        countFoundNode++
                        if (nodeInfo.childCount > 0) {
                            foundNode = nodeInfo
                            return@loop
                        }
                    }
                }
            }

            if (foundNode == null)
                throw Exception("foundNode == null")

        } catch (e: IllegalStateException) {
            ("findNodeChildNotEmpty:$viewIdResourceName IllegalStateException:${e.message}" +
                    " root:${cRoot.hashCode()}").logD()
        } catch (e: Exception) {
            ("findNodeChildNotEmpty:$viewIdResourceName Exception:${e.message}" +
                    " root:${cRoot.hashCode()}").logD()
        }

        return foundNode
    }

    fun clickNode(nodeInfo: AccessibilityNodeInfo) {
        "clickButton name - ${nodeInfo.viewIdResourceName}".logD()
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

            "clickButton node invisible node:${id} parent:${parentId}".logD()
        }
    }

    fun getScreenViews(
        tag: String,
    ): Node? {
        val currentRoot = rootInActiveWindow
        val cRoot: AccessibilityNodeInfo =
            when {
                currentRoot != null -> {
                    currentRoot
                }
                ::root.isInitialized -> {
                    root
                }
                else -> {
                    "rootInActiveWindow == null".logD()
                    return null
                }
            }

        return getScreenViews(cRoot, 0, tag, null)
    }

    fun findFirstChild(
        node: AccessibilityNodeInfo,
        className: ClassName,
        isNotEmptyContentDescription: Boolean,
    ): AccessibilityNodeInfo? {
        var foundNode: AccessibilityNodeInfo? = null
        var childCount = 0

        try {
            val nodes = parseNode(node)
            run loop@{
                nodes.forEach { nodeInfo ->

                    val trueContentDescription =
                        (isNotEmptyContentDescription && !nodeInfo.contentDescription.isNullOrEmpty())
                                ||
                                (!isNotEmptyContentDescription && nodeInfo.contentDescription.isNullOrEmpty())

                    if ((nodeInfo.className ?: "") == className.value
                        && trueContentDescription
                    ) {
                        childCount++
                        foundNode = nodeInfo
                        return@loop
                    }
                }
            }

            if (foundNode == null)
                throw Exception("foundNode == null")

        } catch (e: Exception) {
            ("findFirstChild parent:${node.viewIdResourceName} " +
                    "Exception:${e.message}").logD()
        }
        "childCount:$childCount".logD()
        return foundNode
    }

  /*  fun canScrollDown(node: AccessibilityNodeInfo) =
        node.actionList.contains(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_DOWN)

    fun scrollDown(node: AccessibilityNodeInfo): Boolean {
        return if (!node.actionList.contains(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_DOWN)) {
            "ACTION_SCROLL_DOWN not exist".logD()
            false
        } else {
            node.performAction(ACTION_SCROLL_DOWN.id)
            true
        }
    }*/

    /*fun scrollUp(node: AccessibilityNodeInfo) {
        if (!node.actionList.contains(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_UP)) {
            "ACTION_SCROLL_UP not exist".logD()
        } else {
            node.performAction(ACTION_SCROLL_UP.id)
        }
    }*/

    private fun getScreenViews(
        nodeInfo: AccessibilityNodeInfo,
        depth: Int,
        tag: String,
        parentNode: Node?,
    ): Node {
        var logString = ""

        for (i in 0 until depth) {
            logString += " "
        }

        val rect = getNodeRect(nodeInfo)

        val node = Node(
            nodeInfo.text?.toString(),
            nodeInfo.contentDescription?.toString(),
            nodeInfo.windowId,
            nodeInfo.viewIdResourceName,
            nodeInfo.className?.toString(),
            nodeInfo.childCount,
            mutableListOf(),
            depth,
            rect
        )
        parentNode?.children?.add(node)

        if (nodeInfo.childCount > 0)
            for (i in 0 until nodeInfo.childCount) {
                nodeInfo.getChild(i)?.let {
                    getScreenViews(it, depth + 1, tag, node)
                }
            }

        return node
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun doubleClickInNodeArea(node: AccessibilityNodeInfo) {
        val rect = getNodeRect(node)
        "Rect rect.left:${rect.left} rect.top:${rect.top}".logD()
        val x = Random.nextInt(rect.left + 30, rect.left + rect.width() - 30)
        val y = Random.nextInt(rect.top + 30, rect.top + rect.height() - 30)
        "x:${x} y:${y}".logD()
        val duration = Random.nextLong(50L, 150L)
        "duration:$duration".logD()
        dispatchGesture(
            createClick(x.toFloat(), y.toFloat(), 0, duration),
            object : GestureResultCallback() {

                override fun onCompleted(gestureDescription: GestureDescription?) {
                    super.onCompleted(gestureDescription)
                    "gesture completed duration:$duration startTime:0 ${System.currentTimeMillis()}".logD()

                    val secondDuration = Random.nextLong(50L, 150L)
                    var startTime = 150L - secondDuration + Random.nextLong(50L)
                    if (startTime < 20) startTime += 20

                    dispatchGesture(
                        createClick(
                            x.toFloat() + Random.nextInt(30),
                            y.toFloat() + Random.nextInt(30),
                            startTime,
                            secondDuration
                        ),
                        object : GestureResultCallback() {

                            override fun onCompleted(gestureDescription: GestureDescription?) {
                                super.onCompleted(gestureDescription)
                                "gesture completed duration:$secondDuration startTime:$startTime ${System.currentTimeMillis()}".logD()
                            }

                            override fun onCancelled(gestureDescription: GestureDescription?) {
                                super.onCancelled(gestureDescription)
                                "gesture cancelled".logD()
                            }
                        }, null
                    )
                }

                override fun onCancelled(gestureDescription: GestureDescription?) {
                    super.onCancelled(gestureDescription)
                    "2 gesture cancelled".logD()
                }
            }, null
        )

//  click(x, y)
//  Thread.sleep(Random.nextInt(300, 500).toLong())
//  dispatchGesture(createClick(x.toFloat(), y.toFloat()), callback, null)
//  click(x + Random.nextInt(0, 30), y + Random.nextInt(0, 30))
    }

    @Throws(IllegalStateException::class, NullPointerException::class)
    private fun parseNode(
        nodeInfo: AccessibilityNodeInfo,
        level: Int = 0,
    ): List<AccessibilityNodeInfo> {
        outputViewThreeLog(level, nodeInfo)

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

    private fun outputViewThreeLog(
        level: Int,
        nodeInfo: AccessibilityNodeInfo,
    ) {
        var l = ""
        repeat(level) {
            l = "$l    "
        }
        val outputViewThree = "${l}id:${nodeInfo.viewIdResourceName}, " +
                "class:${nodeInfo.className}, " +
                "text:${nodeInfo.text}"

        outputViewThree.logD()
    }

    private fun getNodeRect(nodeInfo: AccessibilityNodeInfo): Rect {
        val rect = Rect()
        nodeInfo.getBoundsInScreen(rect)
        return rect
    }

/* fun clickInNodeArea(node: AccessibilityNodeInfo) {
     val rect = getNodeRect(node)
     val x = Random.nextInt(rect.left, rect.left + rect.width())
     val y = Random.nextInt(rect.top, rect.top + rect.height())
     click(x, y)
 }*/

/* private fun click(x: Int, y: Int) {
     val path = Path()
     path.moveTo(x.toFloat(), y.toFloat())
     val builder = GestureDescription.Builder()
     val gestureDescription = builder
         .addStroke(GestureDescription.StrokeDescription(path, 10, 500))
         .build()
     dispatchGesture(gestureDescription, null, null)
 }*/

    @RequiresApi(Build.VERSION_CODES.N)
    private fun createClick(
        x: Float,
        y: Float,
        startTime: Long,
        duration: Long,
    ): GestureDescription {
        val clickPath = Path()
        clickPath.moveTo(x, y)
        clickPath.lineTo(x + 2, y + 2)
        val clickBuilder = GestureDescription.Builder()

        clickBuilder.addStroke(
            GestureDescription.StrokeDescription(
                clickPath,
                startTime,
                duration
            )
        )

        return clickBuilder.build()
    }

    private fun getDrawText(nodeInfo: AccessibilityNodeInfo): String? {
        return if (nodeInfo.viewIdResourceName != null || nodeInfo.contentDescription != null)
            "${nodeInfo.viewIdResourceName} ${nodeInfo.contentDescription}"
        else null
    }

    private fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun findFirstChild(
        parentNodeId: String,
        childClassName: ClassName,
        textList: List<String>,
        childId: String?,
    ): AccessibilityNodeInfo? {
        var targetNode: AccessibilityNodeInfo? = null

        val parentNode = findNode(parentNodeId)
        if (parentNode != null) {
            val childNodeList = parseNode(parentNode)
            run loop@{
                childNodeList.forEach { node ->
                    node.apply {
                        if (className ?: "" == childClassName.value
                            && viewIdResourceName == childId
                            && !text.isNullOrEmpty()
                        ) {
                            textList.forEach {
                                if (it == text.toString().lowercase(Locale.ROOT)) {
                                    targetNode = this
                                    return@loop
                                }
                            }

                        }
                    }
                }
                if (targetNode == null)
                    ("findChild not found child node parentNodeId:${parentNodeId} " +
                            "childClassName:${childClassName} childId: $childId").logD()
            }
        } else {
            ("findChild not found parent node parentNodeId:${parentNodeId} " +
                    "childClassName:${childClassName} childId: $childId").logD()
        }

        return targetNode
    }

    private fun hideDialogD() {
        thread {
            if (Looper.myLooper() == null) {
                Looper.prepare()
            }
            val wm = getSystemService(WINDOW_SERVICE) as WindowManager
            wm.removeView(dialogLayout)
            Looper.loop()
        }
    }

  /*  private fun showDialog(message: String) {
        thread {
            if (Looper.myLooper() == null) {
                Looper.prepare()
            }
            val wm = getSystemService(WINDOW_SERVICE) as WindowManager
            dialogLayout = FrameLayout(this)
            val lp = WindowManager.LayoutParams()
            lp.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
            lp.format = PixelFormat.TRANSLUCENT
            lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            lp.gravity = Gravity.CENTER
            val inflater = LayoutInflater.from(this)
            inflater.inflate(R.layout.bot_dialog, dialogLayout)
            val messageTextView = dialogLayout?.findViewById<TextView>(R.id.message)
            val closeDialogBtn = dialogLayout?.findViewById<TextView>(R.id.closeDialog)
            messageTextView?.text = message
            closeDialogBtn?.setOnClickListener {
                hideDialogD()
            }
            wm.addView(dialogLayout, lp)
            Looper.loop()
        }
    }
*/
    fun findNodes(nodeId: String): List<AccessibilityNodeInfo> {
        val result = LinkedList<AccessibilityNodeInfo>()
        val currentRoot = rootInActiveWindow
        val cRoot: AccessibilityNodeInfo =
            when {
                currentRoot != null -> {
                    currentRoot
                }
                ::root.isInitialized -> {
                    root
                }
                else -> {
                    "rootInActiveWindow == null".logD()
                    return result
                }
            }

        try {
            val nodes = parseNode(cRoot)
            run loop@{
                nodes.forEach { nodeInfo ->
                    if (nodeInfo.viewIdResourceName == nodeId) {
                        result.add(nodeInfo)
                    }
                }
            }

            if (result.isEmpty())
                "findNodes: $nodeId not found. Root:${cRoot.hashCode()}".logD()

        } catch (e: Exception) {
            "findNodes:$nodeId Exception:${e.message}. Root:${cRoot.hashCode()}".logD()
        }

        return result
    }

    //Connect to service for stop it
//    private val serviceConnection = object : ServiceConnection {
//        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
//            "onServiceConnected".logD()
//            taskBackgroundService = (service as TaskBackgroundService.TaskBackgroundBinder).service
//            taskBackgroundService?.stopSelf()
//        }
//
//        override fun onServiceDisconnected(name: ComponentName?) {
//            "onServiceDisconnected".logD()
//            taskBackgroundService = null
//        }
//    }

   /* fun onEndOfScenario() {
        stopConsole()
        openMainActivity("End of scenario", true)
    }
*/
   /* private fun openMainActivity(message: String, error: Boolean) {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        stopSelf()
    }*/

    private fun stopConsole() {
//        val serviceIntent = Intent(this, TaskBackgroundService::class.java)
//        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE)
//        "taskBackgroundService == null:${taskBackgroundService == null}".logD()
//        taskBackgroundService?.stopSelf()
    }

    enum class ClassName(val value: String) {
        ImageView("android.widget.ImageView"),
        TextView("android.widget.TextView"),
        Button("android.widget.Button")
    }
}