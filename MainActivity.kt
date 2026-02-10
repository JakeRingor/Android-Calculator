package com.example.calcuboi

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var expression: EditText
    private lateinit var result: EditText

    private var currentExpression = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        expression = findViewById(R.id.expression)
        result = findViewById(R.id.result)

        // Numbers
        setNumber(R.id.btn_zeroo, "0")
        setNumber(R.id.btn_zero, "0")
        setNumber(R.id.btn_one, "1")
        setNumber(R.id.btn_two, "2")
        setNumber(R.id.btn_three, "3")
        setNumber(R.id.btn_four, "4")
        setNumber(R.id.btn_five, "5")
        setNumber(R.id.btn_six, "6")
        setNumber(R.id.btn_seven, "7")
        setNumber(R.id.btn_eight, "8")
        setNumber(R.id.btn_nine, "9")
        setNumber(R.id.btn_dot, ".")

        // Operators
        setOperator(R.id.btn_add, "+")
        setOperator(R.id.btn_subt, "-")
        setOperator(R.id.btn_multiply, "*")
        setOperator(R.id.btn_divide, "/")
        setOperator(R.id.btn_modulo, "%")

        // Clear
        findViewById<Button>(R.id.btn_c).setOnClickListener {
            currentExpression = ""
            expression.setText("0")
            result.setText("0")
        }

        // Delete
        findViewById<Button>(R.id.btn_remove).setOnClickListener {
            if (currentExpression.isNotEmpty()) {
                currentExpression = currentExpression.dropLast(1)
                expression.setText(
                    if (currentExpression.isEmpty()) "0" else currentExpression
                )
                calculateLive()
            }
        }

        // Equal
        findViewById<Button>(R.id.btn_equal).setOnClickListener {
            calculateFinal()
        }
    }

    private fun setNumber(id: Int, value: String) {
        findViewById<Button>(id).setOnClickListener {
            currentExpression += value
            expression.setText(currentExpression)
            calculateLive()
        }
    }

    private fun setOperator(id: Int, op: String) {
        findViewById<Button>(id).setOnClickListener {
            if (currentExpression.isNotEmpty() &&
                !currentExpression.last().toString().matches(Regex("[+\\-*/%]"))
            ) {
                currentExpression += op
                expression.setText(currentExpression)
            }
        }
    }

    private fun calculateLive() {
        try {
            if (currentExpression.isEmpty()) return

            // Don't calculate if last character is operator
            if (currentExpression.last().toString()
                    .matches(Regex("[+\\-*/%]"))
            ) {
                return
            }

            val exp = currentExpression.replace("%", "/100*")
            val resultValue = evaluate(exp)

            result.setText(formatResult(resultValue))

        } catch (e: Exception) {
            result.setText("")
        }
    }


    // Matatanggal yung display pag press ng "="
    private fun calculateFinal() {
        try {
            if (currentExpression.isEmpty()) return

            val exp = currentExpression.replace("%", "/100*")
            val resultValue = evaluate(exp)
            val formatted = formatResult(resultValue)

            // Put final answer in expression
            expression.setText(formatted)

            // Clear result display after equals
            result.setText("")

            // Reset expression
            currentExpression = formatted

        } catch (e: Exception) {
            result.setText("Error")
        }
    }


    private fun evaluate(exp: String): Double {
        val tokens = Regex("(?<=[-+*/])|(?=[-+*/])")
            .split(exp)
            .filter { it.isNotBlank() }
            .toMutableList()

        var i = 0
        while (i < tokens.size) {
            if (tokens[i] == "*" || tokens[i] == "/") {
                val left = tokens[i - 1].toDouble()
                val right = tokens[i + 1].toDouble()
                val res = if (tokens[i] == "*") left * right else left / right
                tokens[i - 1] = res.toString()
                tokens.removeAt(i)
                tokens.removeAt(i)
                i = 0
            } else i++
        }

        var result = tokens[0].toDouble()
        i = 1
        while (i < tokens.size) {
            val op = tokens[i]
            val next = tokens[i + 1].toDouble()
            if (op == "+") result += next
            else if (op == "-") result -= next
            i += 2
        }

        return result
    }

    private fun formatResult(value: Double): String {
        return if (value % 1 == 0.0)
            value.toLong().toString()
        else
            String.format("%.6f", value).trimEnd('0').trimEnd('.')
    }
}
