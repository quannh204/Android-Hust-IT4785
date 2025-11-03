package com.example.register

import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.text.InputType
import android.widget.ScrollView
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.register.R
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var editFirst: EditText
    private lateinit var editLast: EditText
    private lateinit var radioGender: RadioGroup
    private lateinit var radioMale: RadioButton
    private lateinit var radioFemale: RadioButton
    private lateinit var editBirthday: EditText
    private lateinit var btnSelectBirthday: Button
    private lateinit var calendarView: CalendarView
    private lateinit var scrollView: ScrollView
    private lateinit var editAddress: EditText
    private lateinit var editEmail: EditText
    private lateinit var cbTerms: CheckBox
    private lateinit var btnRegister: Button
    private lateinit var labelGender: TextView

    // Lưu lại background mặc định để reset khi bỏ lỗi
    private val normalBg by lazy {
        ContextCompat.getDrawable(this, R.drawable.edit_text_background)
    }
    private val errorBgColor = Color.parseColor("#FFCDD2") // đỏ nhạt cho ô lỗi
    private var isCalendarVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
        setupCalendar()
        setupRegister()
    }

    private fun bindViews() {
        editFirst = findViewById(R.id.edit_first_name)
        editLast = findViewById(R.id.edit_last_name)
        radioGender = findViewById(R.id.radio_group_gender)
        radioMale = findViewById(R.id.radio_male)
        radioFemale = findViewById(R.id.radio_female)
        labelGender = findViewById(R.id.label_gender)

        editBirthday = findViewById(R.id.edit_birthday)
        btnSelectBirthday = findViewById(R.id.btn_select_birthday)
        calendarView = findViewById(R.id.calendarView)
        scrollView = findViewById(R.id.scroll_view)

        // Allow typing in birthday if user wants, but also let tapping toggle the calendar
        editBirthday.isFocusable = true
        editBirthday.isFocusableInTouchMode = true
        editBirthday.inputType = InputType.TYPE_CLASS_DATETIME
        editBirthday.setOnClickListener {
            // call the shared toggle so EditText tap behaves same as Select button
            toggleCalendar()
        }

        // Ensure the Select button is enabled and toggles calendar
        btnSelectBirthday.isEnabled = true
        btnSelectBirthday.isClickable = true
        btnSelectBirthday.setOnClickListener {
            toggleCalendar()
        }

        editAddress = findViewById(R.id.edit_address)
        editEmail = findViewById(R.id.edit_email)
        cbTerms = findViewById(R.id.checkbox_terms)
        btnRegister = findViewById(R.id.btn_register)
    }

    private fun setupCalendar() {
        // Calendar listener for date selection (kept here for separation of concerns)

        // Chọn ngày -> cập nhật EditText và ẩn Calendar
        calendarView.setOnDateChangeListener { _, year, month, day ->
            val cal = Calendar.getInstance()
            cal.set(year, month, day)
            val fmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            editBirthday.setText(fmt.format(cal.time))
            calendarView.visibility = View.GONE
            isCalendarVisible = false
        }
        // (toggleCalendar is a class-level helper; used by both button and EditText)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken ?: editBirthday.windowToken, 0)
    }

    private fun toggleCalendar() {
        if (isCalendarVisible) {
            calendarView.visibility = View.GONE
        } else {
            hideKeyboard()
            calendarView.visibility = View.VISIBLE
            // scroll so the calendar is visible inside the ScrollView
            scrollView.post {
                scrollView.smoothScrollTo(0, calendarView.top)
            }
        }
        isCalendarVisible = !isCalendarVisible
    }

    private fun setupRegister() {
        btnRegister.setOnClickListener {
            if (validateForm()) {
                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Vui lòng điền đủ thông tin bắt buộc.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateForm(): Boolean {
        var ok = true

        // Reset màu lỗi trước
        resetField(editFirst)
        resetField(editLast)
        resetField(editBirthday)
        resetField(editAddress)
        resetField(editEmail)
        labelGender.setTextColor(Color.BLACK)

        // First name
        if (editFirst.text.toString().trim().isEmpty()) {
            markError(editFirst); ok = false
        }

        // Last name
        if (editLast.text.toString().trim().isEmpty()) {
            markError(editLast); ok = false
        }

        // Gender
        if (radioGender.checkedRadioButtonId == -1) {
            labelGender.setTextColor(Color.RED); ok = false
        }

        // Birthday
        if (editBirthday.text.toString().trim().isEmpty()) {
            markError(editBirthday); ok = false
        }

        // Address
        if (editAddress.text.toString().trim().isEmpty()) {
            markError(editAddress); ok = false
        }

        // Email (không chỉ rỗng mà còn phải đúng định dạng)
        val email = editEmail.text.toString().trim()
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            markError(editEmail); ok = false
        }

        // Terms of Use
        if (!cbTerms.isChecked) {
            // bạn có thể đổi màu text để nhấn mạnh
            cbTerms.setTextColor(Color.RED)
            ok = false
        } else {
            cbTerms.setTextColor(Color.BLACK)
        }

        return ok
    }

    private fun markError(view: EditText) {
        // Tô nền đỏ nhạt cho trường bị thiếu
        view.setBackgroundColor(errorBgColor)
    }

    private fun resetField(view: EditText) {
        view.background = normalBg
    }
}
