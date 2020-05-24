package dev.jatzuk.snowwallpaper.ui.preferences

import android.annotation.SuppressLint
import android.app.Dialog
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

class SnowflakeAxesChooserDialog : DialogFragment() {

    private lateinit var preferenceRepository: PreferenceRepository
    private lateinit var availableAxes: BooleanArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferenceRepository = PreferenceRepository.getInstance(context!!)
        availableAxes = preferenceRepository.getSnowflakeAvailableRotationAxes()
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context!!).run {
            setTitle(getString(R.string.snowflake_rotation_axes_title))
            setMultiChoiceItems(
                R.array.snowflake_axis_rotations_info,
                preferenceRepository.getSnowflakeAvailableRotationAxes()
            ) { _, which, isChecked ->
                when (which) {
                    0 -> preferenceRepository.setSnowflakeRotationAxisXAvailability(isChecked)
                    1 -> preferenceRepository.setSnowflakeRotationAxisYAvailability(isChecked)
                    2 -> preferenceRepository.setSnowflakeRotationAxisZAvailability(isChecked)
                }
            }
            setPositiveButton(getString(android.R.string.ok)) { _, _ -> }
            create()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        TODO("customize dialog")
        dialog?.window?.setBackgroundDrawable(
            ColorDrawable(ContextCompat.getColor(context!!, R.color.colorPreferenceTitle))
        )
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}
