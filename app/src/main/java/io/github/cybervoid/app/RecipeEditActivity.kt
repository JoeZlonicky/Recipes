package io.github.cybervoid.app

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout

//TODO: Cleanup code
//TODO: Move views to xml's

class RecipeEditActivity : AppCompatActivity() {

    private lateinit var recipe: Recipe
    private lateinit var oldRecipe: Recipe
    private var isNewRecipe: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_edit)

        // Get extras and save a copy of the recipe
        recipe = intent.getSerializableExtra("Recipe") as Recipe
        isNewRecipe = intent.getBooleanExtra("IsNewRecipe", false)
        oldRecipe = recipe.copy()  // Save a copy for reverting changes

        val title = findViewById<EditText>(R.id.recipeName)
        val ingredientContainer = findViewById<LinearLayout>(R.id.ingredientContainer)
        val instructionContainer = findViewById<LinearLayout>(R.id.instructionContainer)

        title.setText(recipe.name)

        // Make ingredients editable
        for (ingredient in recipe.ingredients) {
            val view = layoutInflater.inflate(R.layout.editable_list_item,
                    ingredientContainer, false) as LinearLayout
            (view.getChildAt(0) as EditText).setText(ingredient)
            ingredientContainer.addView(view)
        }

        // Make instructions editable
        for (instruction in recipe.instructions) {
            val view = layoutInflater.inflate(R.layout.editable_list_item,
                    instructionContainer, false) as LinearLayout
            (view.getChildAt(0) as EditText).setText(instruction)
            instructionContainer.addView(view)
        }

        // Add new ingredient/instruction listeners
        findViewById<ImageButton>(R.id.newIngredient).setOnClickListener {
            layoutInflater.inflate(R.layout.editable_list_item,
                    ingredientContainer, true) as LinearLayout
        }
        findViewById<ImageButton>(R.id.newInstruction).setOnClickListener {
            layoutInflater.inflate(R.layout.editable_list_item,
                    instructionContainer, true) as LinearLayout
        }
        // Add listener for saving changes
        findViewById<ImageButton>(R.id.confirmChanges).setOnClickListener {
            saveChanges()
            val intent = Intent(this, RecipeActivity::class.java).apply {
                putExtra("Recipe", recipe)
            }
            startActivity(intent)
            finish()
        }
        // Add listener for discarding changes
        findViewById<ImageButton>(R.id.cancelChanges).setOnClickListener {
            if (isNewRecipe) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                onBackPressed()
            }
        }

        // Add functionality to delete button
        if (!isNewRecipe) {
            findViewById<Button>(R.id.deleteRecipeButton).setOnClickListener {
                val dialog = AlertDialog.Builder(this, R.style.AlertDialogStyle).create()
                dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes") { _, _ ->
                    RecipeDatabase.deleteRecipe(recipe)
                    RecipeDatabase.save()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No") { _, _ ->
                }
                dialog.setTitle("Delete recipe?")
                dialog.show()
                val posButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                val negButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                val layout = posButton.layoutParams as LinearLayout.LayoutParams
                layout.weight = 10.0f
                posButton.layoutParams = layout
                negButton.layoutParams = layout
            }
        } else {
            // Hide delete button on new recipes
            findViewById<Button>(R.id.deleteRecipeButton).visibility = View.GONE
        }
    }

    fun saveChanges() {
        recipe.name = findViewById<EditText>(R.id.recipeName).text.toString()
        val ingredientContainer = findViewById<LinearLayout>(R.id.ingredientContainer)
        val instructionContainer = findViewById<LinearLayout>(R.id.instructionContainer)
        recipe.ingredients.clear()
        for (i in 0 until ingredientContainer.childCount) {
            val innerContainer = ingredientContainer.getChildAt(i) as LinearLayout
            val ingredient = innerContainer.getChildAt(0) as EditText
            if (ingredient.text.toString().isNotEmpty() && innerContainer.visibility != View.GONE) {
                recipe.ingredients.add(ingredient.text.toString())
            }
        }
        recipe.instructions.clear()
        for (i in 0 until instructionContainer.childCount) {
            val innerContainer = instructionContainer.getChildAt(i) as LinearLayout
            val instruction = innerContainer.getChildAt(0) as EditText
            if (instruction.text.toString().isNotEmpty() && innerContainer.visibility != View.GONE) {
                recipe.instructions.add(instruction.text.toString())
            }
        }
        if (isNewRecipe) {
            RecipeDatabase.addRecipe(recipe)
        } else {
            RecipeDatabase.updateRecipe(recipe)
        }
        RecipeDatabase.save()
    }

    // Go to correct activity on pressing back
    override fun onBackPressed() {
        intent = if (isNewRecipe) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, RecipeActivity::class.java).apply {
                putExtra("Recipe", oldRecipe)
            }
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
