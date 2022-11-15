package k.studio.screen_driver.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import k.studio.screendriverktx.logD
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object MiUiTools {

    fun openPopupPermission(context: Context) {
        try {
            // MIUI 8
            val localIntent = Intent("miui.intent.action.APP_PERM_EDITOR")
            localIntent.setClassName(
                "com.miui.securitycenter",
                "com.miui.permcenter.permissions.PermissionsEditorActivity"
            )
            localIntent.putExtra("extra_pkgname", context.packageName)
            context.startActivity(localIntent)
        } catch (e: java.lang.Exception) {
            try {
                // MIUI 5/6/7
                val localIntent = Intent("miui.intent.action.APP_PERM_EDITOR")
                localIntent.setClassName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.permissions.AppPermissionsEditorActivity"
                )
                localIntent.putExtra("extra_pkgname", context.packageName)
                context.startActivity(localIntent)
            } catch (e1: java.lang.Exception) {
                // Otherwise jump to application details
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                context.startActivity(intent)
            }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun isIntentAvailable(
        intent: Intent?,
        context: Context,
    ): Boolean {
        var result = false
        if (intent != null) {
            val packageManager = context.packageManager
            val queryingFlag = PackageManager.MATCH_DEFAULT_ONLY
            val queryIntentActivities = packageManager.queryIntentActivities(intent, queryingFlag)
            if (queryIntentActivities.size > 0) {
                result = true
            }
        }
        return result
    }

    private fun getSystemProperty(propName: String): String? {
        val line: String
        var input: BufferedReader? = null
        try {
            val p = Runtime.getRuntime().exec("getprop $propName")
            input = BufferedReader(InputStreamReader(p.inputStream), 1024)
            line = input.readLine()
            input.close()
        } catch (ex: IOException) {
            return null
        } finally {
            if (input != null) {
                try {
                    input.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return line
    }

    fun isMiUi(): Boolean {
        return getSystemProperty("ro.miui.ui.version.name")?.isNotBlank() == true
    }

    fun getMiUiVersion(): Int {
        val version: String? = getSystemProperty("ro.miui.ui.version.name")
        version?.let {
            try {
                return version.substring(1).toInt()
            } catch (e: Exception) {
                "Get miui version code error, version : $version".logD()
            }
        }
        return -1
    }
}