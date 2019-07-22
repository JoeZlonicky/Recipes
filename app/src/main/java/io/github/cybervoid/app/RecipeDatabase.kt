package io.github.cybervoid.app

import java.io.*

object RecipeDatabase : Serializable {
    var recipes = mutableListOf<Recipe>()
    lateinit var internalDir: File

    // Saves recipes to internal storage
    fun save() {
        FileOutputStream(File(internalDir, "database.ser")).use { fileStream ->
            ByteArrayOutputStream().use { byteStream ->
                ObjectOutputStream(byteStream).use {objStream ->
                    objStream.writeObject(recipes)
                    objStream.flush()
                    fileStream.write(byteStream.toByteArray())
                }
            }
        }
    }

    // Retrieves any saved recipes from internal storage
    fun load() {
        val file = File(internalDir, "database.ser")
        if (!file.exists()) {
            return
        }
        val bytes = file.readBytes()
        ByteArrayInputStream(bytes).use { byteStream ->
            ObjectInputStream(byteStream).use { objStream ->
                @Suppress("UNCHECKED_CAST")
                recipes = objStream.readObject() as MutableList<Recipe>
            }
        }
    }

    // Finds the next available id for a new recipe
    fun createNewID(): Int {
        var newID = 0
        while (recipes.any { it.id == newID }) {
            ++newID
        }
        return newID
    }

    // Adds a recipe to the recipe list
    fun addRecipe(recipe: Recipe) {
        recipes.add(recipe)
        sort()
    }

    // Updates an existing recipe
    fun updateRecipe(recipeToUpdate: Recipe) {
        recipes.find {recipe -> recipe.id == recipeToUpdate.id}?.
                update(recipeToUpdate)
        sort()
    }

    // Removes a recipe from the recipe list
    fun deleteRecipe(recipe: Recipe) {
        recipes.removeAll {it.id == recipe.id}
    }

    // Sorts recipes by name
    private fun sort() {
        recipes.sortBy { it.name }
    }
}