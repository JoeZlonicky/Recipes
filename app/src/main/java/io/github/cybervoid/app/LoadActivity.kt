package io.github.cybervoid.app

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoadActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load)

        // Load recipes on a coroutine so progress bar updates while loading
        GlobalScope.launch {
            loadRecipes()
            loadingComplete()
        }
    }

    // Give RecipeDatabase the location of internal storage and then load recipes
    private fun loadRecipes() {
        RecipeDatabase.internalDir = filesDir
        RecipeDatabase.load()
    }

    // Go to MainActivity
    private fun loadingComplete() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
