package kashish.com.ui.Activities

import android.arch.lifecycle.LiveData
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.ActionBar
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import kashish.com.R
import kashish.com.adapters.MovieAdapter
import kashish.com.interfaces.OnMovieClickListener
import kashish.com.models.Movie
import kashish.com.utils.Constants
import android.view.MenuItem
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import kashish.com.database.AppDatabase
import kashish.com.database.AppExecutors
import kashish.com.database.Entities.FavouritesEntry
import kashish.com.requestmodels.MovieRequest
import kashish.com.network.NetworkService
import kashish.com.API_KEY.Companion.TMDB_API_KEY
import kashish.com.utils.Helpers
import retrofit2.Call
import retrofit2.Callback
import java.util.*


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
    private var isLoading: Boolean = false

    //Collapsing Toolbar
    private lateinit var mCollapsingToolbar: CollapsingToolbarLayout
    private lateinit var mActionBar: ActionBar
    private lateinit var mToolbar: Toolbar
    private lateinit var mAppBarLayout: AppBarLayout
    private lateinit var mBackdropImageView: ImageView
    private lateinit var mAddToFavourite: CheckBox
    private lateinit var mToolbarMovieTitle: TextView

    //Database
    private lateinit var mDatabase: AppDatabase


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
        Helpers.setUpTransparentStatusBar(window)

        getMovie()
        initViews()
        initToolBar()
        setupCollapsingToolbar()
        initContentList()
        initSimilarRecyclerView()
        delayByfewSeconds()
        setRecyclerViewScrollListener()
        setSwipeRefreshLayoutListener()
        setFavouriteOnClickListener()
        isMovieFavourite()

    }

    private fun initViews(){

        mSimilarRecyclerView = findViewById(R.id.activity_similar_recycler_view)
        mSimilarSwipeToRefresh = findViewById(R.id.activity_similar_swipe_to_refresh)

        similarData = mutableListOf()
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        networkService = NetworkService.instance
        mDatabase = AppDatabase.getInstance(applicationContext)
    }

    private fun initSimilarRecyclerView(){
        configureRecyclerAdapter(resources.configuration.orientation)
        mSimilarAdapter = MovieAdapter(similarData,this,mSharedPreferences)
        mSimilarRecyclerView.setAdapter(mSimilarAdapter)
    }

    private fun fetchSimilarMovie(){
        val call: Call<MovieRequest> = networkService.tmdbApi.getRecommendedMovies(movie.id.toString(),TMDB_API_KEY
                ,"en-US",pageNumber)

        call.enqueue(object : Callback<MovieRequest> {
            override fun onResponse(call: Call<MovieRequest>?, response: retrofit2.Response<MovieRequest>?) {

                val movieRequest: MovieRequest = response!!.body()!!
                Log.i("jhasbfbiuf",movieRequest.page.toString()+ " are the total pages")

                if (movieRequest.results!!.isEmpty()){
                    //stop call to pagination in any case
                    doPagination = false
                    //show msg no posts
                    if(pageNumber == 1){
                        Toast.makeText(this@SimilarMoviesActivity,"Can't find similar movies", Toast.LENGTH_SHORT).show()
                        finish()
                    }

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
                            if(j==movie.genreIds!!.size-1)
                                movie.genreString += Constants.getGenre(movie.genreIds!!.get(j))
                            else
                                movie.genreString += Constants.getGenre(movie.genreIds!!.get(j))+", "
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
                Toast.makeText(this@SimilarMoviesActivity,"Error: "+t!!.message, Toast.LENGTH_SHORT).show()
                Log.i(TAG,t.message+" is the error message")
                finish()
            }

        })
    }

    private fun initToolBar(){
        mCollapsingToolbar = findViewById(R.id.activity_similar_collapsing_layout)
        mToolbar = findViewById(R.id.activity_similar_toolbar)
        mAppBarLayout = findViewById(R.id.activity_similar_app_bar_layout)
        mBackdropImageView = findViewById(R.id.activity_similar_backdrop_image)

        mToolbarMovieTitle = findViewById(R.id.activity_similar_movie_title)
        mAddToFavourite = findViewById(R.id.activity_similar_add_to_favourite)

        setSupportActionBar(mToolbar)
        mActionBar = supportActionBar!!
        mActionBar.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupCollapsingToolbar(){
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this)

        if (mSharedPreferences.getBoolean(getString(R.string.pref_cache_data_key),true)){
            Glide.with(this).load(Helpers.buildBackdropImageUrl(movie.backdropPath!!))
                    .transition(DrawableTransitionOptions.withCrossFade()).into(mBackdropImageView)
        } else{
            Glide.with(this).load(Helpers.buildBackdropImageUrl(movie.backdropPath!!))
                    .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true))
                    .transition(DrawableTransitionOptions.withCrossFade()).into(mBackdropImageView)
        }


        mToolbarMovieTitle.setText(movie.title)

    }

    private fun isMovieFavourite(){
        //Checking if already added to favourite
        val entry: LiveData<MutableList<FavouritesEntry>> = mDatabase.favouritesDao().checkIfFavourite(movie.id!!)
        entry.observe(this, android.arch.lifecycle.Observer {
            when {
                it!!.size == 0 -> mAddToFavourite.isChecked = false
                else -> mAddToFavourite.isChecked = true
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
    private fun setFavouriteOnClickListener(){
        mAddToFavourite.setOnClickListener({
            val movieEntry = FavouritesEntry()
            movieEntry.movieId = movie.id
            movieEntry.voteCount = movie.voteCount
            movieEntry.video = movie.video
            movieEntry.voteAverage = movie.voteAverage
            movieEntry.title = movie.title
            movieEntry.popularity = movie.popularity
            movieEntry.posterPath = movie.posterPath
            movieEntry.originalLanguage = movie.originalLanguage
            movieEntry.originalTitle = movie.originalTitle
            movieEntry.genreIds = movie.genreString
            movieEntry.backdropPath = movie.backdropPath
            movieEntry.adult = movie.adult
            movieEntry.overview = movie.overview
            movieEntry.releaseDate = movie.releaseDate
            movieEntry.genreString = movie.genreString
            movieEntry.timeAdded = Date().time
            movieEntry.tableName = Constants.FAVOURITES

            if (mAddToFavourite.isChecked){
                AppExecutors.getInstance().diskIO().execute({
                    kotlin.run {
                        mDatabase.favouritesDao().insertFavourite(movieEntry)
                    }
                })
                Toast.makeText(this,"Added", Toast.LENGTH_SHORT).show()
            } else{
                AppExecutors.getInstance().diskIO().execute({
                    kotlin.run {
                        mDatabase.favouritesDao().deleteFavourite(movieEntry)
                    }
                })
            }
        })
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
        val similarIntent = Intent(this, DetailActivity::class.java)
        similarIntent.putExtra("movie",movie)
        startActivity(similarIntent)
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

}
