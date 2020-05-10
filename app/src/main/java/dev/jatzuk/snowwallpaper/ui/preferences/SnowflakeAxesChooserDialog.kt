package dev.jatzuk.snowwallpaper.ui.preferences

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
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
//        todo dialog view background color
        return AlertDialog.Builder(context!!).run {
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.fragment_dialog, null).apply {
                val axisX = findViewById<CheckBox>(R.id.axesX)
                val axisY = findViewById<CheckBox>(R.id.axesY)
                val axisZ = findViewById<CheckBox>(R.id.axesZ)

                axisX.isChecked = availableAxes[0]
                axisY.isChecked = availableAxes[1]
                axisZ.isChecked = availableAxes[2]

                axisX.setOnCheckedChangeListener { _, isChecked ->
                    preferenceRepository.setSnowflakeRotationAxisXAvailability(isChecked)
                }

                axisY.setOnCheckedChangeListener { _, isChecked ->
                    preferenceRepository.setSnowflakeRotationAxisYAvailability(isChecked)
                }

                axisZ.setOnCheckedChangeListener { _, isChecked ->
                    preferenceRepository.setSnowflakeRotationAxisZAvailability(isChecked)
                }
            }

            setView(view)
            setTitle(getString(R.string.snowflake_rotation_axes_title))
            setPositiveButton(getString(R.string.dialog_positive_button)) { _, _ -> }
            setNegativeButton(getString(R.string.dialog_negative_button)) { _, _ -> }
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
