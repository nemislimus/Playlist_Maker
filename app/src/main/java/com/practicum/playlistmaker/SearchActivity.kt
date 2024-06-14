package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

class SearchActivity : AppCompatActivity() {

    // переменные для сохранения состояний и хранения данных
    private var searchBarTextValue: String? = null
    private var placeholderStateSaver: Int = 0

    private var trackList = ArrayList<Track>()
    private var trackListValue: String? = Gson().toJson(trackList)
    private var historyTrackList: ArrayList<Track>? = null

    // переменные для основных View на экране
    private lateinit var searchBar: EditText
    private lateinit var trackRecyclerView: RecyclerView
    private lateinit var outOfSearchToolbar: Toolbar
    private lateinit var searchBarClearButton: ImageView
    private lateinit var searchProgressBar: ProgressBar

    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderText: TextView
    private lateinit var placeholderButton: Button
    private lateinit var placeholderLayout: LinearLayout

    private lateinit var historyText: TextView

    // Рабочие инструменты для настроек и логики
    private var trackAdapter = TrackAdapter(
        { if (clickListItemDebounce()) manageListItemClick(it) },
        { clearHistory(it) },
    )
    private val itunesService = Retrofit.itunesInstance
    private val searchRunnable = Runnable { searchTracks(searchBarTextValue) }

    private var isListItemClickAllowed: Boolean = true
    private val searchActivityHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        outOfSearchToolbar = findViewById(R.id.searchToolBar)
        searchBar = findViewById(R.id.searchBarEditText)
        searchBarClearButton = findViewById(R.id.searchBarClearIcon)
        searchProgressBar = findViewById(R.id.searchProgressBar)

        placeholderImage = findViewById(R.id.ivPlaceholderImage)
        placeholderText = findViewById(R.id.tvPlaceholderText)
        placeholderButton = findViewById(R.id.btnPlaceholderRefresh)
        placeholderLayout = findViewById(R.id.llPlaceholderLayout)

        historyText = findViewById(R.id.tvSearchHistory)

        // Достаём значения history из SP
        val sharedPrefs = getSharedPreferences(PlaylistApp.APP_PREFERENCES, MODE_PRIVATE)
        historyTrackList =
            sharedPrefs.getString(HISTORY_KEY, null)?.let { createTrackListFromJson(it) }


        //Нажатие на кнопку плейсхолдера "Обновить"
        placeholderButton.setOnClickListener {
            searchTracks(searchBarTextValue.toString())
        }

        //Выход с экрана поиска
        outOfSearchToolbar.setOnClickListener {
            finish()
        }

        // Очищаем строку поиска и убираем виртуальную клавуатуру
        searchBarClearButton.setOnClickListener {
            searchBar.setText("")
            searchBarTextValue = null
            placeholderStateSaver = PLACEHOLDER_HIDDEN
            hideSearchBarKeyboard()
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
        }

        // Обрабатываем отображение истории при фокусе на searchBar
        searchBar.setOnFocusChangeListener { _, hasFocus ->
            manageHistoryVisibilityOnChanges(hasFocus, searchBar.text.toString())
        }

        // Переопределяем TextWatcher
        val searchBarTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                manageHistoryVisibilityOnChanges(searchBar.hasFocus(), s.toString())
                if (s.isNullOrEmpty()) placheholderStateManager(PLACEHOLDER_HIDDEN)
                searchBarClearButton.visibility = clearSearchBarButtonVisibility(s)
                searchBarTextValue = s.toString()
                searchTracksDebounce()
            }
        }

        searchBar.addTextChangedListener(searchBarTextWatcher)

        // Оформляем список треков (нужный список треков для отображения подгружаем в зависимости от состояния активити)
        trackRecyclerView = findViewById(R.id.rv_trackList)
        trackRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        trackRecyclerView.adapter = trackAdapter
    }

    override fun onResume() {
        super.onResume()

        val sharedPrefs = getSharedPreferences(PlaylistApp.APP_PREFERENCES, MODE_PRIVATE)
        historyTrackList =
            sharedPrefs.getString(HISTORY_KEY, null)?.let { createTrackListFromJson(it) }

        // При условии, что мы вернулись с плеера, мы показываем результат поиска, а если в плеер заходили
        // с истории, то показываем историю
        if (trackList.isEmpty()) {
            manageHistoryVisibilityOnStart()
        } else {
            historyTrackList?.add(trackForButtonInflate)
            trackAdapter.tracks = trackList
            trackAdapter.notifyDataSetChanged()
        }
    }

    override fun onStop() {
        super.onStop()

        historyTrackList?.removeAt(historyTrackList?.size!! - 1)
        val sharedPrefs = getSharedPreferences(PlaylistApp.APP_PREFERENCES, MODE_PRIVATE)

        if (historyTrackList != null) {
            sharedPrefs.edit()
                .putString(HISTORY_KEY, createJsonFromTrackList(historyTrackList!!))
                .apply()
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

    private fun manageHistoryVisibilityOnStart() {
        if (historyTrackList == null || historyTrackList?.isEmpty() == true) {
            historyText.visibility = View.GONE
            trackAdapter.tracks = trackList
        } else {
            historyText.visibility = View.VISIBLE
            historyTrackList?.add(trackForButtonInflate)
            trackAdapter.tracks = historyTrackList!!
            trackAdapter.notifyDataSetChanged()
        }
    }

    private fun manageHistoryVisibilityOnChanges(focus: Boolean, text: String?) {
        if (historyTrackList == null || historyTrackList?.isEmpty() == true) {
            historyText.visibility = View.GONE
        } else {
            historyText.visibility =
                if (focus && text?.isEmpty() == true) View.VISIBLE else View.GONE
        }

        if (historyText.visibility == View.VISIBLE) {
            trackAdapter.tracks = historyTrackList!!
        } else {
            trackAdapter.tracks = trackList
        }
    }

    private fun manageListItemClick(track: Track) {
        // Тут мы выполняем работу с историей
        val sharedPrefs = getSharedPreferences(PlaylistApp.APP_PREFERENCES, MODE_PRIVATE)
        val restoreTrackList = sharedPrefs.getString(HISTORY_KEY, null)
            ?.let { createTrackListFromJson(it) }

        if (historyTrackList != null) {
            historyTrackList?.clear()
            if (restoreTrackList != null) {
                historyTrackList?.addAll(restoreTrackList)
            }
            val indexOfTwin = historyTrackList?.indexOfFirst { track.trackId == it.trackId }

            if (indexOfTwin != null && indexOfTwin != -1) {
                historyTrackList?.removeAt(indexOfTwin)
            }

            historyTrackList?.add(0, track)

            if (historyTrackList?.size!! > 10) {
                historyTrackList?.removeAt(historyTrackList?.size!! - 1)
            }

            sharedPrefs.edit()
                .putString(HISTORY_KEY, createJsonFromTrackList(historyTrackList!!))
                .apply()

            historyTrackList?.add(trackForButtonInflate)
            if (historyText.visibility == View.VISIBLE) {
                trackAdapter.notifyDataSetChanged()
                trackRecyclerView.scrollToPosition(0)
            }

        } else {
            historyTrackList = arrayListOf()
            historyTrackList?.add(track)

            sharedPrefs.edit()
                .putString(HISTORY_KEY, createJsonFromTrackList(historyTrackList!!))
                .apply()

            historyTrackList?.add(trackForButtonInflate)
        }

        // Переходим на экран плеера, передавая ему объект на который нажали
        val playerIntetn = Intent(this, PlayerActivity::class.java).apply {
            putExtra(PlaylistApp.TRACK_KEY, createJsonFromTrack(track))
        }
        startActivity(playerIntetn)
    }

    private fun clearHistory(track: Track) {
        val sharedPrefs = getSharedPreferences(PlaylistApp.APP_PREFERENCES, MODE_PRIVATE)
        sharedPrefs.edit()
            .remove(HISTORY_KEY)
            .apply()

        historyTrackList = null
        manageHistoryVisibilityOnChanges(searchBar.hasFocus(), searchBar.text.toString())
    }

    private fun createJsonFromTrackList(fact: ArrayList<Track>): String {
        return Gson().toJson(fact)
    }

    private fun createTrackListFromJson(jsonValue: String): ArrayList<Track> {
        return Gson().fromJson(jsonValue, trackListType)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(EDIT_TEXT_VALUE, searchBarTextValue)
        trackListValue = Gson().toJson(trackList)
        outState.putString(TRACK_LIST_VALUE, trackListValue)
        outState.putInt(PLACEHOLDER_SAVER, placeholderStateSaver)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchBarTextValue = savedInstanceState.getString(EDIT_TEXT_VALUE)
        searchBar.setText(searchBarTextValue)
        trackListValue = savedInstanceState.getString(TRACK_LIST_VALUE)
        placeholderStateSaver = savedInstanceState.getInt(PLACEHOLDER_SAVER)
        placheholderStateManager(placeholderStateSaver)

        val restoreTrackList = Gson().fromJson<ArrayList<Track>>(trackListValue, trackListType)
        trackList.clear()
        trackList.addAll(restoreTrackList)
    }

    private fun searchTracks(searchBarTextValue: String?) {
        if (searchBarTextValue?.trim()?.isNotEmpty() == true) {
            trackRecyclerView.isVisible = false
            searchProgressBar.isVisible = true
            itunesService.getTracksOnSearch(searchBarTextValue)
                .enqueue(object : Callback<TracksResponse> {
                    override fun onResponse(
                        call: Call<TracksResponse>,
                        response: Response<TracksResponse>
                    ) {
                        searchProgressBar.isVisible = false
                        trackRecyclerView.isVisible = true
                        when (response.code()) {
                            200 -> {
                                if (!response.body()?.results.isNullOrEmpty()) {
                                    placeholderStateSaver = PLACEHOLDER_HIDDEN
                                    trackList.clear()
                                    trackList.addAll(response.body()?.results!!)
                                    trackAdapter.notifyDataSetChanged()
                                    placheholderStateManager(PLACEHOLDER_HIDDEN)
                                    Log.d(
                                        "RESPONSE_LOG",
                                        "200 - LIST ON. code:${response.code()} body:${response.body()?.results} "
                                    )

                                } else {
                                    placeholderStateSaver = 200
                                    placheholderStateManager(response.code())
                                    Log.d(
                                        "RESPONSE_LOG",
                                        "200 -LIST OFF. placeholderSaver = $placeholderStateSaver. code:${response.code()} " +
                                                "body:${response.body()?.results} "
                                    )
                                }
                            }

                            else -> {
                                placeholderStateSaver = PLACEHOLDER_ON_FAILURE
                                placheholderStateManager(response.code())
                                Log.d(
                                    "RESPONSE_LOG",
                                    "NOT 200 -LIST OFF. code:${response.code()} body:${response.body()} "
                                )
                            }
                        }
                    }

                    override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                        searchProgressBar.isVisible = false
                        trackRecyclerView.isVisible = true
                        placeholderStateSaver = PLACEHOLDER_ON_FAILURE
                        placheholderStateManager(PLACEHOLDER_ON_FAILURE)
                        Log.d("RESPONSE_LOG", "FAILURE ")
                    }
                })
        }
    }

    private fun placheholderStateManager(responseCode: Int) {
        if (responseCode == PLACEHOLDER_HIDDEN) {
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

    private fun clickListItemDebounce(): Boolean {
        val current = isListItemClickAllowed
        if (isListItemClickAllowed) {
            isListItemClickAllowed = false
            searchActivityHandler.postDelayed(
                { isListItemClickAllowed = true },
                CLICK_DEBOUNCE_DELAY
            )
        }
        return current
    }

    private fun searchTracksDebounce() {
        searchActivityHandler.removeCallbacks(searchRunnable)
        searchActivityHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L

        private const val EDIT_TEXT_VALUE = "EDIT_TEXT_VALUE"
        private const val TRACK_LIST_VALUE = "TRACK_LIST_VALUE"
        private const val PLACEHOLDER_SAVER = "PLACEHOLDER_SAVER"

        private const val PLACEHOLDER_HIDDEN = -1
        private const val PLACEHOLDER_ON_FAILURE = -2

        const val HISTORY_KEY = "history_key"

        private val trackForButtonInflate: Track = Track(
            -1,
            "plm",
            "plm",
            210743,
            "plm",
            "plm",
            "plm",
            "plm",
            "plm",
            "plm",
        )

        private val trackListType: Type? = object : TypeToken<ArrayList<Track>>() {}.type
    }
}