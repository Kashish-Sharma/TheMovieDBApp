package kashish.com.ui.Activities

import android.content.Intent
import android.content.res.Configuration
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.ProgressBar
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import kashish.com.R
import kashish.com.adapters.MovieAdapter
import kashish.com.interfaces.OnMovieClickListener
import kashish.com.models.Movie
import kashish.com.singleton.VolleySingleton
import kashish.com.utils.Constants
import kashish.com.utils.Helpers
import org.json.JSONArray
import org.json.JSONObject

class SimilarMoviesActivity : AppCompatActivity(), OnMovieClickListener {

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

    private var pageNumber:Int = 1
    private var doPagination:Boolean = true
    private var isScrolling:Boolean = false
    private  var currentItem:Int = -1
    private  var totalItem:Int = -1
    private  var scrolledOutItem:Int = -1
    private var isLoading: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_similar_movies)

        getMovie()
        initViews()
        initContentList()
        initSimilarRecyclerView()
        delayByfewSeconds()
        setRecyclerViewScrollListener()
        setSwipeRefreshLayoutListener()

    }

    private fun initViews(){
        mSimilarRecyclerView = findViewById(R.id.activity_similar_recycler_view)
        mSimilarSwipeToRefresh = findViewById(R.id.activity_similar_swipe_to_refresh)
    }

    private fun initSimilarRecyclerView(){
        configureRecyclerAdapter(resources.configuration.orientation)
        mSimilarAdapter = MovieAdapter(similarData,this)
        mSimilarRecyclerView.setAdapter(mSimilarAdapter)
    }

    private fun fetchSimilarMovie(){

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET,
                Helpers.buildRecommendedMoviesUrl(pageNumber,movie.id.toString()),
                null, Response.Listener { response ->

            val jsonArray: JSONArray = response.getJSONArray(Constants.RESULTS)

            if (jsonArray.length() == 0){
                //stop call to pagination in any case
                doPagination = false

                //show msg no posts
                if(pageNumber == 1)
                    Toast.makeText(this,"Something went wrong", Toast.LENGTH_SHORT).show()
                similarData.removeAt(similarData.size - 1)
                mSimilarAdapter.notifyItemRemoved(similarData.size-1)

            } else {

                //Data loaded, remove progress
                similarData.removeAt(similarData.size-1)
                mSimilarAdapter.notifyItemRemoved(similarData.size-1)


                for (i in 0 until jsonArray.length()) {
                    val jresponse: JSONObject = jsonArray.getJSONObject(i)

                    val movie = Movie()

                    movie.totalPages = response.getInt(Constants.TOTAL_PAGES)
                    movie.voteCount = jresponse.getInt(Constants.VOTE_COUNT)
                    movie.id = jresponse.getInt(Constants.ID)
                    movie.video = jresponse.getBoolean(Constants.VIDEO)
                    movie.voteAverage = jresponse.getDouble(Constants.VOTE_AVERAGE).toFloat()
                    movie.title = jresponse.getString(Constants.TITLE)
                    movie.popularity = jresponse.getDouble(Constants.POPULARITY).toFloat()
                    movie.posterPath = jresponse.getString(Constants.POSTER_PATH)
                    movie.originalLanguage = jresponse.getString(Constants.ORIGINAL_LANGUAGE)
                    movie.originalTitle = jresponse.getString(Constants.ORIGINAL_TITLE)

                    val array: JSONArray = jresponse.getJSONArray(Constants.GENRE_IDS)
                    //val genreList: MutableList<Int> = mutableListOf()
                    for (j in 0 until array.length()) {
                        //genreList.add(array.getInt(j))
                        movie.genreString += Constants.getGenre(array.getInt(j)) + ", "
                    }

                    //movie.genreIds = genreList
                    movie.backdropPath = jresponse.getString(Constants.BACKDROP_PATH)
                    movie.adult = jresponse.getBoolean(Constants.ADULT)
                    movie.overview = jresponse.getString(Constants.OVERVIEW)
                    movie.releaseDate = jresponse.getString(Constants.RELEASE_DATE)
                    movie.contentType = Constants.CONTENT_SIMILAR

                    similarData.add(movie)
                }

                //addProgressBarInList()

                mSimilarAdapter.notifyItemRangeInserted(similarData.size - jsonArray.length(), jsonArray.length())

                isLoading = false

                if (mSimilarSwipeToRefresh.isRefreshing())
                    mSimilarSwipeToRefresh.setRefreshing(false)
            }

        }, Response.ErrorListener { error ->
            Log.i(TAG,error.message+" is the error message")
        })

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
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

}
