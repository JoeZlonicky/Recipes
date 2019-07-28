package io.github.cybervoid.app

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import kotlinx.android.synthetic.main.activity_recipe.*
import java.lang.Integer.max
import java.lang.Integer.min
import java.util.*


class RecipeEditActivity : AppCompatActivity() {

    private lateinit var recipe: Recipe
    private lateinit var oldRecipe: Recipe
    private var isNewRecipe: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_edit)

        if (savedInstanceState == null) {
            // Get extras and save a copy of the recipe
            recipe = intent.getSerializableExtra("Recipe") as Recipe
            isNewRecipe = intent.getBooleanExtra("IsNewRecipe", false)
            oldRecipe = recipe.copy()  // Save a copy for reverting changes
        } else {
            // Retrieve values from savedInstanceState
            Log.d("Test", "Using values from savedInstance")
            recipe = savedInstanceState.getSerializable("Recipe") as Recipe
            oldRecipe = savedInstanceState.getSerializable("OldRecipe") as Recipe
            isNewRecipe = savedInstanceState.getBoolean("IsNewRecipe")

        }

        findViewById<EditText>(R.id.recipeName).setText(recipe.name)

        // Make ingredients editable
        for (ingredient in recipe.ingredients) {
            createEditableItem(ingredient, "New ingredient",
                    findViewById(R.id.ingredientContainer))
        }

        // Make instructions editable
        for (instruction in recipe.instructions) {
            createEditableItem(instruction, "New instruction",
                    findViewById(R.id.instructionContainer))
        }

        // Make notes editable
        for (note in recipe.notes) {
            createEditableItem(note, "New note",
                    findViewById(R.id.noteContainer))
        }

        // Add new ingredient/instruction listeners
        findViewById<ImageButton>(R.id.newIngredient).setOnClickListener {
            createEditableItem("", "New ingredient",
                    findViewById(R.id.ingredientContainer))
        }
        findViewById<ImageButton>(R.id.newInstruction).setOnClickListener {
            createEditableItem("", "New instruction",
                    findViewById(R.id.instructionContainer))
        }
        findViewById<ImageButton>(R.id.newNote).setOnClickListener {
            createEditableItem("", "New note",
                    findViewById(R.id.noteContainer))
        }
        // Add listener for saving changes
        findViewById<ImageButton>(R.id.confirmChanges).setOnClickListener {
            updateRecipe()
            if (recipe.name.any { it != ' ' }) {
                saveChanges()
                val intent = Intent(this, RecipeActivity::class.java).apply {
                    putExtra("Recipe", recipe)
                }
                startActivity(intent)
                if (isNewRecipe) {
                    overridePendingTransition(R.anim.slide_right, R.anim.no_animation)
                } else {
                    overridePendingTransition(0, R.anim.slide_left)
                }
                finish()
            }
        }
        // Add listener for discarding changes
        findViewById<ImageButton>(R.id.cancelChanges).setOnClickListener {
            onBackPressed()
        }

        // Add functionality to delete button
        if (!isNewRecipe) {
            findViewById<Button>(R.id.deleteRecipeButton).setOnClickListener {
                val dialog = AlertDialog.Builder(this, R.style.AlertDialogStyle).create()
                dialog.setTitle("Delete recipe?")
                dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes") { _, _ ->
                    RecipeDatabase.deleteRecipe(recipe)
                    RecipeDatabase.save()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, R.anim.slide_left)
                    finish()
                }
                dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No") { _, _ ->
                }
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

    private fun createEditableItem(text: String, hint: String,
                                   container: LinearLayout) {
        val view = layoutInflater.inflate(R.layout.editable_list_item,
                container, false) as LinearLayout
        (view.getChildAt(1) as EditText).setText(text)
        (view.getChildAt(1) as EditText).hint = hint
        (view.getChildAt(2) as ImageButton).setOnClickListener {
            view.visibility = View.GONE
        }
        (view.getChildAt(0) as LinearLayout).getChildAt(0).setOnClickListener {
            var index = container.indexOfChild(view)
            index = maxOf(index - 1, 0)
            container.removeView(view)
            container.addView(view, index)
        }
        (view.getChildAt(0) as LinearLayout).getChildAt(1).setOnClickListener {
            var index = container.indexOfChild(view)
            index = minOf(index + 1, container.childCount - 1)
            container.removeView(view)
            container.addView(view, index)
        }
        container.addView(view)
    }

    // Update RecipeDatabase with the changes
    private fun saveChanges() {
        if (isNewRecipe) {
            RecipeDatabase.addRecipe(recipe)
        } else {
            RecipeDatabase.updateRecipe(recipe)
        }
        RecipeDatabase.save()
    }

    // Update the recipe based off the containers
    private fun updateRecipe() {
        recipe.name = findViewById<EditText>(R.id.recipeName).text.toString()
        val ingredientContainer = findViewById<LinearLayout>(R.id.ingredientContainer)
        val instructionContainer = findViewById<LinearLayout>(R.id.instructionContainer)
        val noteContainer = findViewById<LinearLayout>(R.id.noteContainer)
        recipe.ingredients.clear()
        for (i in 0 until ingredientContainer.childCount) {
            val innerContainer = ingredientContainer.getChildAt(i) as LinearLayout
            val ingredient = innerContainer.getChildAt(1) as EditText
            if (ingredient.text.toString().isNotEmpty() && innerContainer.visibility != View.GONE) {
                recipe.ingredients.add(ingredient.text.toString())
            }
        }
        recipe.instructions.clear()
        for (i in 0 until instructionContainer.childCount) {
            val innerContainer = instructionContainer.getChildAt(i) as LinearLayout
            val instruction = innerContainer.getChildAt(1) as EditText
            if (instruction.text.toString().isNotEmpty() && innerContainer.visibility != View.GONE) {
                recipe.instructions.add(instruction.text.toString())
            }
        }
        recipe.notes.clear()
        for (i in 0 until noteContainer.childCount) {
            val innerContainer = noteContainer.getChildAt(i) as LinearLayout
            val note = innerContainer.getChildAt(1) as EditText
            if (note.text.toString().isNotEmpty() && innerContainer.visibility != View.GONE) {
                recipe.notes.add(note.text.toString())
            }
        }
    }

    // Confirm discard or just go back if no changes have been made
    override fun onBackPressed() {
        intent = if (isNewRecipe) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, RecipeActivity::class.java).apply {
                putExtra("Recipe", oldRecipe)
            }
        }
        updateRecipe()
        if (recipe.equals(oldRecipe) && !isNewRecipe) {
            startActivity(intent)
            overridePendingTransition(0, R.anim.slide_left)
            finish()
        } else {
            val dialog = AlertDialog.Builder(this, R.style.AlertDialogStyle).create()
            dialog.setTitle("Discard changes?")
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes") { _, _ ->
                startActivity(intent)
                overridePendingTransition(R.anim.no_animation, R.anim.slide_left)
                finish()
            }
            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No") { _, _ ->
            }
            dialog.show()
            val posButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val negButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            val layout = posButton.layoutParams as LinearLayout.LayoutParams
            layout.weight = 10.0f
            posButton.layoutParams = layout
            negButton.layoutParams = layout

        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        updateRecipe()
        outState?.putSerializable("Recipe", recipe)
        outState?.putSerializable("OldRecipe", oldRecipe)
        outState?.putBoolean("IsNewRecipe", isNewRecipe)
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
                    val inputManger = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputManger.hideSoftInputFromWindow(view.windowToken, 0)
                    return true
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}
