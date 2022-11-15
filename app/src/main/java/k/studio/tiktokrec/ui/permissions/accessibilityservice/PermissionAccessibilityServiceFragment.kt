package k.studio.tiktokrec.ui.permissions.accessibilityservice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import k.studio.tiktokrec.R
import k.studio.tiktokrec.databinding.FragmentPermissionAccessibilityBinding
import k.studio.tiktokrec.ui.delegate.permission.IPermissionManager
import k.studio.tiktokrec.ui.delegate.permission.PermissionManager
import kotlinx.coroutines.*

@AndroidEntryPoint
class PermissionAccessibilityServiceFragment :
    Fragment(),
    IPermissionManager by PermissionManager() {

    private var _binding: FragmentPermissionAccessibilityBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<PermissionAccessibilityViewModel>()

    private var openInstructionDialogJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPermissionAccessibilityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (isAccessibilityEnabled(requireContext()))
            findNavController().navigate(R.id.action_permissionAccessibilityServiceFragment_to_getStarsFragment)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.grantPermissionButton.setOnClickListener {
            openAccessibilityPermissionDialog(this)
            openInstructionDialog()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openInstructionDialog() {
        openInstructionDialogJob = lifecycleScope.launch(Dispatchers.IO) {
            var openDialog = true
            launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    openDialog = false
                    openInstructionDialogJob?.cancelAndJoin()
                }
            }
            delay(500)
            if (openDialog)
                openAccessibilityPermissionInstructionDialog()
        }
    }
}