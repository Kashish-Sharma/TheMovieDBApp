package kashish.com.ui.Activities

import android.app.SearchManager;
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.AbsListView
import android.widget.Toast
import kashish.com.R
import kashish.com.adapters.MovieAdapter
import kashish.com.interfaces.OnMovieClickListener
import kashish.com.models.Movie
import kashish.com.requestmodels.MovieRequest
import kashish.com.network.NetworkService
import kashish.com.utils.Constants
import kashish.com.utils.Urls
import retrofit2.Call
import retrofit2.Callback

class SearchActivity : AppCompatActivity(), OnMovieClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private val GRID_COLUMNS_PORTRAIT = 1
    private val GRID_COLUMNS_LANDSCAPE = 2
    private val TAG: String = SearchActivity::class.simpleName.toString()
    lateinit var mSearchAdapter: MovieAdapter
    var searchData: MutableList<Movie> = mutableListOf()
    private lateinit var mSearchRecyclerView : RecyclerView
    private lateinit var mGridLayoutManager: GridLayoutManager
    private lateinit var movie: Movie

    private lateinit var mSharedPreferences: SharedPreferences
    private lateinit var networkService: NetworkService

    private var pageNumber:Int = 1
    private var doPagination:Boolean = true
    private var isScrolling:Boolean = false
    private  var currentItem:Int = -1
    private  var totalItem:Int = -1
    private  var scrolledOutItem:Int = -1
    private var isLoading: Boolean = false

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
        initContentList()
        initSearchRecyclerView()

    }

    private fun initViews(){
        mSearchRecyclerView = findViewById(R.id.activity_search_recycler_view)

        searchData = mutableListOf()
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        networkService = NetworkService.instance
    }

    private fun initSearchRecyclerView(){
        configureRecyclerAdapter(resources.configuration.orientation)
        mSearchAdapter = MovieAdapter(searchData,this,mSharedPreferences)
        mSearchRecyclerView.setAdapter(mSearchAdapter)
    }

    private fun fetchSearchMovie(query: String){
        val call: Call<MovieRequest> = networkService.tmdbApi.getSearchMovies(Urls.TMDB_API_KEY
                ,"en-US",query,pageNumber,"false","US|IN|UK","2|3")

        call.enqueue(object : Callback<MovieRequest> {
            override fun onResponse(call: Call<MovieRequest>?, response: retrofit2.Response<MovieRequest>?) {

                val movieRequest: MovieRequest = response!!.body()!!
                Log.i("jhasbfbiuf",movieRequest.page.toString()+ " are the total pages")

                if (movieRequest.results!!.isEmpty()){
                    //stop call to pagination in any case
                    doPagination = false
                    //show msg no posts
                    if(pageNumber == 1)
                        Toast.makeText(this@SearchActivity,"Something went wrong", Toast.LENGTH_SHORT).show()
                    searchData.removeAt(searchData.size - 1)
                    mSearchAdapter.notifyItemRemoved(searchData.size-1)

                }
                else {

                    //Data loaded, remove progress
                    searchData.removeAt(searchData.size-1)
                    mSearchAdapter.notifyItemRemoved(searchData.size-1)

                    for (i in 0 until movieRequest.results!!.size){
                        val movie: Movie = movieRequest.results!!.get(i)

                        for (j in 0 until movie.genreIds!!.size) {
                            movie.genreString += Constants.getGenre(movie.genreIds!!.get(j)) + ", "
                        }

                        if (movie.posterPath.isNullOrEmpty()){
                            movie.posterPath = "asdsadad"
                        }

                        if (movie.backdropPath.isNullOrEmpty()){
                            movie.backdropPath = "asdsadad"
                        }

                        movie.contentType = Constants.CONTENT_SIMILAR
                        searchData.add(movie)

                    }

                    isLoading = false
                    mSearchAdapter.notifyItemRangeInserted(searchData.size - movieRequest.results!!.size, movieRequest.results!!.size)
                }


            }

            override fun onFailure(call: Call<MovieRequest>?, t: Throwable?) {
                Log.i(TAG,t!!.message+" is the error message")
            }

        })
    }

    private fun addProgressBarInList() {
        val progressBarContent = Movie()
        progressBarContent.contentType = Constants.CONTENT_PROGRESS
        searchData.add(progressBarContent)
    }
    private fun delayByfewSeconds(query: String){
        val handler = Handler()
        handler.postDelayed(Runnable {
            fetchSearchMovie(query)
        }, 2000)
    }

    private fun initContentList(){
        searchData = mutableListOf()
    }
    private fun setRecyclerViewScrollListener(query: String) {
        //Fetching next page's data on reaching bottom
        mSearchRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val reachedBottom = !recyclerView!!.canScrollVertically(1) && dy!=0
                if (reachedBottom && doPagination && !isLoading) {
                    addProgressBarInList()
                    mSearchAdapter.notifyItemInserted(searchData.size-1)
                    pageNumber++
                    isLoading = true
                    delayByfewSeconds(query)
                }
            }
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                    isScrolling = true
            }
        })
    }
    private fun getMovie(){
        movie = intent.getParcelableExtra("movie")
    }
    private fun clearList() {
        val size = searchData.size
        searchData.clear()
        mSearchAdapter.notifyItemRangeRemoved(0, size)
    }

    private fun setToolbar(){
        supportActionBar!!.title = resources.getString(R.string.search)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
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
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()))
        searchView.maxWidth = Integer.MAX_VALUE

        // listening to search query text change
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // filter recycler view when query submitted
                Log.i("SearchInfo", query + " is the onQueryTextSubmit")
                clearList()
                addProgressBarInList()
                delayByfewSeconds(query)
                setRecyclerViewScrollListener(query)
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
