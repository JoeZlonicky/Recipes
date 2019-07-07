package io.github.cybervoid.app

import android.app.ActionBar
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import android.view.MotionEvent
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView

class RecipeActivity : AppCompatActivity() {

    private lateinit var recipe: Recipe

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()
        setContentView(R.layout.activity_recipe)

        recipe = intent.getSerializableExtra("Recipe") as Recipe
        val nameView: TextView = findViewById(R.id.recipeName)
        val ingredientsContainer = findViewById<LinearLayout>(R.id.ingredientContainer)
        val instructionContainer = findViewById<LinearLayout>(R.id.instructionContainer)

        nameView.text = recipe.name
        for (ingredient in recipe.ingredients) {
            ingredientsContainer.addView(createListItem(ingredient, false))
        }
        for (instruction in recipe.instructions) {
            instructionContainer.addView(createListItem(instruction, true))
        }

        findViewById<ImageButton>(R.id.edit).setOnClickListener {
            val intent = Intent(this, RecipeEditActivity::class.java).apply {
                putExtra("Recipe", recipe)
                putExtra("IsNewRecipe", false)
            }
            startActivity(intent)
            finish()
        }
    }

    private fun createListItem(text: String, extraSpacing: Boolean): TextView {
        val textView = TextView(this)
        textView.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorText))
        textView.text = text
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20.0f)
        if (extraSpacing) {
            val layout = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
            layout.bottomMargin = 20
            textView.layoutParams = layout
        }
        return textView
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (view is EditText) {
                val rect = Rect()
                view.getGlobalVisibleRect(rect)
                if (!rect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    view.clearFocus()
                    val inputManger = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputManger.hideSoftInputFromWindow(view.windowToken, 0)

                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}
