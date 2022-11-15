package k.studio.tiktokrec.ui.delegate.service.helper.stayawake

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import k.studio.tiktokrec.R
import k.studio.tiktokrec.utils.logD

/**
 * Delegate screen stay awake
 */
class DelegateStayAwake : StayAwake {
    private var windowManager: WindowManager? = null
    private var chatHeadView: View? = null

    init {
        "DelegateStayAwake.init".logD()
    }

    @SuppressLint("InflateParams")
    override fun initView(context: Context) {
        "DelegateStayAwake.initView".logD()
        chatHeadView = LayoutInflater.from(context).inflate(R.layout.stay_awake, null)
        //type for screen overlay
        @Suppress("DEPRECATION") val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            WindowManager.LayoutParams.TYPE_PHONE
        //type for keep screen on and overlay
        val flag =
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            flag,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.START
        params.x = 10
        params.y = 10
        // View set transparent because don`t have user information
        // and use only for screen wake on
        //chatHeadView.setBackgroundColor(Color.TRANSPARENT)
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        chatHeadView?.let {
            windowManager?.addView(it, params)
        }
    }

    /**
     * call before onDestroy()
     */
    override fun removeView() {
        chatHeadView?.let {
            windowManager?.removeView(it)
        }
    }
}
