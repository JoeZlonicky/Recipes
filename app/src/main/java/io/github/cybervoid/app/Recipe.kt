package io.github.cybervoid.app

import java.io.Serializable

class Recipe(var name: String, val id: Int) : Serializable {
    var ingredients = mutableListOf<String>()
    var instructions = mutableListOf<String>()
    var notes = mutableListOf<String>()

    fun copy(): Recipe {
        val newRecipe = Recipe(name, id)
        newRecipe.ingredients = ingredients.toMutableList()
        newRecipe.instructions = instructions.toMutableList()
        newRecipe.notes = notes.toMutableList()
        return newRecipe
    }

    fun update(recipe: Recipe) {
        name = recipe.name
        ingredients = recipe.ingredients
        instructions = recipe.instructions
        notes = recipe.notes
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

    fun equals(other: Recipe): Boolean {
        if (ingredients != other.ingredients) return false
        if (instructions != other.instructions) return false
        if (notes != other.notes) return false
        if (name != other.name) return false
        if (id != other.id) return false
        return true
    }
}