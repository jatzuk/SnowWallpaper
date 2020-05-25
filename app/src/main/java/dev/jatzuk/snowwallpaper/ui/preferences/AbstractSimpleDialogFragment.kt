package dev.jatzuk.snowwallpaper.ui.preferences

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import dev.jatzuk.snowwallpaper.R
import dev.jatzuk.snowwallpaper.data.preferences.PreferenceRepository
import dev.jatzuk.snowwallpaper.utilities.Logger.errorLog

abstract class AbstractSimpleDialogFragment(
    private val title: String,
    private val message: String? = null,
    private val hasMultiChoiceItems: Boolean = false
) : DialogFragment() {

    private var positiveActionCallback: (() -> Unit)? = null
    private var negativeActionCallback: (() -> Unit)? = null

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

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(
            ColorDrawable(ContextCompat.getColor(context!!, provideBackgroundColor()))
        )
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun invokeOnPositiveAction() {
        positiveActionCallback?.invoke() ?: errorLog("Positive action callback is null", TAG)
    }

    private fun invokeOnNegativeAction() {
        negativeActionCallback?.invoke() ?: errorLog("Negative action callback is null", TAG)
    }

    protected open fun provideBackgroundColor(): Int = R.color.colorPreferenceCategoryBackground

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
    }
}

//Fragment null must be a public static class to be  properly recreated from instance state.
class ResetPreferenceDialogFragment(
    title: String,
    message: String
) : AbstractSimpleDialogFragment(title, message)

class SnowflakeAxesChooserDialog(title: String) :
    AbstractSimpleDialogFragment(title, hasMultiChoiceItems = true) {

    private val preferenceRepository: PreferenceRepository by lazy {
        PreferenceRepository.getInstance(activity!!)
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
}
