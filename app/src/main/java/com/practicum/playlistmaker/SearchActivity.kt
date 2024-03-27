package com.practicum.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast

class SearchActivity : AppCompatActivity() {

    private var searchBarTextValue: String? = null  // сюда будем записывать текст из поисковой строки
    private lateinit var searchBar: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val outOfSearchButton = findViewById<Button>(R.id.outOfSearchButton)
        searchBar = findViewById(R.id.searchBarEditText)
        val searchBarClearButton = findViewById<ImageView>(R.id.searchBarClearIcon)

        //Выход с экрана поиска
        outOfSearchButton.setOnClickListener {
            finish()
        }

        // Очищаем строку поиска и убираем клавуатуру
        searchBarClearButton.setOnClickListener {
            searchBar.setText("")

            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchBar.windowToken, 0)
        }

        // Переопределяем TextWatcher
        val searchBarTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty())
                    Toast.makeText(this@SearchActivity, "Строка поиска пуста", Toast.LENGTH_SHORT).show()

                searchBarClearButton.visibility = clearSearchBarButtonVisibility(s)

                searchBarTextValue = s.toString()
            }
        }

        searchBar.addTextChangedListener(searchBarTextWatcher)
    }

    // Видимость "крестика" для очистки строки поиска
    private fun clearSearchBarButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDIT_TEXT_VALUE, searchBarTextValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchBarTextValue = savedInstanceState.getString(EDIT_TEXT_VALUE)
        searchBar.setText(searchBarTextValue)
    }

    companion object {
        const val EDIT_TEXT_VALUE = "EDIT_TEXT_VALUE"
    }
}