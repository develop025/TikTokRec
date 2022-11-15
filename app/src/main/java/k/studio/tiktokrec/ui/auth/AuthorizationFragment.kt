package k.studio.tiktokrec.ui.auth

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import k.studio.tiktokrec.R
import k.studio.tiktokrec.databinding.FragmentAuthorizationBinding
import k.studio.tiktokrec.utils.FirebaseException

@AndroidEntryPoint
class AuthorizationFragment : BaseFragment() {

    private var _binding: FragmentAuthorizationBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var signInClient: SignInClient

    private val signInLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            handleSignInResult(result.data)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthorizationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            signInButton.setOnClickListener {
                val email = fieldEmail.text.toString()
                val password = fieldPassword.text.toString()
                if (validateForm())
                    signIn(email, password)
            }
            signUpButton.setOnClickListener {
                val email = binding.fieldEmail.text.toString()
                val password = binding.fieldPassword.text.toString()
                if (validateForm())
                    signUp(email, password)
            }
            googleAuthButton.setOnClickListener {
                googleAuth()
            }
        }

        signInClient = Identity.getSignInClient(requireContext())
        auth = Firebase.auth
    }

    private fun handleSignInResult(data: Intent?) {
        try {
            val credential = signInClient.getSignInCredentialFromIntent(data)
            val idToken = credential.googleIdToken
            firebaseAuthWithGoogle(idToken)
        } catch (e: ApiException) {
            val message = FirebaseException.getMessageRes(e)
            showErrorDialog(message)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        blockScreen()
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    navigate(NavigationDestination.GET_STARS)
                } else {
                    val message = FirebaseException.getMessageRes(task.exception)
                    showErrorDialog(message)
                }
                unblockScreen()
            }
    }

    private fun googleAuth() {
        val signInRequest: GetSignInIntentRequest = GetSignInIntentRequest.builder()
            .setServerClientId(getString(R.string.default_web_client_id))
            .build()

        signInClient.getSignInIntent(signInRequest)
            .addOnSuccessListener { pendingIntent ->
                launchSignIn(pendingIntent)
            }
            .addOnFailureListener { e ->
                val message = FirebaseException.getMessageRes(e)
                showErrorDialog(message)
                showErrorDialog(R.string.google_authorization_fail)
            }
    }

    private fun launchSignIn(pendingIntent: PendingIntent) {
        try {
            val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent).build()
            signInLauncher.launch(intentSenderRequest)
        } catch (e: Exception) {
            val message = FirebaseException.getMessageRes(e)
            showErrorDialog(message)
            showErrorDialog(R.string.firebase_unknown_error)
        }
    }

    private fun signIn(email: String, password: String) {
        blockScreen()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    auth.currentUser?.let { user ->
                        if (!user.isEmailVerified)
                            navigate(NavigationDestination.EMAIL_VERIFICATION)
                        else
                            navigate(NavigationDestination.GET_STARS)
                    }
                } else {
                    val errorRes = FirebaseException.getMessageRes(task.exception)
                    showErrorDialog(errorRes)
                }
                unblockScreen()
            }
    }

    private fun signUp(email: String, password: String) {
        blockScreen()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    auth.currentUser?.let { user ->
                        if (!user.isEmailVerified)
                            sendEmailVerification()
                        else
                            navigate(NavigationDestination.GET_STARS)
                    }
                } else {
                    val errorRes = FirebaseException.getMessageRes(task.exception)
                    showErrorDialog(errorRes)
                }

                unblockScreen()
            }
    }

    private fun sendEmailVerification() {
        blockScreen()

        val user = auth.currentUser!!
        user.sendEmailVerification()
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    navigate(NavigationDestination.EMAIL_VERIFICATION)
                } else {
                    val errorRes = FirebaseException.getMessageRes(task.exception)
                    showErrorDialog(errorRes)
                }
            }
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = binding.fieldEmail.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding.fieldEmail.error = "Required."
            valid = false
        } else {
            binding.fieldEmail.error = null
        }

        val password = binding.fieldPassword.text.toString()
        if (TextUtils.isEmpty(password)) {
            binding.fieldPassword.error = "Required."
            valid = false
        } else {
            binding.fieldPassword.error = null
        }

        return valid
    }

    private fun navigate(navigationDestination: NavigationDestination) {
        val navDirection = when (navigationDestination) {
            NavigationDestination.EMAIL_VERIFICATION -> R.id.action_emailAuthFragment_to_emailVerificationFragment
            NavigationDestination.GET_STARS -> R.id.action_emailAuthFragment_to_getStarsFragment
        }

        findNavController().navigate(navDirection)
    }

    enum class NavigationDestination {
        EMAIL_VERIFICATION,
        GET_STARS
    }
}