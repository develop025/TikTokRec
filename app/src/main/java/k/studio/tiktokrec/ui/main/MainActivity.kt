package k.studio.tiktokrec.ui.main

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import k.studio.tiktokrec.R
import k.studio.tiktokrec.databinding.ActivityMainBinding
import k.studio.tiktokrec.utils.gone
import k.studio.tiktokrec.utils.show


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), IScreenState {

    private lateinit var binding: ActivityMainBinding

    private val destinationListener =
        NavController.OnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.splashFragment -> {
                    hideBottomBar()
                    showContentBehindKeyboard()
                }
                R.id.authorizationFragment -> {
                    hideBottomBar()
                    showContentBehindKeyboard()
                }
                R.id.emailVerificationFragment -> {
                    hideBottomBar()
                    showContentBehindKeyboard()
                }
                R.id.tikTokAuthFragment -> {
                    hideBottomBar()
                    showContentBehindKeyboard()
                }
                R.id.permissionAccessibilityServiceFragment -> {
                    hideBottomBar()
                    showContentBehindKeyboard()
                }
                R.id.permissionDrawOverAnotherAppsFragment -> {
                    hideBottomBar()
                    showContentBehindKeyboard()
                }
                else -> {
                    showBottomBar()
                    hideContentBehindKeyboard()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment? ?: return
        val navController = host.navController

        setupBottomNavMenu(navController)

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            binding.root.setOnApplyWindowInsetsListener { _, windowInsets ->
//                val imeHeight = windowInsets.getInsets(WindowInsets.Type.ime()).bottom
//                binding.root.setPadding(0, 0, 0, imeHeight)
//
//                windowInsets
//            }
//        }

        navController.addOnDestinationChangedListener(destinationListener)
    }

    private fun setupBottomNavMenu(navController: NavController) {
        binding.bottomNavView.setupWithNavController(navController)
    }

    private fun showBottomBar() {
        binding.bottomNavView.show()
    }

    private fun hideBottomBar() {
        binding.bottomNavView.gone()
    }

    //TODO: fix SOFT_INPUT_ADJUST_RESIZE
    private fun showContentBehindKeyboard() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
//            window.setDecorFitsSystemWindows(false)
//        else
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    private fun hideContentBehindKeyboard() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
//            window.setDecorFitsSystemWindows(true)
//        else
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun setState(screenState: ScreenState) {
        when (screenState) {
            ScreenState.TOUCHABLE -> {
                binding.progressBarContainer.visibility = View.GONE
                window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
            ScreenState.NOT_TOUCHABLE -> {
                binding.progressBarContainer.visibility = View.VISIBLE
                window?.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            }
        }
    }
}

interface IScreenState {
    fun setState(screenState: ScreenState)
}

enum class ScreenState {
    TOUCHABLE,
    NOT_TOUCHABLE
}

fun Activity?.asScreenState(): IScreenState? {
    return if (this is IScreenState)
        this
    else
        null
}