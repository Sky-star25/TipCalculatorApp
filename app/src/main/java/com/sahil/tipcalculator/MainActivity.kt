package com.sahil.tipcalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import kotlin.math.ceil

class MainActivity : AppCompatActivity() {

    private lateinit var billInput: EditText
    private lateinit var tipBar: SeekBar
    private lateinit var tipPercentLabel: TextView
    private lateinit var tipDesc: TextView

    private lateinit var splitInput: EditText
    private lateinit var roundSwitch: Switch

    private lateinit var tipOutput: TextView
    private lateinit var totalOutput: TextView
    private lateinit var perPersonOutput: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setListeners()
        updateResults()
    }

    private fun initViews() {
        billInput = findViewById(R.id.etBase)
        tipBar = findViewById(R.id.seekBarTip)
        tipPercentLabel = findViewById(R.id.tvTipPercentValue)
        tipDesc = findViewById(R.id.tvTipDescription)

        splitInput = findViewById(R.id.etSplitCount)
        roundSwitch = findViewById(R.id.switchRoundTotal)

        tipOutput = findViewById(R.id.tvTipAmount)
        totalOutput = findViewById(R.id.tvTotalAmount)
        perPersonOutput = findViewById(R.id.tvPerPersonAmount)
    }

    private fun setListeners() {

        billInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateResults()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        splitInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateResults()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        tipBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekbar: SeekBar?, percent: Int, fromUser: Boolean) {
                tipPercentLabel.text = "$percent%"
                updateTipDescription(percent)
                updateResults()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        roundSwitch.setOnCheckedChangeListener { _, _ ->
            updateResults()
        }
    }

    private fun updateResults() {
        val bill = getBill()
        val tipPercent = tipBar.progress
        val split = getSplitCount()
        val roundUp = roundSwitch.isChecked

        val tip = bill * tipPercent / 100.0
        var total = bill + tip

        if (roundUp) {
            total = ceil(total)
        }

        val perPerson = if (split > 0) total / split else 0.0

        tipOutput.text = String.format("Tip: $%.2f", tip)
        totalOutput.text = String.format("Total: $%.2f", total)
        perPersonOutput.text = String.format("Per person: $%.2f", perPerson)
    }

    private fun updateTipDescription(percent: Int) {
        val resId = when {
            percent < 10 -> R.string.tip_description_poor
            percent < 15 -> R.string.tip_description_ok
            percent < 20 -> R.string.tip_description_good
            else -> R.string.tip_description_great
        }
        tipDesc.setText(resId)
    }

    private fun getBill(): Double {
        val text = billInput.text.toString().trim()
        return text.toDoubleOrNull() ?: 0.0
    }

    private fun getSplitCount(): Int {
        val text = splitInput.text.toString().trim()
        val num = text.toIntOrNull() ?: 1
        return if (num <= 0) 1 else num
    }
}
