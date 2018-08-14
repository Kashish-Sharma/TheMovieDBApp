package kashish.com.ui.Fragments


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
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
import android.widget.TextView
import android.widget.Toast
import kashish.com.Injection

import kashish.com.R
import kashish.com.adapters.MovieAdapter
import kashish.com.adapters.NowShowingAdapter
import kashish.com.database.AppDatabase
import kashish.com.database.AppExecutors
import kashish.com.database.Entities.TopRatedEntry
import kashish.com.database.Entities.UpcomingEntry
import kashish.com.interfaces.OnMovieClickListener
import kashish.com.models.Movie
import kashish.com.requestmodels.MovieRequest
import kashish.com.network.NetworkService
import kashish.com.ui.Activities.DetailActivity
import kashish.com.utils.Constants
import kashish.com.utils.Constants.Companion.CONTENT_PROGRESS
import kashish.com.utils.Constants.Companion.NOWSHOWING
import kashish.com.utils.Urls
import kashish.com.viewmodels.TopRatedViewModel
import kashish.com.viewmodels.UpcomingViewModel
import retrofit2.Call
import retrofit2.Callback


/**
 * A simple [Fragment] subclass.
 */
class TopRatedMoviesFragment : Fragment(), OnMovieClickListener {

    private val TAG:String = "TopRatedMoviesFragment"
    private val GRID_COLUMNS_PORTRAIT = 1
    private val GRID_COLUMNS_LANDSCAPE = 2
    private lateinit var mMainView : View
    private lateinit var mRecyclerView : RecyclerView
    private lateinit var mSwipeRefreshLayout : SwipeRefreshLayout
    private lateinit var mGridLayoutManager : GridLayoutManager
    private lateinit var emptyList: TextView

    private lateinit var viewModel: TopRatedViewModel
    private lateinit var mSharedPreferences: SharedPreferences
    private lateinit var networkService: NetworkService
    private lateinit var mDatabase: AppDatabase


    lateinit var mMovieAdapter: NowShowingAdapter
    lateinit var data:MutableList<Movie>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_top_rated_movies, container, false)

        initViews()
        initRecyclerView()
        getTopRatedData(true)
        setSwipeRefreshLayoutListener()
        setupScrollListener()

        return mMainView
    }

    private fun initViews(){
        mRecyclerView = mMainView.findViewById(R.id.fragment_top_rated_movies_recycler_view)
        mSwipeRefreshLayout = mMainView.findViewById(R.id.fragment_top_rated_movies_swipe_refresh)
        emptyList = mMainView.findViewById(R.id.emptyTopRatedList)

        data = mutableListOf()
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        networkService = NetworkService.instance
        mDatabase = AppDatabase.getInstance(context!!.applicationContext)
    }

    private fun initRecyclerView() {
        configureRecyclerAdapter(resources.configuration.orientation)
        viewModel = ViewModelProviders.of(this, Injection.provideTopRatedViewModelFactory(context!!))
                .get(TopRatedViewModel::class.java)

        mMovieAdapter = NowShowingAdapter(this,mSharedPreferences)
        mRecyclerView.adapter = mMovieAdapter


        viewModel.topRated.observe(this, Observer<List<TopRatedEntry>> {
            Log.i("asdfghjkjhgfdfghj", "list: ${it?.size}")
            showEmptyList(it?.size == 0)
            mMovieAdapter.submitList(convertEntryToMovieList(it!!))
        })
        viewModel.networkErrors.observe(this, Observer<String> {
            Toast.makeText(context, "\uD83D\uDE28 Wooops ${it}", Toast.LENGTH_LONG).show()
        })
    }


    private fun showEmptyList(show: Boolean) {
        if (show) {
            emptyList.visibility = View.VISIBLE
            mRecyclerView.visibility = View.GONE
        } else {
            emptyList.visibility = View.GONE
            mRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun getTopRatedData(doReload: Boolean){
        viewModel.getTopRated(doReload)
        mMovieAdapter.submitList(null)
        mSwipeRefreshLayout.isRefreshing = false
    }

    private fun setSwipeRefreshLayoutListener() {
        mSwipeRefreshLayout.setOnRefreshListener {
            AppExecutors.getInstance().diskIO().execute(Runnable {
                mDatabase.upcomingDao().deleteAll()
            })
            mRecyclerView.scrollToPosition(0)
            viewModel.getTopRated(true)
            mMovieAdapter.submitList(null)
            mSwipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setupScrollListener() {
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = mGridLayoutManager.itemCount
                val visibleItemCount = mGridLayoutManager.childCount
                val lastVisibleItem = mGridLayoutManager.findLastVisibleItemPosition()

                viewModel.listScrolled(visibleItemCount, lastVisibleItem, totalItemCount)
            }
        })
    }

    private fun convertEntryToMovieList(list: List<TopRatedEntry>): MutableList<Movie>{
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
            passMovie.tableName = NOWSHOWING
            movieList.add(passMovie)
        }
        return movieList
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
