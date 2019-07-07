package io.github.cybervoid.app

import android.util.Log
import java.io.*

object RecipeDatabase : Serializable {
    var recipes = mutableListOf<Recipe>()
    lateinit var internalDir: File

    fun save() {
        val fileStream = FileOutputStream(File(internalDir, "database.ser"))
        val byteStream = ByteArrayOutputStream()
        val objStream = ObjectOutputStream(byteStream)
        objStream.writeObject(recipes)
        objStream.flush()
        fileStream.write(byteStream.toByteArray())
        objStream.close()
        byteStream.close()
        fileStream.close()
    }

    fun load() {
        try {
            val file = File(internalDir, "database.ser")
            val bytes = file.readBytes()
            val byteStream = ByteArrayInputStream(bytes)
            val objStream = ObjectInputStream(byteStream)
            @Suppress("UNCHECKED_CAST")
            val database = objStream.readObject() as MutableList<Recipe>
            objStream.close()
            byteStream.close()
            recipes = database
        } catch (e: FileNotFoundException) {
            Log.d("Files", "Failed to find existing recipe database")
        }
    }

    fun getNewID(): Int {
        var newID = 0
        while (recipes.any { it.id == newID }) {
            ++newID
        }
        Log.d("Test", "NewID: $newID")
        return newID
    }

    fun addRecipe(recipe: Recipe) {
        recipes.add(recipe)
        sort()

    }

    fun updateRecipe(recipeToUpdate: Recipe) {
        for (recipe in recipes) {
            if (recipe.id == recipeToUpdate.id) {
                recipes[recipes.indexOf(recipe)] = recipeToUpdate
            }
        }
        sort()
    }

    fun deleteRecipe(recipe: Recipe) {
        recipes.removeAll {it.id == recipe.id}
    }

    private fun sort() {
        recipes.sortBy { it.name }
    }
}