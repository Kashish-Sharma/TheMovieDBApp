package kashish.com.ui.Activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.AbsListView
import kashish.com.R
import kashish.com.adapters.MovieAdapter
import kashish.com.interfaces.OnMovieClickListener
import kashish.com.models.Movie
import kashish.com.utils.Constants
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import kashish.com.requestmodels.MovieRequest
import kashish.com.singleton.NetworkService
import kashish.com.utils.Urls
import retrofit2.Call
import retrofit2.Callback


class SimilarMoviesActivity : AppCompatActivity(), OnMovieClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    //Similar movies
    private val GRID_COLUMNS_PORTRAIT = 1
    private val GRID_COLUMNS_LANDSCAPE = 2
    private val TAG: String = SimilarMoviesActivity::class.simpleName.toString()
    lateinit var mSimilarAdapter: MovieAdapter
    var similarData: MutableList<Movie> = mutableListOf()
    private lateinit var mSimilarRecyclerView : RecyclerView
    private lateinit var mSimilarSwipeToRefresh : SwipeRefreshLayout
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

    //Toolbar
    private lateinit var mToolbar: Toolbar
    private lateinit var mToolbarTitle:TextView
    private lateinit var mToolbarSubtitle:TextView


    override fun onCreate(savedInstanceState: Bundle?) {

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (mSharedPreferences.getBoolean(getString(R.string.pref_night_mode_key)
                ,resources.getBoolean(R.bool.pref_night_mode_default_value))) {
            setTheme(R.style.AppThemeDark)
        } else{
            setTheme(R.style.AppTheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_similar_movies)

        getMovie()
        initViews()
        setToolbar()
        initContentList()
        initSimilarRecyclerView()
        delayByfewSeconds()
        setRecyclerViewScrollListener()
        setSwipeRefreshLayoutListener()

    }

    private fun initViews(){
        mToolbar = findViewById(R.id.activity_similar_toolbar)
        mToolbarSubtitle = mToolbar.findViewById(R.id.similar_toolbar_subtitle)
        mToolbarTitle = mToolbar.findViewById(R.id.similar_toolbar_title)
        mSimilarRecyclerView = findViewById(R.id.activity_similar_recycler_view)
        mSimilarSwipeToRefresh = findViewById(R.id.activity_similar_swipe_to_refresh)

        similarData = mutableListOf()
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        networkService = NetworkService.instance
    }

    private fun initSimilarRecyclerView(){
        configureRecyclerAdapter(resources.configuration.orientation)
        mSimilarAdapter = MovieAdapter(similarData,this,mSharedPreferences)
        mSimilarRecyclerView.setAdapter(mSimilarAdapter)
    }

    private fun fetchSimilarMovie(){
        val call: Call<MovieRequest> = networkService.tmdbApi.getRecommendedMovies(movie.id.toString(),Urls.TMDB_API_KEY
                ,"en-US",pageNumber)

        call.enqueue(object : Callback<MovieRequest> {
            override fun onResponse(call: Call<MovieRequest>?, response: retrofit2.Response<MovieRequest>?) {

                val movieRequest: MovieRequest = response!!.body()!!
                Log.i("jhasbfbiuf",movieRequest.page.toString()+ " are the total pages")

                if (movieRequest.results!!.isEmpty()){
                    //stop call to pagination in any case
                    doPagination = false
                    //show msg no posts
                    if(pageNumber == 1)
                        Toast.makeText(this@SimilarMoviesActivity,"Something went wrong", Toast.LENGTH_SHORT).show()

                    similarData.removeAt(similarData.size - 1)
                    mSimilarAdapter.notifyItemRemoved(similarData.size-1)
                    mSimilarSwipeToRefresh.isRefreshing = false

                }
                else {

                    //Data loaded, remove progress
                    similarData.removeAt(similarData.size-1)
                    mSimilarAdapter.notifyItemRemoved(similarData.size-1)

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
                        similarData.add(movie)

                    }

                    isLoading = false
                    if (mSimilarSwipeToRefresh.isRefreshing())
                        mSimilarSwipeToRefresh.setRefreshing(false)
                    mSimilarAdapter.notifyItemRangeInserted(similarData.size - movieRequest.results!!.size, movieRequest.results!!.size)
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
        similarData.add(progressBarContent)
    }
    private fun delayByfewSeconds(){
        val handler = Handler()
        handler.postDelayed(Runnable {
            fetchSimilarMovie()
        }, 2000)
    }
    private fun setSwipeRefreshLayoutListener() {
        mSimilarSwipeToRefresh.setOnRefreshListener {
            pageNumber = 1
            doPagination = true
            clearList()
            addProgressBarInList()
            fetchSimilarMovie()
        }
    }
    private fun initContentList(){
        similarData = mutableListOf()
        addProgressBarInList()
    }
    private fun setRecyclerViewScrollListener() {
        //Fetching next page's data on reaching bottom
        mSimilarRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val reachedBottom = !recyclerView!!.canScrollVertically(1) && dy!=0
                if (reachedBottom && doPagination && !isLoading) {
                    addProgressBarInList()
                    mSimilarAdapter.notifyItemInserted(similarData.size-1)
                    pageNumber++
                    isLoading = true
                    delayByfewSeconds()
                }

//                currentItem = mGridLayoutManager.childCount
//                totalItem = mGridLayoutManager.itemCount
//                scrolledOutItem = mGridLayoutManager.findFirstVisibleItemPosition()
//
//                if (isScrolling && doPagination && !isLoading && (currentItem+scrolledOutItem == totalItem)){
//                    pageNumber++
//                    isScrolling = false
//                    isLoading = true
//                    delayByfewSeconds()
//                }


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
        val size = similarData.size
        similarData.clear()
        mSimilarAdapter.notifyItemRangeRemoved(0, size)
    }

    private fun setToolbar(){
        mToolbarTitle.setText(movie.title)
        mToolbarSubtitle.setText("Similar movies")
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
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
        mSimilarRecyclerView.setLayoutManager(mGridLayoutManager)
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
        if(key.equals(getString(R.string.pref_night_mode_key))){
            if (p0!!.getBoolean(key,resources.getBoolean(R.bool.pref_night_mode_default_value))){
                restartActivity()
            } else{
                restartActivity()            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this)
    }

}
