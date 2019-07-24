package io.github.cybervoid.app

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.media.Image
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_recipe.*


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

        findViewById<EditText>(R.id.recipeName).setText(recipe.name)

        // Make ingredients editable
        for (ingredient in recipe.ingredients) {
            addIngredientToLayout(ingredient)
        }

        // Make instructions editable
        for (instruction in recipe.instructions) {
            addInstructionToLayout(instruction)
        }

        // Make notes editable
        for (note in recipe.notes) {
            addNoteToLayout(note)
        }

        // Add new ingredient/instruction listeners
        findViewById<ImageButton>(R.id.newIngredient).setOnClickListener {
            addIngredientToLayout("")
        }
        findViewById<ImageButton>(R.id.newInstruction).setOnClickListener {
            addInstructionToLayout("")
        }
        findViewById<ImageButton>(R.id.newNote).setOnClickListener {
            addNoteToLayout("")
        }
        // Add listener for saving changes
        findViewById<ImageButton>(R.id.confirmChanges).setOnClickListener {
            val recipeName = findViewById<EditText>(R.id.recipeName).text.toString()
            if (recipeName.any { it != ' ' }) {
                saveChanges()
                val intent = Intent(this, RecipeActivity::class.java).apply {
                    putExtra("Recipe", recipe)
                }
                startActivity(intent)
                finish()
            }
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

    private fun addIngredientToLayout(text: String) {
        val ingredientContainer = findViewById<LinearLayout>(R.id.ingredientContainer)
        val view = layoutInflater.inflate(R.layout.editable_list_item,
                ingredientContainer, false) as LinearLayout
        view.findViewById<EditText>(R.id.editText).setText(text)
        view.findViewById<EditText>(R.id.editText).hint = "New ingredient"
        view.findViewById<ImageButton>(R.id.imageButton).setOnClickListener {
            view.visibility = View.GONE
        }
        ingredientContainer.addView(view)
    }

    private fun addInstructionToLayout(text: String) {
        val instructionContainer = findViewById<LinearLayout>(R.id.instructionContainer)
        val view = layoutInflater.inflate(R.layout.editable_list_item,
                instructionContainer, false) as LinearLayout
        view.findViewById<EditText>(R.id.editText).setText(text)
        view.findViewById<EditText>(R.id.editText).hint = "New instruction"
        view.findViewById<ImageButton>(R.id.imageButton).setOnClickListener {
            view.visibility = View.GONE
        }
        instructionContainer.addView(view)
    }

    private fun addNoteToLayout(text: String) {
        val noteContainer = findViewById<LinearLayout>(R.id.noteContainer)
        val view = layoutInflater.inflate(R.layout.editable_list_item,
                ingredientContainer, false) as LinearLayout
        view.findViewById<EditText>(R.id.editText).setText(text)
        view.findViewById<EditText>(R.id.editText).hint = "New note"
        view.findViewById<ImageButton>(R.id.imageButton).setOnClickListener {
            view.visibility = View.GONE
        }
        noteContainer.addView(view)
    }

    private fun saveChanges() {
        recipe.name = findViewById<EditText>(R.id.recipeName).text.toString()
        val ingredientContainer = findViewById<LinearLayout>(R.id.ingredientContainer)
        val instructionContainer = findViewById<LinearLayout>(R.id.instructionContainer)
        val noteContainer = findViewById<LinearLayout>(R.id.noteContainer)
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
        recipe.notes.clear()
        for (i in 0 until noteContainer.childCount) {
            val innerContainer = noteContainer.getChildAt(i) as LinearLayout
            val note = innerContainer.getChildAt(0) as EditText
            if (note.text.toString().isNotEmpty() && innerContainer.visibility != View.GONE) {
                recipe.notes.add(note.text.toString())
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
