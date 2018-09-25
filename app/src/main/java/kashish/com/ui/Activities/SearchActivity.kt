package kashish.com.ui.Activities

import android.app.SearchManager;
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import kashish.com.Injection
import kashish.com.R
import kashish.com.adapters.NowShowingAdapter
import kashish.com.adapters.SearchAdapter
import kashish.com.database.AppDatabase
import kashish.com.database.AppExecutors
import kashish.com.database.Entities.SearchEntry
import kashish.com.interfaces.OnMovieClickListener
import kashish.com.models.Movie
import kashish.com.network.NetworkService
import kashish.com.utils.Constants
import kashish.com.utils.Constants.Companion.SEARCHES
import kashish.com.viewmodels.SearchViewModel

class SearchActivity : AppCompatActivity(), OnMovieClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private val GRID_COLUMNS_PORTRAIT = 1
    private val GRID_COLUMNS_LANDSCAPE = 2
    private val TAG: String = SearchActivity::class.simpleName.toString()

    private lateinit var viewModel: SearchViewModel
    private lateinit var mSearchAdapter: SearchAdapter

    private lateinit var emptyList: TextView
    private lateinit var mSearchRecyclerView : RecyclerView
    private lateinit var mGridLayoutManager: GridLayoutManager

    private lateinit var mDatabase: AppDatabase
    private lateinit var mSharedPreferences: SharedPreferences
    private lateinit var networkService: NetworkService


    override fun onCreate(savedInstanceState: Bundle?) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (mSharedPreferences.getBoolean(getString(R.string.pref_night_mode_key)
                ,resources.getBoolean(R.bool.pref_night_mode_default_value))) {
            setTheme(R.style.AppThemeSearchDark)
        } else{
            setTheme(R.style.AppThemeSearch)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()
        setToolbar()
        initSearchRecyclerView()

    }

    private fun initViews(){
        mSearchRecyclerView = findViewById(R.id.activity_search_recycler_view)
        emptyList = findViewById(R.id.emptyList)
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        networkService = NetworkService.instance
        mDatabase = AppDatabase.getInstance(this)
    }

    private fun initSearchRecyclerView(){
        configureRecyclerAdapter(resources.configuration.orientation)
        viewModel = ViewModelProviders.of(this, Injection.provideSearchViewModelFactory(this))
                .get(SearchViewModel::class.java)

        mSearchAdapter = SearchAdapter(this,mSharedPreferences)
        mSearchRecyclerView.adapter = mSearchAdapter
        Toast.makeText(this,"Started",Toast.LENGTH_SHORT).show()
        viewModel.searches.observe(this, Observer<PagedList<SearchEntry>> {
            Log.d("Activity", "list: ${it?.size}")
            showEmptyList(it?.size == 0)
            mSearchAdapter.submitList(it!!)
        })
        viewModel.networkErrors.observe(this, Observer<String> {
            Toast.makeText(this, "\uD83D\uDE28 Wooops ${it}", Toast.LENGTH_LONG).show()
        })
    }


    private fun showEmptyList(show: Boolean) {
        if (show) {
            emptyList.visibility = View.VISIBLE
            mSearchRecyclerView.visibility = View.GONE
        } else {
            emptyList.visibility = View.GONE
            mSearchRecyclerView.visibility = View.VISIBLE
        }
    }



    private fun setToolbar(){
        supportActionBar!!.title = resources.getString(R.string.search)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun convertEntryToMovieList(list: List<SearchEntry>): MutableList<Movie>{
        val movieList: MutableList<Movie> = mutableListOf()
        for(i in 0 until list.size)
        {       val movie = list.get(i)
            val passMovie = Movie()
            passMovie.id = movie.movieId
            passMovie.voteCount = movie.voteCount
            passMovie.video = movie.video
            passMovie.voteAverage = movie.voteAverage
            passMovie.title = movie.title
            passMovie.popularity = movie.popularity
            passMovie.posterPath = movie.posterPath!!
            passMovie.originalLanguage = movie.originalLanguage
            passMovie.originalTitle = movie.originalTitle
            passMovie.backdropPath = movie.backdropPath!!
            passMovie.adult = movie.adult
            passMovie.overview = movie.overview
            passMovie.releaseDate = movie.releaseDate
            passMovie.genreString = movie.genreString!!
            passMovie.contentType = Constants.CONTENT_MOVIE
            passMovie.tableName = SEARCHES
            movieList.add(passMovie)
        }
        return movieList
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item!!.getItemId()
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        configureRecyclerAdapter(newConfig!!.orientation)
    }

    private fun configureRecyclerAdapter(orientation: Int) {
        val isPortrait = orientation == Configuration.ORIENTATION_PORTRAIT
        mGridLayoutManager = GridLayoutManager(this, if (isPortrait) GRID_COLUMNS_PORTRAIT else GRID_COLUMNS_LANDSCAPE)
        mSearchRecyclerView.setLayoutManager(mGridLayoutManager)
    }


    override fun onMovieClickListener(movie: Movie) {
        val detailIntent = Intent(this, DetailActivity::class.java)
        detailIntent.putExtra("movie",movie)
        startActivity(detailIntent)
    }

    private fun restartActivity(){
        this.recreate()
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, key: String?) {
        if(key.equals(getString(R.string.pref_night_mode_key)))
            restartActivity()
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        menu.findItem(R.id.search).expandActionView()
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()))
        searchView.maxWidth = Integer.MAX_VALUE

        // listening to search query text change
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // filter recycler view when query submitted
                Log.i("SearchInfo", query + " is the onQueryTextSubmit")
                AppExecutors.getInstance().diskIO().execute(Runnable {
                    mDatabase.searchDao().deleteAll()
                })
                mSearchRecyclerView.scrollToPosition(0)
                viewModel.searchRepo(query)
                mSearchAdapter.submitList(null)
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                // filter recycler view when text is changed
                Log.i("SearchInfo", query + " is the onQueryTextChange")
                return false
            }
        })
        return true
    }

}
