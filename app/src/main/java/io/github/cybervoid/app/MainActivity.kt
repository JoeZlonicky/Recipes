package io.github.cybervoid.app

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.TypedValue
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import java.io.*

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        val recipes = getRecipes()
        val container: LinearLayout = findViewById(R.id.buttonContainer)
        saveRecipes(recipes)
        for (recipe in recipes) {
            val button = Button(this)
            button.text = recipe.name
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30.0f)
            val layout = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                    toPixels(75))
            layout.topMargin = 25
            button.layoutParams = layout
            button.isAllCaps = false
            button.setTextColor(resources.getColor(R.color.colorText))
            button.setBackgroundColor(resources.getColor(R.color.colorBoxes))
            button.setOnClickListener {
                goToRecipe(recipe)
            }
            container.addView(button)
        }
    }

    private fun toPixels(dp: Int) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(), resources.displayMetrics).toInt()

    private fun getRecipes(): MutableList<Recipe> {
        val recipes = mutableListOf<Recipe>()
        val path = filesDir.toString()
        val files = File(path).listFiles()
        for (file in files) {
            Log.d("Files", "Found file: ${file.name}")
            val bytes = file.readBytes()
            val byteStream = ByteArrayInputStream(bytes)
            val objStream = ObjectInputStream(byteStream)
            val recipe = objStream.readObject()
            recipes.add(recipe as Recipe)
            objStream.close()
            byteStream.close()
        }
        return recipes
    }

    private fun saveRecipes(recipes: MutableList<Recipe>) {
        for (recipe in recipes) {
            Log.d("Files", "Saving recipe: ${recipe.name}")
            val fileStream = FileOutputStream(File(filesDir, recipe.name + ".ser"))
            val byteStream = ByteArrayOutputStream()
            val objStream = ObjectOutputStream(byteStream)
            objStream.writeObject(recipe)
            objStream.flush()
            fileStream.write(byteStream.toByteArray())
            objStream.close()
            byteStream.close()
            fileStream.close()
        }
    }

    private fun goToRecipe(recipe: Recipe) {
        val intent = Intent(this, RecipeActivity::class.java).apply {
            putExtra("RECIPE", recipe)
        }
        startActivity(intent)
    }
}
