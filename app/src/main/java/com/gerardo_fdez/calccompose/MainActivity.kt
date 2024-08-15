package com.gerardo_fdez.calccompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gerardo_fdez.calccompose.ui.theme.CalcComposeTheme
import java.util.Stack
import kotlin.math.pow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            }
        }

    /**
     * Método para determinar la precedencia de un operador.
     *
     * @param c El operador cuya precedencia se desea conocer.
     * @return El nivel de precedencia del operador, o -1 si el operador no es reconocido.
     */
    private fun precedence(c: Char): Int {
        return when (c) {
            '+', '-' -> 1
            '*', '/' -> 2
            '^' -> 3
            else -> -1
        }
    }

    /**
     * Convierte una expresión infix a postfix.
     *
     * @param expresion La expresión en formato infix.
     * @return La expresión convertida en formato postfix.
     */
    private fun infixToPostfix(expresion: String): String {
        val result = StringBuilder()
        val stack = Stack<Char>()

        try {
            val cleanedExpression = expresion.replace("\\s+".toRegex(), "")

            var i = 0
            while (i < cleanedExpression.length) {
                val c = cleanedExpression[i]

                if (c.isLetterOrDigit()) {
                    while (i < cleanedExpression.length && cleanedExpression[i].isLetterOrDigit()) {
                        result.append(cleanedExpression[i])
                        i++
                    }
                    result.append(' ')
                    i--
                } else if (c == '(') {
                    stack.push(c)
                } else if (c == ')') {
                    while (!stack.isEmpty() && stack.peek() != '(')
                        result.append(stack.pop()).append(' ')
                    if (!stack.isEmpty() && stack.peek() != '(') {
                        return "Expresión inválida"
                    } else {
                        stack.pop()
                    }
                } else {
                    while (!stack.isEmpty() && precedence(c) <= precedence(stack.peek()))
                        result.append(stack.pop()).append(' ')
                    stack.push(c)
                }
                i++
            }

            while (!stack.isEmpty()) {
                if (stack.peek() == '(')
                    return "Expresión inválida"
                result.append(stack.pop()).append(' ')
            }

            return result.toString().trim()
        } catch (e: Exception) {
            return "Expresión inválida"
        }
    }

    /**
     * Método para convertir de string a lista
     */
    private fun stringToList(expression: String): List<String> {
        return expression.split(" ").filter { it.isNotEmpty() }
    }

    /**
     * Método para evaluar la operación postfix
     */
    private fun evaluatePostfix(postfix: List<String>): Double {
        val stack = Stack<Double>()

        for (token in postfix) {
            when {
                token.toDoubleOrNull() != null -> stack.push(token.toDouble())
                else -> {
                    val b = stack.pop()
                    val a = stack.pop()
                    stack.push(when (token) {
                        "+" -> a + b
                        "-" -> a - b
                        "*" -> a * b
                        "/" -> a / b
                        "^" -> a.pow(b)
                        else -> throw IllegalArgumentException("Unknown operator: $token")
                    })
                }
            }
        }
        return stack.pop()
    }

    fun isValidInfixExpression(expression: String): Boolean {
        // Eliminamos espacios para simplificar la validación
        val cleanedExpression = expression.replace("\\s+".toRegex(), "")

        // Expresión regular para verificar números y operadores
        val singleOperatorRegex = Regex("^[+\\-*/^]$")
        val doubleSignRegex = Regex("([+\\-*/^]{2,}|\\d+[+\\-*/^]{2,}|[+\\-*/^]{2,}\\d+)")

        // Verificamos si la expresión contiene solo un símbolo matemático
        if (singleOperatorRegex.matches(cleanedExpression)) {
            return false // Solo un símbolo matemático no es una expresión válida
        }

        // Verificamos si la expresión contiene un número con dos signos consecutivos
        if (doubleSignRegex.containsMatchIn(cleanedExpression)) {
            return false // Contiene números con dos signos consecutivos
        }

        // Si no se encontró ninguna de las condiciones inválidas, la expresión es válida
        return true
    }

    /**
     * Método para evaluar la operación Infix
     */
    private fun evaluate(expresion: String): String{
        if(!isValidInfixExpression(expresion)) return "Error!"
        val modifiedContent = StringBuilder()
        val postfixExpression: String = infixToPostfix(expresion)
        if(postfixExpression.equals("Expresión inválida")) return "Error!"
        val result = evaluatePostfix(stringToList(postfixExpression))
        modifiedContent.append(postfixExpression).append("\n")
        return result.toString()
    }
}