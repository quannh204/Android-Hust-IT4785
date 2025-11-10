package com.example.bai1_04_11

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var fromSpinner: Spinner
    private lateinit var toSpinner: Spinner
    private lateinit var fromAmount: EditText
    private lateinit var toAmount: EditText
    private lateinit var swapButton: ImageView
    
    // Exchange rates with respect to USD
    private val exchangeRates = mapOf(
        "USD" to 1.0,
        "EUR" to 0.85,
        "JPY" to 110.0,
        "GBP" to 0.73,
        "AUD" to 1.35,
        "CAD" to 1.25,
        "CHF" to 0.92,
        "CNY" to 6.45,
        "SGD" to 1.35,
        "VND" to 23000.0
    )

    private var isUpdatingFromAmount = false
    private var isUpdatingToAmount = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        fromSpinner = findViewById(R.id.fromCurrencySpinner)
        toSpinner = findViewById(R.id.toCurrencySpinner)
        fromAmount = findViewById(R.id.fromAmountEditText)
        toAmount = findViewById(R.id.toAmountEditText)
        swapButton = findViewById(R.id.swapButton)

        // Set up spinners
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.currencies,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        fromSpinner.adapter = adapter
        toSpinner.adapter = adapter

        // Set default selections
        fromSpinner.setSelection(0) // USD
        toSpinner.setSelection(9)   // VND

        // Set up listeners
        setupListeners()
    }

    private fun setupListeners() {
        // Spinner change listeners
        val spinnerListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateConversion()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        fromSpinner.onItemSelectedListener = spinnerListener
        toSpinner.onItemSelectedListener = spinnerListener

        // Amount change listener
        fromAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (!isUpdatingFromAmount) {
                    updateConversion()
                }
            }
        })

        // Swap button listener
        swapButton.setOnClickListener {
            val fromPos = fromSpinner.selectedItemPosition
            val toPos = toSpinner.selectedItemPosition
            fromSpinner.setSelection(toPos)
            toSpinner.setSelection(fromPos)
            updateConversion()
        }
    }

    private fun updateConversion() {
        if (fromAmount.text.toString().isEmpty()) {
            toAmount.setText("")
            return
        }

        try {
            val amount = fromAmount.text.toString().toDouble()
            val fromCurrency = getCurrencyCode(fromSpinner.selectedItem.toString())
            val toCurrency = getCurrencyCode(toSpinner.selectedItem.toString())

            val convertedAmount = convertCurrency(amount, fromCurrency, toCurrency)
            
            isUpdatingToAmount = true
            toAmount.setText(String.format("%.2f", convertedAmount))
            isUpdatingToAmount = false
        } catch (e: NumberFormatException) {
            toAmount.setText("")
        }
    }

    private fun getCurrencyCode(spinnerText: String): String {
        return spinnerText.substring(0, 3)
    }

    private fun convertCurrency(amount: Double, from: String, to: String): Double {
        val fromRate = exchangeRates[from] ?: 1.0
        val toRate = exchangeRates[to] ?: 1.0
        return amount * (toRate / fromRate)
    }
}