package k.studio.tiktokrec.ui.delegate.permission

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment

interface IPermissionManager {

    //    fun startNextStep(
//        fragment: Fragment,
//        childFragmentManager: FragmentManager,
//        context: Context,
//        sharedPreferences: SharedPreferences,
//        onGranted: () -> Unit
//    )
//
//    companion object {
//        const val PERMISSION_GRANT_INSTRUCTION_STEP_KEY = "PERMISSION_GRANT_INSTRUCTION_STEP_KEY"
//    }

    fun isDrawOverlayEnabled(context: Context): Boolean
    fun isAccessibilityEnabled(context: Context): Boolean
    fun checkMiUiPermissions(sharedPreferences: SharedPreferences): Boolean

    @MainThread
    fun openAccessibilityPermissionDialog(fragment: Fragment)

    @MainThread
    fun openDrawOverlayPermissionDialog(fragment: Fragment)
    fun Fragment.openDrawOverlayPermissionInstructionDialog()
    fun Fragment.openAccessibilityPermissionInstructionDialog()
}
