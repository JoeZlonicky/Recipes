package io.github.cybervoid.app

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        createRecipeButtons()
        setFABListener()
        addSearchBarListeners()
    }

    private fun createRecipeButtons() {
        val container: LinearLayout = findViewById(R.id.recipeContainer)
        for (recipe in RecipeDatabase.recipes) {
            val button = Button(this)
            button.text = recipe.name
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30.0f)
            val layout = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
            layout.topMargin = toPixels(10)
            button.layoutParams = layout
            button.isAllCaps = false
            button.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorText))
            button.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.colorPrimaryDark))
            button.setOnClickListener {
                goToRecipe(recipe)
            }
            container.addView(button)
        }
    }

    private fun setFABListener() {
        val newRecipeButton = findViewById<FloatingActionButton>(R.id.newRecipe)
        newRecipeButton.setOnClickListener {
            newRecipe()
        }
    }

    private fun addSearchBarListeners() {
        findViewById<EditText>(R.id.searchBar).addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateVisibleRecipes()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        findViewById<Switch>(R.id.ingredientsSearchToggle).setOnClickListener {
            updateVisibleRecipes()
        }
    }

    private fun updateVisibleRecipes() {
        val s = findViewById<EditText>(R.id.searchBar).text
        val container = findViewById<LinearLayout>(R.id.recipeContainer)
        val includeIngredients = findViewById<Switch>(R.id.ingredientsSearchToggle).isChecked
        for (i in 0..(container.childCount - 1)) {
            container.getChildAt(i).visibility = View.GONE
            if (RecipeDatabase.recipes[i].name.contains(s.toString(), true)) {
                container.getChildAt(i).visibility = View.VISIBLE
            }
            if (includeIngredients) {
                for (ingredient in RecipeDatabase.recipes[i].ingredients) {
                    if (ingredient.contains(s.toString(), true)) {
                        container.getChildAt(i).visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun goToRecipe(recipe: Recipe) {
        val intent = Intent(this, RecipeActivity::class.java).apply {
            putExtra("Recipe", recipe)
            putExtra("IsNewRecipe", false)
        }
        startActivity(intent)
        finish()
    }

    private fun newRecipe() {
        val intent = Intent(this, RecipeEditActivity::class.java).apply {
            putExtra("Recipe", Recipe("", RecipeDatabase.getNewID()))
            putExtra("IsNewRecipe", true)
        }
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
                    return true
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun toPixels(dp: Int) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(), resources.displayMetrics).toInt()
}
