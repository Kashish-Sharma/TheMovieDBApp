package kashish.com.ui.Fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest

import kashish.com.R
import kashish.com.adapters.MovieAdapter
import kashish.com.models.Movie
import kashish.com.singleton.VolleySingleton
import kashish.com.utils.Constants.Companion.ADULT
import kashish.com.utils.Constants.Companion.BACKDROP_PATH
import kashish.com.utils.Constants.Companion.CONTENT_MOVIE
import kashish.com.utils.Constants.Companion.CONTENT_PROGRESS
import kashish.com.utils.Constants.Companion.GENRE_IDS
import kashish.com.utils.Constants.Companion.ID
import kashish.com.utils.Constants.Companion.ORIGINAL_LANGUAGE
import kashish.com.utils.Constants.Companion.ORIGINAL_TITLE
import kashish.com.utils.Constants.Companion.OVERVIEW
import kashish.com.utils.Constants.Companion.POPULARITY
import kashish.com.utils.Constants.Companion.POSTER_PATH
import kashish.com.utils.Constants.Companion.RELEASE_DATE
import kashish.com.utils.Constants.Companion.RESULTS
import kashish.com.utils.Constants.Companion.TITLE
import kashish.com.utils.Constants.Companion.VIDEO
import kashish.com.utils.Constants.Companion.VOTE_AVERAGE
import kashish.com.utils.Constants.Companion.VOTE_COUNT
import kashish.com.utils.Urls
import org.json.JSONArray
import org.json.JSONObject



class UpcomingMoviesFragment : Fragment() {

    private val TAG:String = "UpcomingMoviesFragment"
    private lateinit var mMainView : View
    private lateinit var mRecyclerView : RecyclerView
    private lateinit var mLinearLayoutManager : LinearLayoutManager
    private lateinit var mSwipeRefreshLayout : SwipeRefreshLayout

    private var pageNumber:Int = 1
    private var doPagination:Boolean = true

    lateinit var mMovieAdapter:MovieAdapter

    var data:MutableList<Movie> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mMainView =  inflater.inflate(R.layout.fragment_upcoming_movies, container, false)


        initViews()
        fetchData()
        initRecyclerView()
        setSwipeRefreshLayoutListener()
        setRecyclerViewScrollListener()


        return mMainView
    }

    private fun fetchData(){
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET,
                Urls.UPCOMING_MOVIES.plus(pageNumber.toString()),null, Response.Listener { response ->


            if (!data.isEmpty()) data.removeAt(data.size-1)

            val jsonArray:JSONArray = response.getJSONArray(RESULTS)

            if (jsonArray.length() == 0){
                //stop call to pagination in any case
                doPagination = false;

                //show msg no posts
                if(pageNumber == 1)
                    Toast.makeText(getContext(),"Something went wrong",Toast.LENGTH_SHORT).show()
                else
                {
                    //to remove progress bar
                    data.removeAt(data.size-1);
                    mMovieAdapter.notifyItemRemoved(data.size-1);
                }
            }

            for (i in 0 until jsonArray.length()) {
                val jresponse:JSONObject = jsonArray.getJSONObject(i)

                val movie = Movie()

                movie.voteCount = jresponse.getInt(VOTE_COUNT)
                movie.id = jresponse.getInt(ID)
                movie.video = jresponse.getBoolean(VIDEO)
                movie.voteAverage = jresponse.getDouble(VOTE_AVERAGE).toFloat()
                movie.title = jresponse.getString(TITLE)
                movie.popularity = jresponse.getDouble(POPULARITY).toFloat()
                movie.posterPath = jresponse.getString(POSTER_PATH)
                movie.originalLanguage = jresponse.getString(ORIGINAL_LANGUAGE)
                movie.originalTitle = jresponse.getString(ORIGINAL_TITLE)

                val array:JSONArray = jresponse.getJSONArray(GENRE_IDS)
                val genreList:MutableList<Int> = mutableListOf()
                for (j in 0 until array.length()) {
                    genreList.add(array.getInt(j))
                }

                movie.genreIds = genreList
                movie.backdropPath = jresponse.getString(BACKDROP_PATH)
                movie.adult = jresponse.getBoolean(ADULT)
                movie.overview = jresponse.getString(OVERVIEW)
                movie.releaseDate = jresponse.getString(RELEASE_DATE)
                movie.contentType = CONTENT_MOVIE

                data.add(movie)
            }

            addProgressBarInList()
            mMovieAdapter.notifyItemRangeInserted(data.size - jsonArray.length(),jsonArray.length())

            if (mSwipeRefreshLayout.isRefreshing())
                mSwipeRefreshLayout.setRefreshing(false)

        }, Response.ErrorListener { error ->
            Log.i(TAG,error.message)
        })

        VolleySingleton.getInstance(this.context!!).addToRequestQueue(jsonObjectRequest)
    }

    private fun initViews(){
        mRecyclerView = mMainView.findViewById(R.id.fragment_upcoming_movies_recycler_view)
        mSwipeRefreshLayout = mMainView.findViewById(R.id.fragment_upcoming_movies_swipe_refresh)
    }
    private fun clearList() {
        val size = data.size
        data.clear()
        data.clear()
        mMovieAdapter.notifyItemRangeRemoved(0, size)
    }
    private fun initRecyclerView() {

        addProgressBarInList()

        mLinearLayoutManager = LinearLayoutManager(context)
        mRecyclerView.setLayoutManager(mLinearLayoutManager)

        mMovieAdapter = MovieAdapter(data)
        mRecyclerView.setAdapter(mMovieAdapter)
    }
    private fun setSwipeRefreshLayoutListener() {
        mSwipeRefreshLayout.setOnRefreshListener {
            pageNumber = 1
            doPagination = true
            clearList()
            fetchData()
        }
    }
    private fun setRecyclerViewScrollListener() {
        //Fetching next page's data on reaching bottom
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val reachedBottom = !recyclerView!!.canScrollVertically(1)
                if (reachedBottom && doPagination) {
                    pageNumber++
                    fetchData()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }
    private fun addProgressBarInList() {
        val progressBarContent = Movie()
        progressBarContent.contentType = CONTENT_PROGRESS
        data.add(progressBarContent)
    }
}

