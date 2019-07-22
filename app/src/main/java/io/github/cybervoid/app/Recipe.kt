package io.github.cybervoid.app

import java.io.Serializable

class Recipe(var name: String, val id: Int) : Serializable {
    var ingredients = mutableListOf<String>()
    var instructions = mutableListOf<String>()

    fun copy(): Recipe {
        val newRecipe = Recipe(name, id)
        newRecipe.ingredients = ingredients.toMutableList()
        newRecipe.instructions = instructions.toMutableList()
        return newRecipe
    }

    fun update(recipe: Recipe) {
        name = recipe.name
        ingredients = recipe.ingredients
        instructions = recipe.instructions
    }

    fun searchMatches(search: String, includeIngredients: Boolean): Boolean {
        if (name.contains(search, true)) {
            return true
        }
        if (includeIngredients) {
            return ingredients.any { it.contains(search, true) }
        }
        return false
    }
}