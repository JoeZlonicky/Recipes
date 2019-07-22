package io.github.cybervoid.app

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create buttons from recipes
        val container: LinearLayout = findViewById(R.id.recipeContainer)
        for (recipe in RecipeDatabase.recipes) {
            val button = layoutInflater.inflate(R.layout.recipe_button,
                    container, false) as Button
            button.text = recipe.name
            button.setOnClickListener {
                goToRecipe(recipe)
            }
            container.addView(button)
        }

        // Add listener for new recipe
        findViewById<FloatingActionButton>(R.id.newRecipe).setOnClickListener {
            newRecipe()
        }

        // Add listeners for search bar and ingredients toggle
        findViewById<EditText>(R.id.searchBar).
                addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateVisibleRecipes()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int,
                                           count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int,
                                       before: Int, count: Int) {
            }
        })
        findViewById<Switch>(R.id.ingredientsSearchToggle).setOnClickListener {
            updateVisibleRecipes()
        }
    }

    // Only show recipes who names and possibly ingredients match search
    private fun updateVisibleRecipes() {
        val searchString = findViewById<EditText>(R.id.searchBar).text.toString()
        val recipeContainer = findViewById<LinearLayout>(R.id.recipeContainer)
        val includeIngredients = findViewById<Switch>(R.id.
                ingredientsSearchToggle).isChecked
        for (i in 0..(recipeContainer.childCount - 1)) {
            val include = RecipeDatabase.recipes[i].searchMatches(searchString,
                    includeIngredients)
            recipeContainer.getChildAt(i).visibility = if (include) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    // Go to RecipeActivity
    private fun goToRecipe(recipe: Recipe) {
        val intent = Intent(this, RecipeActivity::class.java).apply {
            putExtra("Recipe", recipe)
            putExtra("IsNewRecipe", false)
        }
        startActivity(intent)
        finish()
    }

    // Go to RecipeEditActivity with a new recipe
    private fun newRecipe() {
        val intent = Intent(this,
                RecipeEditActivity::class.java).apply {
            putExtra("Recipe", Recipe("", RecipeDatabase.createNewID()))
            putExtra("IsNewRecipe", true)
        }
        startActivity(intent)
        finish()
    }

    // Release focus on touch outside the view
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (view is EditText) {
                val rect = Rect()
                view.getGlobalVisibleRect(rect)
                if (!rect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    view.clearFocus()
                    val inputManger = getSystemService(Context.
                            INPUT_METHOD_SERVICE) as InputMethodManager
                    inputManger.hideSoftInputFromWindow(view.windowToken, 0)
                    return true
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}
