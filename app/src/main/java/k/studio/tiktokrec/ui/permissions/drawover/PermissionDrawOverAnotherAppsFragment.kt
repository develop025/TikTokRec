package k.studio.tiktokrec.ui.permissions.drawover

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
import k.studio.tiktokrec.databinding.FragmentPermissionDrawOverAnotherAppsBinding
import k.studio.tiktokrec.ui.delegate.permission.IPermissionManager
import k.studio.tiktokrec.ui.delegate.permission.PermissionManager
import k.studio.tiktokrec.utils.logD
import kotlinx.coroutines.*

@AndroidEntryPoint
class PermissionDrawOverAnotherAppsFragment :
    Fragment(),
    IPermissionManager by PermissionManager() {

    private var _binding: FragmentPermissionDrawOverAnotherAppsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<PermissionDrawOverAnotherAppsViewModel>()

    private var openInstructionDialogJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPermissionDrawOverAnotherAppsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.grantPermissionButton.setOnClickListener {
            openDrawOverlayPermissionDialog(this)
            openInstructionDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        "onResume".logD()
        if (isDrawOverlayEnabled(requireContext()))
            findNavController().navigate(R.id.action_permissionDrawOverAnotherAppsFragment_to_getStarsFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openInstructionDialog() {
        lifecycleScope.launch(Dispatchers.IO) {
            var openDialog = true
            launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    openDialog = false
                    openInstructionDialogJob?.cancelAndJoin()
                }
            }
            delay(500)
            if (openDialog)
                openDrawOverlayPermissionInstructionDialog()
        }
    }
}