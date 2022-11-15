package k.studio.tiktokrec.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import k.studio.tiktokrec.R
import k.studio.tiktokrec.databinding.FragmentGetStarsBinding
import k.studio.tiktokrec.ui.delegate.permission.IPermissionManager
import k.studio.tiktokrec.ui.delegate.permission.PermissionManager
import k.studio.tiktokrec.utils.logD

/**
 * DelegateLifecycleBinder - detect service is start
 * DelegateLifecycleBotHelper - bind service and manage service
 */
@AndroidEntryPoint
class GetStarsFragment : Fragment(),
    IPermissionManager by PermissionManager() {
    //TODO move service manage to AS
    /*LifecycleBotHelper<AppForegroundService> by DelegateLifecycleBotHelper(),
    LifecycleBinder<AppForegroundService, ServiceBinder<AppForegroundService>> by DelegateLifecycleBinder()*/

    private var _binding: FragmentGetStarsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<GetStarsViewModel>()

    //TODO move service manage to AS
    /*private var stopAppForegroundServiceOnBind = AtomicBoolean(false)*/

    override fun onResume() {
        super.onResume()
        //TODO move service manage to AS
        /* "GetStarsFragment.onResume bindService & setStartServiceListener".logD()
         bindService(requireContext(), AppForegroundService::class.java)

         setStartServiceListener(object : LifecycleBotHelper.IParentInteract {
             override fun bindService(intentService: Intent) {
                 "GetStarsFragment.bindService action:${intentService.action}".logD()
             }
         })*/

        //TODO: impl check for MIUI
        if (!isAccessibilityEnabled(requireContext()))
            navigate(NavigationDestination.PERMISSION_ACCESSIBILITY_SERVICE)
        else if (!isDrawOverlayEnabled(requireContext()))
            navigate(NavigationDestination.PERMISSION_DRAW_OVER)
        //TODO move service manage to AS
        /* else if (boundLiveData.value == true)
             stopService(requireActivity(), AppForegroundService::class.java)
         else {
             "stopAppForegroundServiceOnBind = true".logD()
             stopAppForegroundServiceOnBind.set(true)
         }*/
        "GetStarsFragment.onResume stopAction".logD()
        viewModel.stopAction()
    }

    override fun onPause() {
        super.onPause()
        //TODO move service manage to AS
        /*unbindService(requireContext())*/
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGetStarsBinding.inflate(inflater, container, false)
        return binding.root
    }

    //TODO move service manage to AS
    /*private val boundAppForegroundServiceObserver: (bound: Boolean) -> Unit = { bound ->
        "boundAppForegroundServiceObserver:$bound".logD()
        if (bound && stopAppForegroundServiceOnBind.get()) {
            "boundAppForegroundServiceObserver.stopService".logD()
            stopService(requireActivity(), AppForegroundService::class.java)
        }
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        "GetStarsFragment.onViewCreated".logD()

        //TODO move service manage to AS
        /*boundLiveData.observe(viewLifecycleOwner, boundAppForegroundServiceObserver)*/

        binding.startButton.setOnClickListener {
            //TODO move service manage to AS
            /*stopAppForegroundServiceOnBind.set(false)
            "stopAppForegroundServiceOnBind = false".logD()
            startService(requireActivity(), AppForegroundService::class.java)*/
            viewModel.startAction()
            requireActivity().finishAffinity()
        }

        binding.clearUniqueButton.setOnClickListener {
            viewModel.clearUniqueActions()
        }

        binding.clearOrdersButton.setOnClickListener {
            viewModel.clearOrders()
        }

        binding.resetOrderAtButton.setOnClickListener {
            viewModel.resetOrderAt()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun navigate(navigationDestination: NavigationDestination) {
        val navDirection = when (navigationDestination) {
            NavigationDestination.PERMISSION_ACCESSIBILITY_SERVICE -> R.id.action_getStarsFragment_to_permissionAccessibilityServiceFragment
            NavigationDestination.PERMISSION_DRAW_OVER -> R.id.action_getStarsFragment_to_permissionDrawOverAnotherAppsFragment
        }

        findNavController().navigate(navDirection)
    }

    enum class NavigationDestination {
        PERMISSION_ACCESSIBILITY_SERVICE,
        PERMISSION_DRAW_OVER
    }
}

