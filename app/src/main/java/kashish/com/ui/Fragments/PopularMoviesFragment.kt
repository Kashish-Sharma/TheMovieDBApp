package kashish.com.ui.Fragments


import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest

import kashish.com.R
import kashish.com.adapters.MovieAdapter
import kashish.com.interfaces.OnMovieClickListener
import kashish.com.models.Movie
import kashish.com.singleton.VolleySingleton
import kashish.com.ui.Activities.DetailActivity
import kashish.com.utils.Constants
import kashish.com.utils.Helpers
import org.json.JSONArray
import org.json.JSONObject


/**
 * A simple [Fragment] subclass.
 */
class PopularMoviesFragment : Fragment(), OnMovieClickListener {

    private val TAG:String = "PopularMoviesFragment"
    private val GRID_COLUMNS_PORTRAIT = 1
    private val GRID_COLUMNS_LANDSCAPE = 2
    private lateinit var mMainView : View
    private lateinit var mRecyclerView : RecyclerView
    private lateinit var mSwipeRefreshLayout : SwipeRefreshLayout
    private lateinit var mGridLayoutManager : GridLayoutManager


    private var pageNumber:Int = 1
    private var doPagination:Boolean = true
    private var isScrolling:Boolean = false
    private  var currentItem:Int = -1
    private  var totalItem:Int = -1
    private  var scrolledOutItem:Int = -1
    private var isLoading: Boolean = false

    lateinit var mMovieAdapter: MovieAdapter
    lateinit var data:MutableList<Movie>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_popular_movies, container, false)

        initViews()
        initContentList()
        fetchData()
        initRecyclerView()
        setSwipeRefreshLayoutListener()
        setRecyclerViewScrollListener()

        return mMainView
    }

    private fun initViews(){
        mRecyclerView = mMainView.findViewById(R.id.fragment_popular_movies_recycler_view)
        mSwipeRefreshLayout = mMainView.findViewById(R.id.fragment_popular_movies_swipe_refresh)
    }
    private fun clearList() {
        val size = data.size
        data.clear()
        mMovieAdapter.notifyItemRangeRemoved(0, size)
    }
    private fun initRecyclerView() {
        configureRecyclerAdapter(resources.configuration.orientation)
        mMovieAdapter = MovieAdapter(data,this)
        mRecyclerView.setAdapter(mMovieAdapter)
        mRecyclerView.setHasFixedSize(true)
    }
    private fun setSwipeRefreshLayoutListener() {
        mSwipeRefreshLayout.setOnRefreshListener {
            pageNumber = 1
            doPagination = true
            clearList()
            addProgressBarInList()
            fetchData()
        }
    }
    private fun initContentList(){
        data = mutableListOf()
        addProgressBarInList()
    }
    private fun setRecyclerViewScrollListener() {
        //Fetching next page's data on reaching bottom
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

//                val reachedBottom = !recyclerView!!.canScrollVertically(1) && dy!=0
//                if (reachedBottom && doPagination && !isLoading) {
//                    pageNumber++
//                    isLoading = true
//                    delayByfewSeconds()
//                }

                currentItem = mGridLayoutManager.childCount
                totalItem = mGridLayoutManager.itemCount
                scrolledOutItem = mGridLayoutManager.findFirstVisibleItemPosition()

                if (isScrolling && doPagination && !isLoading && (currentItem+scrolledOutItem == totalItem)){
                    pageNumber++
                    isScrolling = false
                    isLoading = true
                    delayByfewSeconds()
                }


            }
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                    isScrolling = true
            }
        })
    }
    private fun fetchData(){

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET,
                Helpers.buildPopularMoviesUrl(pageNumber),null, Response.Listener { response ->

            val jsonArray: JSONArray = response.getJSONArray(Constants.RESULTS)

            if (jsonArray.length() == 0){
                //stop call to pagination in any case
                doPagination = false

                //show msg no posts
                if(pageNumber == 1)
                    Toast.makeText(getContext(),"Something went wrong", Toast.LENGTH_SHORT).show()
                data.removeAt(data.size - 1)
                mMovieAdapter.notifyItemRemoved(data.size-1)

            } else {

                //Data loaded, remove progress
                data.removeAt(data.size-1)


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
                    movie.contentType = Constants.CONTENT_MOVIE

                    data.add(movie)
                }

                addProgressBarInList()

                mMovieAdapter.notifyItemRangeInserted(data.size - jsonArray.length(), jsonArray.length())

                isLoading = false

                if (mSwipeRefreshLayout.isRefreshing())
                    mSwipeRefreshLayout.setRefreshing(false)
            }

        }, Response.ErrorListener { error ->
            Log.i(TAG,error.message+" is the error message")
        })

        VolleySingleton.getInstance(this.context!!).addToRequestQueue(jsonObjectRequest)
    }
    private fun delayByfewSeconds(){
        val handler = Handler()
        handler.postDelayed(Runnable {
            fetchData()
        }, 2000)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("asdfghjkl","OnDestroyView")
        clearList()
    }
    private fun addProgressBarInList() {
        val progressBarContent = Movie()
        progressBarContent.contentType = Constants.CONTENT_PROGRESS
        data.add(progressBarContent)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        configureRecyclerAdapter(newConfig!!.orientation)
    }

    private fun configureRecyclerAdapter(orientation: Int) {
        val isPortrait = orientation == Configuration.ORIENTATION_PORTRAIT
        mGridLayoutManager = GridLayoutManager(context, if (isPortrait) GRID_COLUMNS_PORTRAIT else GRID_COLUMNS_LANDSCAPE)
        mRecyclerView.setLayoutManager(mGridLayoutManager)
    }

    override fun onMovieClickListener(movie: Movie) {
        val detailIntent = Intent(context, DetailActivity::class.java)
        detailIntent.putExtra("movie",movie)
        context!!.startActivity(detailIntent)
    }

}// Required empty public constructor
