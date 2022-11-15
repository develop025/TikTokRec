package k.studio.tiktokrec.ui.auth

import android.app.AlertDialog
import androidx.fragment.app.Fragment
import k.studio.tiktokrec.R
import k.studio.tiktokrec.ui.main.ScreenState
import k.studio.tiktokrec.ui.main.asScreenState

abstract class BaseFragment : Fragment() {

    protected fun showErrorDialog(message: String, onClickButton: (() -> Unit)? = null) {
        val builder: AlertDialog.Builder? = activity?.let {
            AlertDialog.Builder(it)
        }

        builder?.apply {
            setMessage(message)
            setTitle(R.string.error_dialog_title)
            setPositiveButton(
                android.R.string.ok
            ) { dialog, _ ->
                dialog.dismiss()
                onClickButton?.invoke()
            }
        }

        val dialog: AlertDialog? = builder?.create()
        dialog?.show()
    }

    protected fun showErrorDialog(messageRes: Int, onClickButton: (() -> Unit)? = null) {
        val builder: AlertDialog.Builder? = activity?.let {
            AlertDialog.Builder(it)
        }

        builder?.apply {
            setMessage(messageRes)
            setTitle(R.string.error_dialog_title)
            setPositiveButton(
                android.R.string.ok
            ) { dialog, _ ->
                dialog.dismiss()
                onClickButton?.invoke()
            }
        }

        val dialog: AlertDialog? = builder?.create()
        dialog?.show()
    }

    protected fun showDialog(messageRes: Int) {
        val builder: AlertDialog.Builder? = activity?.let {
            AlertDialog.Builder(it)
        }

        builder?.apply {
            setMessage(messageRes)
            setTitle(R.string.dialog_title)
            setPositiveButton(
                android.R.string.ok
            ) { dialog, _ -> dialog.dismiss() }
        }

        val dialog: AlertDialog? = builder?.create()
        dialog?.show()
    }

    protected fun showDialog(message: String) {
        val builder: AlertDialog.Builder? = activity?.let {
            AlertDialog.Builder(it)
        }

        builder?.apply {
            setMessage(message)
            setTitle(R.string.dialog_title)
            setPositiveButton(
                android.R.string.ok
            ) { dialog, _ -> dialog.dismiss() }
        }

        val dialog: AlertDialog? = builder?.create()
        dialog?.show()
    }

    protected fun setScreenState(screenState: ScreenState) {
        activity.asScreenState()?.setState(screenState)
    }

    protected fun blockScreen() {
        activity.asScreenState()?.setState(ScreenState.NOT_TOUCHABLE)
    }

    protected fun unblockScreen() {
        activity.asScreenState()?.setState(ScreenState.TOUCHABLE)
    }
}