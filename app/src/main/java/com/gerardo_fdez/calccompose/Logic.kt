package com.gerardo_fdez.calccompose

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable

class Logic  : ViewModel(){
    private val _equationText = MutableLiveData("")
    val equationText : LiveData<String> = _equationText

    private val _resultText = MutableLiveData("0")
    val resultText : LiveData<String> = _resultText

    fun onButtonClick(btn : String){
        Log.i("Clicked Button", btn)

        _equationText.value?.let {
            if(btn=="AC"){
                _equationText.value = ""
                _resultText.value = "0"
                return
            }

            if(btn=="^"){
                _equationText.value = it + "^"
                return
            }

            if(btn == "="){
                _equationText.value = _resultText.value
                return
            }

            _equationText.value = it + btn

            //Calcular el Resultado
            try {
                _resultText.value = calculateResult(_equationText.value.toString())
            }
            catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    fun calculateResult(equation: String): String {
        val modifiedEquation = parseExponent(equation) // Reemplazar "^" por la expresión adecuada
        val context: Context = Context.enter()
        context.optimizationLevel = -1
        val scriptable: Scriptable = context.initStandardObjects()
        var finalResult = context.evaluateString(scriptable, modifiedEquation, "Javascript", 1, null).toString()
        if (finalResult.endsWith(".0")) {
            finalResult = finalResult.replace(".0", "")
        }
        return finalResult
    }

    private fun parseExponent(equation: String): String {
        val regex = Regex("([0-9.]+)\\^([0-9.]+)")
        return regex.replace(equation) {
            val base = it.groupValues[1]
            val exponent = it.groupValues[2]
            "Math.pow($base, $exponent)"
        }
    }
}
