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
}