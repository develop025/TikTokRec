package k.studio.tiktokrec.ui.auth

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import k.studio.tiktokrec.BuildConfig
import k.studio.tiktokrec.R
import k.studio.tiktokrec.data.error.ErrorCodeIsTikTokUserExist
import k.studio.tiktokrec.data.error.ErrorsResources
import k.studio.tiktokrec.databinding.FragmentTikTokAuthBinding
import k.studio.tiktokrec.ui.main.ScreenState
import k.studio.tiktokrec.utils.logD


@AndroidEntryPoint
class TikTokAuthFragment : BaseFragment() {

    private var _binding: FragmentTikTokAuthBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<TikTokAuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTikTokAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (BuildConfig.DEBUG)
            binding.username.setText("lesfleuresss")//develop025 //lesfleuresss

        binding.enter.setOnClickListener {
            setScreenState(ScreenState.NOT_TOUCHABLE)

            val username = binding.username.text.toString()

            binding.webView.apply {
                settings.javaScriptEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        "webViewClient.onPageStarted".logD()
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        "webViewClient.onPageFinished".logD()
                        view?.evaluateJavascript("(function(){return window.document.body.outerHTML})();") { page ->
                            "page:$page".logD()
                            if (page.contains("user-title")) {
                                viewModel.onUsernameValidated(username)
                            } else {
                                showDialog(ErrorsResources.getResource(ErrorCodeIsTikTokUserExist.USERNAME_NOT_FOUND))
                            }
                        }
                        setScreenState(ScreenState.TOUCHABLE)
                    }
                }
                loadUrl("https://www.tiktok.com/@$username")
            }
        }

        viewModel.navigationDestination.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { navigationDestination ->
                val navDirection = when (navigationDestination) {
                    NavigationDestination.AUTHORIZATION -> R.id.action_tikTokAuthFragment_to_authorizationFragment
                    NavigationDestination.EMAIL_VERIFICATION -> R.id.action_tikTokAuthFragment_to_emailVerificationFragment
                    NavigationDestination.GET_STARS -> R.id.action_tikTokAuthFragment_to_getStarsFragment
                }

                findNavController().navigate(navDirection)
            }
        }

        viewModel.errorMessageRes.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { errorMessageRes ->
                showErrorDialog(errorMessageRes)
            }
        }

        viewModel.screenState.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { screenState ->
                setScreenState(screenState)
            }
        }
    }
}