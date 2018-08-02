package kashish.com.ui.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.ActionBar
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kashish.com.R
import kashish.com.models.Movie
import kashish.com.models.MovieDetail
import kashish.com.singleton.VolleySingleton
import kashish.com.utils.Constants
import kashish.com.utils.Constants.Companion.getGenre
import kashish.com.utils.DateUtils
import kashish.com.utils.Helpers.buildBackdropImageUrl
import kashish.com.utils.Helpers.buildImageUrl
import kashish.com.utils.Helpers.buildMovieDetailUrl
import kashish.com.utils.Helpers.setUpTransparentStatusBar
import org.json.JSONObject
import java.nio.file.Files.size




class DetailActivity : AppCompatActivity() {

    private val TAG: String = DetailActivity::class.java.simpleName
    private var movie: Movie = Movie()

    //Collapsing Toolbar
    private lateinit var mCollapsingToolbar: CollapsingToolbarLayout
    private lateinit var mActionBar: ActionBar
    private lateinit var mToolbar: Toolbar
    private lateinit var mAppBarLayout: AppBarLayout
    private lateinit var mBackdropImageView: ImageView

    private lateinit var mToolbarMovieTitle: TextView
    private lateinit var mToolbarMovieDate: TextView
    private lateinit var mToolbarMoviePoster: ImageView

    //Nested scroll view
    private lateinit var mNestedScrollView: NestedScrollView

    //Ratings
    private lateinit var mAdult: TextView
    private lateinit var mVoteAvg: TextView
    private lateinit var mVotes: TextView

    //Overview
    private lateinit var mDetailOverView: TextView
    private lateinit var mDetailGenre: TextView
    private var movieGenre: String = ""
    private lateinit var mDetailRatingBar: RatingBar
    private lateinit var mRunTimeTextView: TextView
    private lateinit var mBudgetTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.DetailTheme)
        setUpTransparentStatusBar(window)
        setContentView(R.layout.activity_detail)

        getMovie()
        getGenre()
        initToolBar()
        setupCollapsingToolbar()

        initViews()
        fetchMovieDetails()
        setRatingsData()
        setOverViewData()

    }

    private fun getMovie(){
        movie = intent.getParcelableExtra("movie")
    }
    private fun getGenre(){
        movieGenre = intent.getStringExtra("genre")
    }
    private fun initToolBar(){
        mCollapsingToolbar = findViewById(R.id.activity_detail_collapsing_layout)
        mToolbar = findViewById(R.id.activity_detail_toolbar)
        mAppBarLayout = findViewById(R.id.activity_detail_app_bar_layout)
        mBackdropImageView = findViewById(R.id.activity_detail_backdrop_image)

        mToolbarMovieTitle = findViewById(R.id.activity_detail_movie_title)
        mToolbarMovieDate = findViewById(R.id.activity_detail_movie_date)
        mToolbarMoviePoster = findViewById(R.id.activity_detail_poster_image)

        setSupportActionBar(mToolbar)
        mActionBar = supportActionBar!!
        mActionBar.setDisplayHomeAsUpEnabled(true)
    }
    private fun setupCollapsingToolbar(){
        Glide.with(this).load(buildBackdropImageUrl(movie.backdropPath!!))
                .transition(DrawableTransitionOptions.withCrossFade()).into(mBackdropImageView)
        Glide.with(this).load(buildImageUrl(movie.posterPath!!))
                .transition(DrawableTransitionOptions.withCrossFade()).into(mToolbarMoviePoster)
        mToolbarMovieTitle.setText(movie.title)
        mToolbarMovieDate.setText(DateUtils.getStringDate(movie.releaseDate!!))
    }
    private fun collapseAppBarOnScrollUp(){
        mAppBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (Math.abs(verticalOffset) == appBarLayout.totalScrollRange) {
                // Collapsed
            } else if (verticalOffset == 0) {
                // Expanded
            } else {
                // Somewhere in between
            }
        })
    }
    private fun initViews(){
        mAdult = findViewById(R.id.activity_detail_adult)
        mVoteAvg = findViewById(R.id.activity_detail_vote_average)
        mVotes = findViewById(R.id.activity_detail_vote_count)

        mDetailOverView = findViewById(R.id.activity_detail_overview)
        mDetailGenre = findViewById(R.id.activity_detail_genre)
        mDetailRatingBar = findViewById(R.id.activity_detail_rating_bar)
        mDetailRatingBar.numStars = 5

        mRunTimeTextView = findViewById(R.id.activity_detail_movie_run_time)
        mBudgetTextView = findViewById(R.id.activity_detail_movie_budget)
    }
    private fun setRatingsData(){
        if (movie.adult!!) mAdult.setText("adult: false")
        else mAdult.setText("adult: false")

        mVoteAvg.setText("rating: "+movie.voteAverage.toString()+"/10")

        if (movie.voteCount!! >= 1000) mVotes.setText("votes: "+("%.2f".format(movie.voteCount!!.toFloat().div(1000)))+"k")
        else mVotes.setText("votes: "+movie.voteCount.toString())

    }
    private fun setOverViewData(){
        mDetailOverView.setText(movie.overview)
        mDetailGenre.setText(movieGenre)
        mDetailRatingBar.rating = movie.voteAverage!!.div(2)
    }
    private fun setRuntimeAndBudget(runtime: Int, budget: Int){
        mRunTimeTextView.setText("runtime: "+runtime+"mins")
        mBudgetTextView.setText("budget: $"+(budget.div(1000))+"k")
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item!!.getItemId()
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun fetchMovieDetails(){

        val movieDetailUrl = buildMovieDetailUrl(movie.id.toString())

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET,
                movieDetailUrl,null, Response.Listener { response ->

            val jsonObject: JSONObject = response
            val movieDetail = MovieDetail()

            movieDetail.homePage = jsonObject.getString("homepage")
            movieDetail.imdbId = jsonObject.getString("imdb_id")
            movieDetail.budget = jsonObject.getInt("budget")
            movieDetail.revenue = jsonObject.getInt("revenue")
            movieDetail.runtime = jsonObject.getInt("runtime")
            movieDetail.releaseStatus = jsonObject.getString("status")
            movieDetail.tagLine = jsonObject.getString("tagline")

            setRuntimeAndBudget(movieDetail.runtime, movieDetail.budget)

        }, Response.ErrorListener { error ->
            Log.i(TAG,error.message)
        })

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }


}
