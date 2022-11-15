package k.studio.tiktokrec.ui.home

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import k.studio.tiktokrec.R
import k.studio.tiktokrec.databinding.FragmentPromoteBinding
import k.studio.tiktokrec.ui.auth.BaseFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PromoteFragment : BaseFragment() {

    private var _binding: FragmentPromoteBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<PromoteViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPromoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.subscribeRemoteOrders()
    }

    override fun onPause() {
        super.onPause()
        viewModel.unsubscribeRemoteOrders()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = OrderHeartPagingDataAdapter()

        with(binding) {
            windUp100LikesButton.setOnClickListener {
                if (validateForm())
                    viewModel.windUpLikes(fieldVideoLink.text.toString(), 100)
            }

            //TODO: for test
            fieldVideoLink.setText("https://www.tiktok.com/@y.yevchenko/video/7129186325713931525")

            orders.adapter = adapter
        }

        lifecycleScope.launch {
            viewModel.flowOrdersHearts.collectLatest(adapter::submitData)
        }

        viewModel.navigationDestination.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { navigationDestination ->
                when (navigationDestination) {
                    PromoteViewModel.NavigationDestination.TIK_TOK_AUTH -> {
                        findNavController().navigate(R.id.action_promoteFragment_to_tikTokAuthFragment)
                    }
                }
            }
        }

        viewModel.errorMessageRes.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { errorMessageRes ->
                showErrorDialog(errorMessageRes)
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { errorMessage ->
                showErrorDialog(errorMessage)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun validateForm(): Boolean {
        var valid = true

        val videoLink = binding.fieldVideoLink.text.toString()
        if (TextUtils.isEmpty(videoLink)) {
            binding.fieldVideoLink.error = "Required."
            valid = false
        } else {
            binding.fieldVideoLink.error = null
        }

        return valid
    }
}