package com.practicum.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private var searchBarTextValue: String? = null

    private lateinit var searchBar: EditText
    private lateinit var trackRecyclerView: RecyclerView
    private lateinit var outOfSearchButton: Button
    private lateinit var searchBarClearButton: ImageView


    private var trackList = ArrayList<Track>()
    private val trackAdapter = TrackAdapter(trackList)
    private val itunesService = Retrofit.itunesInstance

    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderText: TextView
    private lateinit var placeholderButton: Button
    private lateinit var placeholderLayout: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        outOfSearchButton = findViewById(R.id.outOfSearchButton)
        searchBar = findViewById(R.id.searchBarEditText)
        searchBarClearButton = findViewById(R.id.searchBarClearIcon)

        placeholderImage = findViewById(R.id.ivPlaceholderImage)
        placeholderText = findViewById(R.id.tvPlaceholderText)
        placeholderButton = findViewById(R.id.btnPlaceholderRefresh)
        placeholderLayout = findViewById(R.id.llPlaceholderLayuot)

        //Нажатие на кнопку Обновить
        placeholderButton.setOnClickListener {
            searchTracks(searchBarTextValue.toString())
        }

        //Выход с экрана поиска
        outOfSearchButton.setOnClickListener {
            finish()
        }

        // Очищаем строку поиска и убираем клавуатуру
        searchBarClearButton.setOnClickListener {
            searchBar.setText("")
            searchBarTextValue = null

            hideSearchBarKeyboard()
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            placheholderStateManager(PLACEHOLDER_HIDDEN)
        }

        // Переопределяем TextWatcher
        val searchBarTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty())
                    Toast.makeText(
                        this@SearchActivity,
                        getString(R.string.search_bar_empty),
                        Toast.LENGTH_SHORT
                    ).show()

                searchBarClearButton.visibility = clearSearchBarButtonVisibility(s)

                searchBarTextValue = s.toString()
            }
        }

        searchBar.addTextChangedListener(searchBarTextWatcher)

        // Оформляем список треков
        trackRecyclerView = findViewById(R.id.rv_trackList)
        trackRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        trackRecyclerView.adapter = trackAdapter

        // Фишка из теории - биндим кнопку DONE на вирт-клаве
        searchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTracks(searchBarTextValue.toString())
            }
            false
        }
    }

    // Видимость "крестика" для очистки строки поиска
    private fun clearSearchBarButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    // Спрятать клавиатурку
    private fun hideSearchBarKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(searchBar.windowToken, 0)
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

    private fun searchTracks(searchBarTextValue: String) {
        if (searchBarTextValue.trim().isNotEmpty()) {
            itunesService.getTracksOnSearch(searchBarTextValue).enqueue(object: Callback<TracksResponse> {
                override fun onResponse(
                    call: Call<TracksResponse>,
                    response: Response<TracksResponse>
                ) {
                    when (response.code()) {
                        200 -> {
                            if (!response.body()?.results.isNullOrEmpty()) {
                                trackList.clear()
                                trackList.addAll(response.body()?.results!!)
                                trackAdapter.notifyDataSetChanged()
                                placheholderStateManager(PLACEHOLDER_HIDDEN)
                            } else
                                placheholderStateManager(response.code())
                        }

                        else -> {
                            placheholderStateManager(response.code())
                        }
                    }
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    placheholderStateManager(PLACEHOLDER_ON_FAILURE)
                }
            })
        }
    }

    private fun placheholderStateManager (responseCode: Int) {
        if(responseCode == PLACEHOLDER_HIDDEN) {
            placeholderLayout.visibility = View.GONE
            return
        }

        trackList.clear()
        trackAdapter.notifyDataSetChanged()
        placeholderLayout.visibility = View.VISIBLE

        when (responseCode) {
            200 -> {
                placeholderImage.visibility = View.VISIBLE
                placeholderText.visibility = View.VISIBLE
                placeholderButton.visibility = View.GONE
                placeholderImage.setImageResource(R.drawable.not_found)
                placeholderText.setText(R.string.search_error_not_found)
            }

            else -> {
                placeholderImage.visibility = View.VISIBLE
                placeholderText.visibility = View.VISIBLE
                placeholderButton.visibility = View.VISIBLE
                placeholderImage.setImageResource(R.drawable.no_internet)
                placeholderText.setText(R.string.search_error_no_internet)
            }
        }

    }

    companion object {
        private const val EDIT_TEXT_VALUE = "EDIT_TEXT_VALUE"
        private const val PLACEHOLDER_HIDDEN = -1
        private const val PLACEHOLDER_ON_FAILURE = -2
    }
}