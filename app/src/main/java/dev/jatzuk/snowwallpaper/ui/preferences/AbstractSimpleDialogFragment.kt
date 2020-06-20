package dev.jatzuk.snowwallpaper.ui.preferences

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository
import dev.jatzuk.snowwallpaper.utilities.Logger.errorLog

abstract class AbstractSimpleDialogFragment : DialogFragment() {

    private lateinit var title: String
    private var message: String? = null
    private var hasMultiChoiceItems = false
    private var positiveActionCallback: (() -> Unit)? = null
    private var negativeActionCallback: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(TITLE_ARGUMENT_KEY) ?: getString(R.string.no_title)
            message = it.getString(MESSAGE_ARGUMENT_KEY)
            hasMultiChoiceItems = it.getBoolean(MULTI_CHOICE_KEY)
        }
    }

    final override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it, R.style.DefaultAlertDialog)
            builder
                .setTitle(title)
                .setPositiveButton(android.R.string.ok) { _, _ -> invokeOnPositiveAction() }
                .setNegativeButton(android.R.string.cancel) { _, _ -> invokeOnNegativeAction() }

            message?.let { msg -> builder.setMessage(msg) }

            if (hasMultiChoiceItems) builder.addMultiChoiceItems()

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun invokeOnPositiveAction() {
        positiveActionCallback?.invoke() ?: errorLog("Positive action callback is null", TAG)
    }

    private fun invokeOnNegativeAction() {
        negativeActionCallback?.invoke() ?: errorLog("Negative action callback is null", TAG)
        dismiss()
    }

    open fun invokeOnPositiveAction(callback: () -> Unit) {
        positiveActionCallback = callback
    }

    open fun invokeOnNegativeAction(callback: () -> Unit) {
        negativeActionCallback = callback
    }

    private fun AlertDialog.Builder.addMultiChoiceItems(): AlertDialog.Builder {
        setMultiChoiceItems(
            R.array.snowflake_axis_rotations_info,
            provideCheckedItems(),
            provideDialogListener()
        )
        return this
    }

    open fun provideDialogListener(): DialogInterface.OnMultiChoiceClickListener? = null

    open fun provideCheckedItems(): BooleanArray? = null

    companion object {
        private const val TAG = "AbstractSimpleDialogFragment"
        const val TITLE_ARGUMENT_KEY = "TITLE_ARGUMENT_KEY"
        const val MESSAGE_ARGUMENT_KEY = "MESSAGE_ARGUMENT_KEY"
        const val MULTI_CHOICE_KEY = "MULTI_CHOICE_KEY"
    }
}

//Fragment null must be a public static class to be  properly recreated from instance state.
class ResetPreferenceDialogFragment : AbstractSimpleDialogFragment() {

    companion object {
        fun newInstance(title: String, message: String): AbstractSimpleDialogFragment {
            return ResetPreferenceDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(TITLE_ARGUMENT_KEY, title)
                    putString(MESSAGE_ARGUMENT_KEY, message)
                }
            }
        }
    }
}

class SnowflakeAxesChooserDialog : AbstractSimpleDialogFragment() {

    private lateinit var preferenceRepository: PreferenceRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceRepository = PreferenceRepository.getInstance(requireContext())
    }

    override fun invokeOnPositiveAction(callback: () -> Unit) {}

    override fun invokeOnNegativeAction(callback: () -> Unit) {}

    override fun provideCheckedItems(): BooleanArray? =
        preferenceRepository.getSnowflakeAvailableRotationAxes()

    override fun provideDialogListener(): DialogInterface.OnMultiChoiceClickListener? {
        return DialogInterface.OnMultiChoiceClickListener { _, which, isChecked ->
            when (which) {
                0 -> preferenceRepository.setSnowflakeRotationAxisXAvailability(isChecked)
                1 -> preferenceRepository.setSnowflakeRotationAxisYAvailability(isChecked)
                2 -> preferenceRepository.setSnowflakeRotationAxisZAvailability(isChecked)
            }
        }
    }

    companion object {
        fun newInstance(title: String): AbstractSimpleDialogFragment {
            return SnowflakeAxesChooserDialog().apply {
                arguments = Bundle().apply {
                    putString(TITLE_ARGUMENT_KEY, title)
                    putBoolean(MULTI_CHOICE_KEY, true)
                }
            }
        }
    }
}
