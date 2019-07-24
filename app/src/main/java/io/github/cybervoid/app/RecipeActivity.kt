package io.github.cybervoid.app

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView

class RecipeActivity : AppCompatActivity() {

    private lateinit var recipe: Recipe

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        recipe = intent.getSerializableExtra("Recipe") as Recipe
        findViewById<TextView>(R.id.recipeName).text = recipe.name

        // Add ingredients to layout
        val ingredientsContainer = findViewById<LinearLayout>(R.
                id.ingredientContainer)
        for (ingredient in recipe.ingredients) {
            val view = layoutInflater.inflate(R.layout.ingredient_text_view,
                    ingredientsContainer, false) as TextView
            view.text = ingredient
            ingredientsContainer.addView(view)
        }

        // Add instructions to layout
        val instructionContainer = findViewById<LinearLayout>(R.
                id.instructionContainer)
        for ((i, instruction) in recipe.instructions.withIndex()) {
            val view = layoutInflater.inflate(R.layout.instruction_text_view,
                    instructionContainer, false) as TextView
            val text = (i + 1).toString() + ". " + instruction
            view.text = text
            instructionContainer.addView(view)
        }

        // Add notes to layout
        val noteContainer = findViewById<LinearLayout>(R.id.noteContainer)
        for (note in recipe.notes) {
            val view = layoutInflater.inflate(R.layout.note_text_view,
                    noteContainer, false) as TextView
            view.text = note
            noteContainer.addView(view)
        }

        // Go to RecipeEditActivity on pressing edit button
        findViewById<ImageButton>(R.id.edit).setOnClickListener {
            val intent = Intent(this,
                    RecipeEditActivity::class.java).apply {
                putExtra("Recipe", recipe)
                putExtra("IsNewRecipe", false)
            }
            startActivity(intent)
            finish()
        }
    }

    // Go back to MainActivity on pressing back
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
