package com.example.calculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // Trạng thái phép toán
    private enum class Op { NONE, ADD, SUB, MUL, DIV }

    private var acc: Long? = null          // toán hạng tích lũy (trái)
    private var pending: Op = Op.NONE      // phép toán đang chờ
    private var isTypingNew = true          // true: người dùng đang nhập toán hạng mới
    private var current: Long = 0           // giá trị đang hiển thị (số nguyên)

    private lateinit var display: TextView
    private lateinit var displayExpr: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Nếu bạn không đổi tên layout, thay activity_main bằng đúng tên file XML bạn dùng
        setContentView(R.layout.activity_main)

        // Lấy TextView hiển thị
        display = findViewById(R.id.display)
        displayExpr = findViewById(R.id.display_expr)

        // Gán sự kiện cho dãy số
        val numberIds = listOf(
            R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4,
            R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9
        )
        numberIds.forEach { id ->
            findViewById<Button?>(id)?.setOnClickListener {
                onDigit((it as Button).text.toString())
            }
        }

        // Các nút chức năng
        findViewById<Button?>(R.id.btn_add)?.setOnClickListener { onOperator(Op.ADD) }
        findViewById<Button?>(R.id.btn_subtract)?.setOnClickListener { onOperator(Op.SUB) }
        findViewById<Button?>(R.id.btn_multiply)?.setOnClickListener { onOperator(Op.MUL) }
        findViewById<Button?>(R.id.btn_divide)?.setOnClickListener { onOperator(Op.DIV) }

        findViewById<Button?>(R.id.btn_equals)?.setOnClickListener { onEquals() }

        // CE: xóa toán hạng hiện tại về 0
        findViewById<Button?>(R.id.btn_ce)?.setOnClickListener { clearEntry() }
        // C: xóa phép toán, nhập lại từ đầu
        findViewById<Button?>(R.id.btn_c)?.setOnClickListener { clearAll() }
        // BS: xóa 1 chữ số hàng đơn vị của toán hạng hiện tại
        findViewById<Button?>(R.id.btn_bs)?.setOnClickListener { backspace() }

        // +/-: đổi dấu
        findViewById<Button?>(R.id.btn_sign)?.setOnClickListener { toggleSign() }

        // Nút "." có trong giao diện nhưng đề bài yêu cầu số nguyên.
        // Ta sẽ bỏ qua hoặc khóa nó:
        findViewById<Button?>(R.id.btn_decimal)?.setOnClickListener {
            // Không làm gì để đảm bảo chỉ làm việc với số nguyên
        }

        updateDisplay()
    }

    /** Nhập chữ số */
    private fun onDigit(d: String) {
        if (isTypingNew) {
            // Bắt đầu nhập toán hạng mới
            current = 0
            isTypingNew = false
        }
        // current = current * 10 + d
        val digit = d[0] - '0'
        current = when {
            current == 0L && digit == 0 -> 0
            else -> safeMulAdd(current, digit)
        }
        updateDisplay()
    }

    /** Chọn phép toán: nếu đã có acc & pending thì tính trước, sau đó gán pending mới */
    private fun onOperator(op: Op) {
        if (!isTypingNew) {
            computeIfNeeded()
        }
        pending = op
        acc = current
        isTypingNew = true
    }

    /** '=': thực hiện phép toán đang chờ */
    private fun onEquals() {
        computeIfNeeded()
        pending = Op.NONE
        acc = null
        isTypingNew = true
        updateDisplay()
    }

    /** CE: xóa giá trị toán hạng hiện tại về 0 */
    private fun clearEntry() {
        current = 0
        isTypingNew = true
        updateDisplay()
    }

    /** C: xóa phép toán, nhập lại từ đầu */
    private fun clearAll() {
        acc = null
        pending = Op.NONE
        current = 0
        isTypingNew = true
        updateDisplay()
    }

    /** BS: xóa chữ số hàng đơn vị của toán hạng hiện tại */
    private fun backspace() {
        if (!isTypingNew) {
            current = current / 10
            updateDisplay()
        }
    }

    /** +/-: đổi dấu toán hạng hiện tại */
    private fun toggleSign() {
        if (!isTypingNew || current != 0L) {
            current = -current
            updateDisplay()
        }
    }

    /** Thực hiện phép toán nếu có pending và acc */
    private fun computeIfNeeded() {
        val a = acc
        if (a != null) {
            val b = current
            current = when (pending) {
                Op.ADD -> safeAdd(a, b)
                Op.SUB -> safeSub(a, b)
                Op.MUL -> safeMul(a, b)
                Op.DIV -> {
                    if (b == 0L) {
                        // Chia cho 0 -> báo lỗi và reset
                        showError()
                        0
                    } else a / b
                }
                Op.NONE -> b
            }
            acc = null
        }
    }

    /** Cập nhật TextView hiển thị */
    private fun updateDisplay() {
        display.text = current.toString()
        updateExpression()
    }

    /** Hiển thị biểu thức đang nhập: "acc op current" */
    private fun updateExpression() {
        val opSymbol = when (pending) {
            Op.ADD -> "+"
            Op.SUB -> "-"
            Op.MUL -> "×"
            Op.DIV -> "÷"
            Op.NONE -> ""
        }

        val left = acc?.toString()
        val right = if (isTypingNew) "" else current.toString()

        val exprText = if (left == null || pending == Op.NONE) {
            // Chưa có phép toán đang chờ -> chỉ hiển thị số đang nhập
            current.toString()
        } else {
            listOfNotNull(left, opSymbol.takeIf { it.isNotEmpty() }, right.takeIf { it.isNotEmpty() })
                .joinToString(" ")
        }
        displayExpr.text = exprText
    }

    private fun showError() {
        display.text = getString(R.string.error_text)
        // Reset trạng thái để người dùng nhập lại
        acc = null
        pending = Op.NONE
        isTypingNew = true
    }

    /* ---- Các hàm "safe" để tránh tràn số Long (tuỳ chọn) ---- */
    private fun safeAdd(a: Long, b: Long): Long =
        try { Math.addExact(a, b) } catch (_: ArithmeticException) { showError(); 0 }

    private fun safeSub(a: Long, b: Long): Long =
        try { Math.subtractExact(a, b) } catch (_: ArithmeticException) { showError(); 0 }

    private fun safeMul(a: Long, b: Long): Long =
        try { Math.multiplyExact(a, b) } catch (_: ArithmeticException) { showError(); 0 }

    private fun safeMulAdd(base: Long, addDigit: Int): Long =
        try { Math.addExact(Math.multiplyExact(base, 10L), addDigit.toLong()) }
        catch (_: ArithmeticException) { showError(); 0 }
}