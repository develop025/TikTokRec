package k.studio.tiktokrec.ui.splash

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import k.studio.tiktokrec.R
import k.studio.tiktokrec.databinding.FragmentSplashBinding

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<SplashViewModel>()

    private var allowNavigate: Boolean = false
    private var navDirection: Int? = null

    private var timer: CountDownTimer = object : CountDownTimer(2000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
        }

        override fun onFinish() {
            allowNavigate = true
            navDirection?.let {
                findNavController().navigate(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        timer.start()
    }

    override fun onPause() {
        super.onPause()
        timer.cancel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.navigationDestination.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { navigationDestination ->
                navDirection = when (navigationDestination) {
                    NavigationDestination.EMAIL_REGISTER -> R.id.action_splashFragment_to_authorizationFragment
                    NavigationDestination.EMAIL_VERIFICATION -> R.id.action_splashFragment_to_emailVerificationFragment
                    NavigationDestination.TIK_TOK_AUTH -> R.id.action_splashFragment_to_tikTokAuthFragment
                    NavigationDestination.GET_STARS -> R.id.action_splashFragment_to_getStarsFragment
                }
                if (allowNavigate) {
                    navDirection?.let { direction ->
                        findNavController().navigate(direction)
                    }
                }
            }
        }

        val animation: Animation = AlphaAnimation(1f, 0f)
        animation.duration = 1000
        animation.interpolator = LinearInterpolator()
        animation.repeatCount = Animation.INFINITE
        animation.repeatMode = Animation.REVERSE

        binding.logo.startAnimation(animation)
    }
}