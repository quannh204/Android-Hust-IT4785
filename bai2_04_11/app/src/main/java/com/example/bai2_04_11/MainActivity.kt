package com.example.bai2_04_11

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener

class MainActivity : AppCompatActivity() {

    private lateinit var edtNumber: EditText
    private lateinit var rbOdd: RadioButton
    private lateinit var rbEven: RadioButton
    private lateinit var rbPrime: RadioButton
    private lateinit var rbPerfect: RadioButton
    private lateinit var rbSquare: RadioButton
    private lateinit var rbFibo: RadioButton
    private lateinit var lvNumbers: ListView
    private lateinit var tvEmpty: TextView

    private val numbers = mutableListOf<Int>()
    private lateinit var adapter: ArrayAdapter<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bindViews()
        setupListView()
        setupEvents()
    }

    private fun bindViews() {
        edtNumber = findViewById(R.id.edtNumber)
        rbOdd = findViewById(R.id.rbOdd)
        rbEven = findViewById(R.id.rbEven)
        rbPrime = findViewById(R.id.rbPrime)
        rbPerfect = findViewById(R.id.rbPerfect)
        rbSquare = findViewById(R.id.rbSquare)
        rbFibo = findViewById(R.id.rbFibo)
        lvNumbers = findViewById(R.id.lvNumbers)
        tvEmpty = findViewById(R.id.tvEmpty)
    }

    private fun setupListView() {
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, numbers)
        lvNumbers.adapter = adapter
    }

    private fun setupEvents() {
        // Khi nhập số -> cập nhật danh sách
        edtNumber.addTextChangedListener {
            updateList()
        }

        // 6 RadioButton: đảm bảo chỉ chọn duy nhất 1
        val allRadios = listOf(rbOdd, rbEven, rbPrime, rbPerfect, rbSquare, rbFibo)

        fun selectRadio(clicked: RadioButton) {
            allRadios.forEach { it.isChecked = (it == clicked) }
            updateList()
        }

        allRadios.forEach { rb ->
            rb.setOnClickListener {
                selectRadio(rb)
            }
        }
    }

    private fun updateList() {
        numbers.clear()

        val text = edtNumber.text.toString()
        val n = text.toIntOrNull()

        // Không hợp lệ: không hiển thị số nào
        if (n == null || n <= 0) {
            showEmpty()
            return
        }

        // Xác định loại số được chọn
        val type: NumberType? = when {
            rbOdd.isChecked -> NumberType.ODD
            rbEven.isChecked -> NumberType.EVEN
            rbPrime.isChecked -> NumberType.PRIME
            rbPerfect.isChecked -> NumberType.PERFECT
            rbSquare.isChecked -> NumberType.SQUARE
            rbFibo.isChecked -> NumberType.FIBONACCI
            else -> null
        }

        if (type == null) {
            showEmpty()
            return
        }

        for (i in 1 until n) {
            if (isMatch(i, type)) {
                numbers.add(i)
            }
        }

        adapter.notifyDataSetChanged()

        if (numbers.isEmpty()) {
            showEmpty()
        } else {
            lvNumbers.visibility = View.VISIBLE
            tvEmpty.visibility = View.GONE
        }
    }

    private fun showEmpty() {
        lvNumbers.visibility = View.GONE
        tvEmpty.visibility = View.VISIBLE
        numbers.clear()
        adapter.notifyDataSetChanged()
    }

    private fun isMatch(x: Int, type: NumberType): Boolean {
        return when (type) {
            NumberType.ODD -> x % 2 != 0
            NumberType.EVEN -> x % 2 == 0
            NumberType.PRIME -> isPrime(x)
            NumberType.PERFECT -> isPerfect(x)
            NumberType.SQUARE -> isSquare(x)
            NumberType.FIBONACCI -> isFibo(x)
        }
    }

    private fun isPrime(x: Int): Boolean {
        if (x < 2) return false
        var i = 2
        while (i * i <= x) {
            if (x % i == 0) return false
            i++
        }
        return true
    }

    private fun isPerfect(x: Int): Boolean {
        if (x <= 1) return false
        var sum = 1
        var i = 2
        while (i * i <= x) {
            if (x % i == 0) {
                sum += i
                val other = x / i
                if (other != i) sum += other
            }
            i++
        }
        return sum == x
    }

    private fun isSquare(x: Int): Boolean {
        val r = kotlin.math.sqrt(x.toDouble()).toInt()
        return r * r == x
    }

    private fun isFibo(x: Int): Boolean {
        if (x < 0) return false
        // Một số là Fibonacci nếu 5n^2+4 hoặc 5n^2-4 là số chính phương
        val n2 = x.toLong() * x
        return isSquareLong(5L * n2 + 4L) || isSquareLong(5L * n2 - 4L)
    }

    private fun isSquareLong(v: Long): Boolean {
        if (v < 0) return false
        val r = kotlin.math.sqrt(v.toDouble()).toLong()
        return r * r == v
    }

    private enum class NumberType {
        ODD, EVEN, PRIME, PERFECT, SQUARE, FIBONACCI
    }
}