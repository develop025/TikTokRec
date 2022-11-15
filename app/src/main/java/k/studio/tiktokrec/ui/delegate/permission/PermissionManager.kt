package k.studio.tiktokrec.ui.delegate.permission

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.provider.Settings
import android.text.TextUtils
import androidx.annotation.MainThread
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import k.studio.screen_driver.utils.MiUiTools
import k.studio.tiktokrec.bot.AppBot
import k.studio.tiktokrec.ui.permissions.PermissionGrantInstructionActivity
import k.studio.tiktokrec.utils.logD


class PermissionManager : IPermissionManager {

    companion object {
        const val keyMiUiPermissions = "keyMiUiPermissionsFirstStart"
        const val TAG = "PermissionManager"

        /** Intent extra bundle key for the Android settings app. */
        private const val EXTRA_FRAGMENT_ARG_KEY = ":settings:fragment_args_key"

        /** Intent extra bundle key for the Android settings app. */
        private const val EXTRA_SHOW_FRAGMENT_ARGUMENTS = ":settings:show_fragment_args"

        private const val EXTRA_SYSTEM_ALERT_WINDOW = "system_alert_window"
    }

    override fun checkMiUiPermissions(sharedPreferences: SharedPreferences): Boolean {
        return if (sharedPreferences.getBoolean(keyMiUiPermissions, true)) {
            sharedPreferences.edit().putBoolean(keyMiUiPermissions, false).apply()
            false
        } else
            true
    }

    override fun isDrawOverlayEnabled(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }

    //TODO: fix not start accessibility service after update on Nokia 7.2
    override fun isAccessibilityEnabled(context: Context): Boolean {
        "PermissionManager.isAccessibilityEnabled".logD(TAG)
        var accessibilityEnabled = 0
        val accessibilityFound = false

        try {
            accessibilityEnabled =
                Settings.Secure.getInt(
                    context.contentResolver,
                    Settings.Secure.ACCESSIBILITY_ENABLED
                )
            "PermissionManager.isAccessibilityEnabled accessibilityEnabled:$accessibilityEnabled".logD(
                TAG
            )
        } catch (e: Settings.SettingNotFoundException) {
            "PermissionManager.isAccessibilityEnabled Error finding setting, default accessibility to not found: ${e.message}".logD(
                TAG
            )
        }

        val mStringColonSplitter = TextUtils.SimpleStringSplitter(':')

        if (accessibilityEnabled == 1) {
            val settingValue = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue)
                while (mStringColonSplitter.hasNext()) {
                    val accessibilityService = mStringColonSplitter.next()
                    "PermissionManager.isAccessibilityEnabled accessibilityService:$accessibilityService".logD(
                        TAG
                    )
                    if (accessibilityService.equals(getServiceName(context), ignoreCase = true)) {
                        return true
                    }
                }
            }
        }
        "PermissionManager.isAccessibilityEnabled accessibilityFound:$accessibilityFound".logD(TAG)

        return accessibilityFound
    }

    private fun getServiceName(context: Context): String {
        return "${context.packageName}/${AppBot::class.java.canonicalName}"
    }

    private fun Intent.highlightSettingsTo(arg: String): Intent {
        putExtra(EXTRA_FRAGMENT_ARG_KEY, arg)
        val bundle = bundleOf(EXTRA_FRAGMENT_ARG_KEY to arg)
        putExtra(EXTRA_SHOW_FRAGMENT_ARGUMENTS, bundle)
        return this
    }

    @MainThread
    override fun openAccessibilityPermissionDialog(fragment: Fragment) {
        val showArgs = fragment.context?.packageName + "/" + AppBot::class.java.name
        val action = Settings.ACTION_ACCESSIBILITY_SETTINGS
        val permissionIntent = Intent(action).highlightSettingsTo(showArgs)
        try {
            fragment.startActivity(permissionIntent)
        } catch (e: Exception) {
            "openAccessibilityPermissionDialog.startActivity exception: ${e.message}".logD()
        }
    }

    @MainThread
    override fun openDrawOverlayPermissionDialog(fragment: Fragment) {
        val action = Settings.ACTION_MANAGE_OVERLAY_PERMISSION
        val uri = Uri.parse("package:${fragment.context?.packageName}")
        val intent = Intent(action, uri).highlightSettingsTo(EXTRA_SYSTEM_ALERT_WINDOW)
        try {
            fragment.startActivity(intent)
        } catch (e: Exception) {
            "openDrawOverlayPermissionDialog.startActivity exception: ${e.message}".logD()
        }
    }


    override fun Fragment.openDrawOverlayPermissionInstructionDialog() {
        val dialogIntent = Intent(context, PermissionGrantInstructionActivity::class.java)
        try {
            startActivity(dialogIntent)
        } catch (e: Exception) {
            //Not problem. User exit from settings
            "openDrawOverlayPermissionInstructionDialog.startActivity exception: ${e.message}".logD()
        }
    }

    override fun Fragment.openAccessibilityPermissionInstructionDialog() {
        val dialogIntent = Intent(context, PermissionGrantInstructionActivity::class.java)
        try {
            startActivity(dialogIntent)
        } catch (e: Exception) {
            //Not problem. User exit from settings
            "openAccessibilityPermissionInstructionDialog.startActivity exception: ${e.message}".logD()
        }
    }

    @MainThread
    private fun openAccessMiUiPermissionsDialog(fragment: Fragment) {
        MiUiTools.openPopupPermission(fragment.requireContext())
    }
}
