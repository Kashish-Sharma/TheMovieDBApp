package kashish.com.ui.Fragments


import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
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

import kashish.com.R
import kashish.com.adapters.MovieAdapter
import kashish.com.interfaces.OnMovieClickListener
import kashish.com.models.Movie
import kashish.com.requestmodels.MovieRequest
import kashish.com.network.NetworkService
import kashish.com.ui.Activities.DetailActivity
import kashish.com.utils.Constants
import kashish.com.utils.Constants.Companion.CONTENT_MOVIE
import kashish.com.utils.Constants.Companion.CONTENT_PROGRESS
import kashish.com.utils.Urls.Companion.TMDB_API_KEY
import retrofit2.Call
import retrofit2.Callback


/**
 * A simple [Fragment] subclass.
 */
class NowShowingMoviesFragment : Fragment(), OnMovieClickListener {

    private val TAG:String = "NowShowinMoviesFragment"
    private val GRID_COLUMNS_PORTRAIT = 1
    private val GRID_COLUMNS_LANDSCAPE = 2
    private lateinit var mMainView : View
    private lateinit var mRecyclerView : RecyclerView
    private lateinit var mSwipeRefreshLayout : SwipeRefreshLayout
    private lateinit var mGridLayoutManager : GridLayoutManager

    private lateinit var mSharedPreferences: SharedPreferences
    private lateinit var networkService: NetworkService


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
        mMainView = inflater.inflate(R.layout.fragment_now_showing, container, false)

        initViews()
        initContentList()
        fetchData()
        initRecyclerView()
        setSwipeRefreshLayoutListener()
        setRecyclerViewScrollListener()

        return mMainView
    }

    private fun initViews(){
        mRecyclerView = mMainView.findViewById(R.id.fragment_now_showing_movies_recycler_view)
        mSwipeRefreshLayout = mMainView.findViewById(R.id.fragment_now_showing_movies_swipe_refresh)

        data = mutableListOf()
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        networkService = NetworkService.instance
    }
    private fun clearList() {
        val size = data.size
        data.clear()
        mMovieAdapter.notifyItemRangeRemoved(0, size)
    }
    private fun initRecyclerView() {
        configureRecyclerAdapter(resources.configuration.orientation)
        mMovieAdapter = MovieAdapter(data,this,mSharedPreferences)
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

                val reachedBottom = !recyclerView!!.canScrollVertically(1) && dy!=0
                if (reachedBottom && doPagination && !isLoading) {
                    addProgressBarInList()
                    mMovieAdapter.notifyItemInserted(data.size-1)
                    pageNumber++
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

    private fun delayByfewSeconds(){
        val handler = Handler()
        handler.postDelayed(Runnable {
            fetchData()
        }, 2000)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        clearList()
    }
    private fun addProgressBarInList() {
        val progressBarContent = Movie()
        progressBarContent.contentType = CONTENT_PROGRESS
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

    private fun fetchData(){
        val call: Call<MovieRequest> = networkService.tmdbApi.getNowShowingMovies(TMDB_API_KEY
                ,"en-US",pageNumber,"US|IN|UK","2|3")

        call.enqueue(object : Callback<MovieRequest>{
            override fun onResponse(call: Call<MovieRequest>?, response: retrofit2.Response<MovieRequest>?) {

                val movieRequest: MovieRequest = response!!.body()!!
                Log.i("jhasbfbiuf",movieRequest.page.toString()+ " are the total pages")

            if (movieRequest.results!!.isEmpty()){
                //stop call to pagination in any case
                doPagination = false
                //show msg no posts
                if(pageNumber == 1)
                    Toast.makeText(getContext(),"Something went wrong", Toast.LENGTH_SHORT).show()
                data.removeAt(data.size - 1)
                mMovieAdapter.notifyItemRemoved(data.size-1)
                mSwipeRefreshLayout.isRefreshing = false

            }
            else {

                //Data loaded, remove progress
                data.removeAt(data.size-1)
                mMovieAdapter.notifyItemRemoved(data.size-1)

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

                    movie.contentType = CONTENT_MOVIE
                    data.add(movie)

                }

                isLoading = false
                if (mSwipeRefreshLayout.isRefreshing())
                    mSwipeRefreshLayout.setRefreshing(false)
                mMovieAdapter.notifyItemRangeInserted(data.size - movieRequest.results!!.size, movieRequest.results!!.size)
            }


        }

            override fun onFailure(call: Call<MovieRequest>?, t: Throwable?) {
                Log.i(TAG,t!!.message+" is the error message")
            }

        })
    }

}// Required empty public constructor
