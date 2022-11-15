package k.studio.tiktokrec.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import k.studio.tiktokrec.R
import k.studio.tiktokrec.databinding.FragmentEmailVerificationBinding
import k.studio.tiktokrec.utils.FirebaseException
import k.studio.tiktokrec.utils.logW

@AndroidEntryPoint
class EmailVerificationFragment : BaseFragment() {

    private var _binding: FragmentEmailVerificationBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmailVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        checkVerification()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth

        with(binding) {
            title.text = getStartDescription()

            checkButton.setOnClickListener {
                blockScreen()
                auth.currentUser?.reload()?.addOnCompleteListener { task ->
                    unblockScreen()
                    if (task.isSuccessful) {
                        title.text = getReloadDescription()
                    } else {
                        val messageRes = FirebaseException.getMessageRes(task.exception)
                        showErrorDialog(messageRes)
                    }
                    checkVerification()
                }
            }

            resendButton.setOnClickListener {
                auth.currentUser?.let { user ->
                    blockScreen()
                    user.sendEmailVerification()
                        .addOnCompleteListener(requireActivity()) { task ->
                            unblockScreen()
                            if (task.isSuccessful) {
                                val text = getVerificationEmailSentMessage()
                                showDialog(text)
                            } else {
                                val messageRes = FirebaseException.getMessageRes(task.exception)
                                showErrorDialog(messageRes)
                                showErrorDialog(R.string.verification_email_sent_error)
                            }
                            checkVerification()
                        }
                }
            }
        }

        FirebaseAuth.getInstance().addIdTokenListener(
            FirebaseAuth.IdTokenListener {
                // Log out
                if (it.currentUser == null)
                    navigate(NavigationDestination.AUTHORIZATION)
            })
    }

    private fun getVerificationEmailSentMessage(): String {
        val email = auth.currentUser?.email
        return String.format(resources.getString(R.string.verification_email_sent), email)
    }

    private fun getReloadDescription(): String {
        val email = auth.currentUser?.email
        return String.format(resources.getString(R.string.email_yet_not_verified), email)
    }

    private fun getStartDescription(): String {
        val email = auth.currentUser?.email
        return String.format(resources.getString(R.string.confirm_email), email)
    }

    private fun checkVerification() {
        validUserData().let { validUserData ->
            when (validUserData) {
                ValidUserData.USER_NOT_EXIST -> {
                    showErrorDialog(R.string.user_not_authorized) {
                        signOut()
                    }
                }
                ValidUserData.EMAIL_NOT_EXIST -> {
                    showErrorDialog(R.string.email_not_exist) {
                        signOut()
                    }
                }
                ValidUserData.USER_VALID -> {
                }

                ValidUserData.EMAIL_VERIFIED -> {
                    navigate(NavigationDestination.GET_STARS)
                }
            }
        }
    }

    private fun validUserData(): ValidUserData {
        return if (auth.currentUser == null) {
            "EmailVerificationFragment currentUser not exist".logW()
            ValidUserData.USER_NOT_EXIST
        } else if (auth.currentUser != null && auth.currentUser?.email == null) {
            "EmailVerificationFragment currentUser exist but email not exist".logW()
            ValidUserData.EMAIL_NOT_EXIST
        } else if (auth.currentUser?.isEmailVerified == true)
            ValidUserData.EMAIL_VERIFIED
        else
            ValidUserData.USER_VALID
    }

    private fun navigate(navigationDestination: NavigationDestination) {
        val navDirection = when (navigationDestination) {
            NavigationDestination.GET_STARS -> R.id.action_emailVerificationFragment_to_getStarsFragment
            NavigationDestination.AUTHORIZATION -> R.id.action_emailVerificationFragment_to_authorizationFragment
        }

        findNavController().navigate(navDirection)
    }

    private fun signOut() {
        auth.signOut()
    }

    enum class NavigationDestination {
        AUTHORIZATION,
        GET_STARS
    }

    enum class ValidUserData {
        USER_NOT_EXIST,
        EMAIL_NOT_EXIST,
        EMAIL_VERIFIED,
        USER_VALID
    }
}