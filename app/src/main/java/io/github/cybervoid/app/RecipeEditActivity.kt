package io.github.cybervoid.app

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
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

class RecipeEditActivity : AppCompatActivity() {

    private lateinit var recipe: Recipe
    private lateinit var oldRecipe: Recipe
    private var isNewRecipe: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()
        setContentView(R.layout.activity_recipe_edit)

        recipe = intent.getSerializableExtra("Recipe") as Recipe
        oldRecipe = recipe.copy()
        isNewRecipe = intent.getBooleanExtra("IsNewRecipe", false)

        val title = findViewById<EditText>(R.id.recipeName)
        val ingredientContainer = findViewById<LinearLayout>(R.id.ingredientContainer)
        val instructionContainer = findViewById<LinearLayout>(R.id.instructionContainer)

        title.setText(recipe.name)
        for (i in 0 until recipe.ingredients.size) {
            ingredientContainer.addView(createTextEdit(recipe.ingredients[i]))
        }
        for (i in 0 until recipe.instructions.size) {
            instructionContainer.addView(createTextEdit(recipe.instructions[i]))
        }
        findViewById<ImageButton>(R.id.newIngredient).setOnClickListener {
            ingredientContainer.addView(createTextEdit())
        }
        findViewById<ImageButton>(R.id.newInstruction).setOnClickListener {
            instructionContainer.addView(createTextEdit())
        }
        findViewById<ImageButton>(R.id.confirmChanges).setOnClickListener {
            recipe.name = title.text.toString()
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
            val intent = Intent(this, RecipeActivity::class.java).apply {
                putExtra("Recipe", recipe)
            }
            startActivity(intent)
            finish()
        }
        findViewById<ImageButton>(R.id.cancelChanges).setOnClickListener {
            if (isNewRecipe) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                onBackPressed()
            }
        }

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
            findViewById<Button>(R.id.deleteRecipeButton).visibility = View.GONE
        }
    }

    private fun createTextEdit(text:String = ""): View {
        val container = LinearLayout(this)
        container.orientation = LinearLayout.HORIZONTAL
        container.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)

        val newEdit = TextInputEditText(this)
        newEdit.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f)
        newEdit.setText(text)
        newEdit.setTextColor(resources.getColor(R.color.colorText))
        newEdit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20.0f)
        container.addView(newEdit)

        val delete = ImageButton(this)
        delete.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.FILL_PARENT)
        delete.setImageDrawable(resources.getDrawable(R.drawable.round_cancel_icon))
        delete.setBackgroundColor(resources.getColor(R.color.colorBoxes))
        container.addView(delete)
        delete.setOnClickListener {
            container.visibility = View.GONE
        }
        return container
    }

    override fun onBackPressed() {
        if (isNewRecipe) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this, RecipeActivity::class.java).apply {
                putExtra("Recipe", oldRecipe)
            }
            startActivity(intent)
            finish()
        }
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
