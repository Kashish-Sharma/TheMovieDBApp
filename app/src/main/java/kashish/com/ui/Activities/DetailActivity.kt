package kashish.com.ui.Activities

import android.app.Activity
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.AppBarLayout
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.*
import android.support.v7.widget.Toolbar
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import kashish.com.R
import kashish.com.adapters.MovieReviewAdapter
import kashish.com.singleton.VolleySingleton
import kashish.com.utils.Constants.Companion.CONTENT_REVIEW
import kashish.com.utils.Constants.Companion.RESULTS
import kashish.com.utils.DateUtils
import kashish.com.utils.Helpers.buildBackdropImageUrl
import kashish.com.utils.Helpers.buildImageUrl
import kashish.com.utils.Helpers.buildMovieDetailUrl
import kashish.com.utils.Helpers.buildMovieReviewUrl
import kashish.com.utils.Helpers.setUpTransparentStatusBar
import org.json.JSONArray
import org.json.JSONObject
import kashish.com.adapters.CastCrewAdapter
import kashish.com.adapters.MovieAdapter
import kashish.com.adapters.VideoAdapter
import kashish.com.database.AppDatabase
import kashish.com.database.AppExecutors
import kashish.com.database.MovieEntry
import kashish.com.interfaces.OnMovieClickListener
import kashish.com.interfaces.OnReviewReadMoreClickListener
import kashish.com.interfaces.OnVideoClickListener
import kashish.com.models.*
import kashish.com.utils.Constants
import kashish.com.utils.Constants.Companion.CAST
import kashish.com.utils.Constants.Companion.CREW
import kashish.com.utils.Helpers
import kashish.com.utils.Helpers.buildImdbUrl
import kashish.com.utils.Helpers.buildMovieCastUrl
import kashish.com.utils.Helpers.buildRecommendedMoviesUrl
import kashish.com.utils.Helpers.buildWikiUrl
import kashish.com.viewmodels.FavouritesViewModel
import java.util.*


class DetailActivity : AppCompatActivity(), OnReviewReadMoreClickListener, OnVideoClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private val TAG: String = DetailActivity::class.java.simpleName
    private var movie: Movie = Movie()
    private lateinit var mReviewSnapHelper: SnapHelper
    private lateinit var mCastSnapHelper: SnapHelper
    private lateinit var mCrewSnapHelper: SnapHelper
    private lateinit var mTrailerSnapHelper: SnapHelper

    private lateinit var mSharedPreferences: SharedPreferences

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
    private lateinit var movieDetail: MovieDetail
    private lateinit var mAddToFavourite: CheckBox

    //Ratings
    private lateinit var mAdult: TextView
    private lateinit var mVoteAvg: TextView
    private lateinit var mVotes: TextView

    //Overview
    private lateinit var mDetailOverView: TextView
    private lateinit var mDetailGenre: TextView
    private lateinit var mDetailRatingBar: RatingBar
    private lateinit var mRunTimeTextView: TextView
    private lateinit var mBudgetTextView: TextView

    //Reviews
    lateinit var mReviewReviewAdapter: MovieReviewAdapter
    var data:MutableList<MovieReview> = mutableListOf()
    private lateinit var mReviewRecyclerView : RecyclerView
    private lateinit var mLinearLayoutManager : LinearLayoutManager
    private lateinit var mReviewProgressBar : ProgressBar
    private lateinit var mReviewReadMoreBottomSheet : BottomSheetDialog
    private lateinit var mReviewReadMoreAuthor : TextView
    private lateinit var mReviewReadMoreContent : TextView

    //Cast
    lateinit var mCastAdapter: CastCrewAdapter
    var castData: MutableList<Cast> = mutableListOf()
    private lateinit var mCastRecyclerView : RecyclerView
    private lateinit var mCastProgressBar : ProgressBar

    //Crew
    lateinit var mCrewAdapter: CastCrewAdapter
    var crewData: MutableList<Cast> = mutableListOf()
    private lateinit var mCrewRecyclerView : RecyclerView
    private lateinit var mCrewProgressBar : ProgressBar

    //Trailer
    lateinit var mTrailerAdapter: VideoAdapter
    var trailerData: MutableList<Video> = mutableListOf()
    private lateinit var mTrailerRecyclerView : RecyclerView
    private lateinit var mTrailerProgressBar : ProgressBar


    private lateinit var mSimilarMoviesBtn: TextView
    private lateinit var mWikipediaBtn : TextView
    private lateinit var mImdbBtn : TextView

    //Database
    private val EXTRA_MOVIE_ID: String = "extraMovieId"
    private val INSTANCE_MOVIE_ID: String = "instanceMovieId"
    companion object {
        private val DEFAULT_TASK_ID: Int = -1
    }
    private var mMovieId = DEFAULT_TASK_ID
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
        setContentView(R.layout.activity_detail)
        setUpTransparentStatusBar(window)

        getMovie()
        initToolBar()
        initViews()
        setupCollapsingToolbar()

        initReviewRecyclerView()
        initCastRecyclerView()
        initCrewRecyclerView()
        initTrailerRecyclerView()

        fetchMovieDetails()
        fetchMovieReviews()
        fetchMovieCast()
        setRatingsData()
        setOverViewData()
        setOnClickListenersOnWikiImdnb()
        setFavouriteOnClickListener()
    }

    private fun getMovie(){
        movie = intent.getParcelableExtra("movie")
    }
    private fun initToolBar(){
        mCollapsingToolbar = findViewById(R.id.activity_detail_collapsing_layout)
        mToolbar = findViewById(R.id.activity_detail_toolbar)
        mAppBarLayout = findViewById(R.id.activity_detail_app_bar_layout)
        mBackdropImageView = findViewById(R.id.activity_detail_backdrop_image)

        mToolbarMovieTitle = findViewById(R.id.activity_detail_movie_title)
        mToolbarMovieDate = findViewById(R.id.activity_detail_movie_date)
        mToolbarMoviePoster = findViewById(R.id.activity_detail_poster_image)
        mAddToFavourite = findViewById(R.id.activity_detail_add_to_favourite)

        setSupportActionBar(mToolbar)
        mActionBar = supportActionBar!!
        mActionBar.setDisplayHomeAsUpEnabled(true)
    }
    private fun setupCollapsingToolbar(){
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this)

        if (mSharedPreferences.getBoolean(getString(R.string.pref_cache_data_key),true)){
            Glide.with(this).load(buildBackdropImageUrl(movie.backdropPath!!))
                    .transition(DrawableTransitionOptions.withCrossFade()).into(mBackdropImageView)
            Glide.with(this).load(buildImageUrl(movie.posterPath!!))
                    .transition(DrawableTransitionOptions.withCrossFade()).into(mToolbarMoviePoster)
        } else{
            Glide.with(this).load(buildBackdropImageUrl(movie.backdropPath!!))
                    .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true))
                    .transition(DrawableTransitionOptions.withCrossFade()).into(mBackdropImageView)
            Glide.with(this).load(buildImageUrl(movie.posterPath!!))
                    .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true))
                    .transition(DrawableTransitionOptions.withCrossFade()).into(mToolbarMoviePoster)
        }


        mToolbarMovieTitle.setText(movie.title)
        mToolbarMovieDate.setText(DateUtils.getStringDate(movie.releaseDate!!))

        //Checking if already added to favourite
        AppExecutors.getInstance().diskIO().execute(Runnable {
            val isCheck = mDatabase.movieDao().checkIfFavourite(movie.id!!)
            runOnUiThread(Runnable {
                mAddToFavourite.isChecked = isCheck
            })
        })

    }

    private fun initViews(){
        mReviewSnapHelper = LinearSnapHelper()
        mCastSnapHelper = LinearSnapHelper()
        mCrewSnapHelper = LinearSnapHelper()
        mTrailerSnapHelper = LinearSnapHelper()

        mAdult = findViewById(R.id.activity_detail_adult)
        mVoteAvg = findViewById(R.id.activity_detail_vote_average)
        mVotes = findViewById(R.id.activity_detail_vote_count)

        mDetailOverView = findViewById(R.id.activity_detail_overview)
        mDetailGenre = findViewById(R.id.activity_detail_genre)
        mDetailRatingBar = findViewById(R.id.activity_detail_rating_bar)
        mDetailRatingBar.numStars = 5

        mRunTimeTextView = findViewById(R.id.activity_detail_movie_run_time)
        mBudgetTextView = findViewById(R.id.activity_detail_movie_budget)

        mReviewRecyclerView = findViewById(R.id.activity_detail_review_recycler_view)
        mReviewProgressBar = findViewById(R.id.activity_detail_review_progress_bar)

        mCastRecyclerView = findViewById(R.id.activity_detail_cast_recycler_view)
        mCastProgressBar = findViewById(R.id.activity_detail_cast_progress_bar)

        mCrewRecyclerView = findViewById(R.id.activity_detail_crew_recycler_view)
        mCrewProgressBar = findViewById(R.id.activity_detail_crew_progress_bar)

        mTrailerRecyclerView = findViewById(R.id.activity_detail_trailer_recycler_view)
        mTrailerProgressBar = findViewById(R.id.activity_detail_trailer_progress_bar)

        mSimilarMoviesBtn = findViewById(R.id.activity_similar_movie_text)
        mSimilarMoviesBtn.setOnClickListener(View.OnClickListener {
            val similarIntent = Intent(this, SimilarMoviesActivity::class.java)
            similarIntent.putExtra("movie",movie)
            startActivity(similarIntent)
        })

        mWikipediaBtn = findViewById(R.id.activity_detail_wikipedia_btn)
        mImdbBtn = findViewById(R.id.activity_detail_imdb_btn)

        mDatabase = AppDatabase.getInstance(applicationContext)

    }
    private fun initReviewRecyclerView(){
        mLinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mReviewRecyclerView.setLayoutManager(mLinearLayoutManager)
        mReviewReviewAdapter = MovieReviewAdapter(data,this)
        mReviewRecyclerView.setAdapter(mReviewReviewAdapter)
        mReviewSnapHelper.attachToRecyclerView(mReviewRecyclerView)
    }
    private fun initCastRecyclerView(){
        mLinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mCastRecyclerView.setLayoutManager(mLinearLayoutManager)
        mCastAdapter = CastCrewAdapter(castData,mSharedPreferences)
        mCastRecyclerView.setAdapter(mCastAdapter)
        mCastSnapHelper.attachToRecyclerView(mCastRecyclerView)
    }
    private fun initCrewRecyclerView(){
        mLinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mCrewRecyclerView.setLayoutManager(mLinearLayoutManager)
        mCrewAdapter = CastCrewAdapter(crewData,mSharedPreferences)
        mCrewRecyclerView.setAdapter(mCrewAdapter)
        mCrewSnapHelper.attachToRecyclerView(mCrewRecyclerView)
    }
    private fun initTrailerRecyclerView(){
        mLinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mTrailerRecyclerView.setLayoutManager(mLinearLayoutManager)
        mTrailerAdapter = VideoAdapter(trailerData,this,mSharedPreferences)
        mTrailerRecyclerView.setAdapter(mTrailerAdapter)
        mTrailerSnapHelper.attachToRecyclerView(mTrailerRecyclerView)
    }
    private fun setRatingsData(){
        if (movie.adult!!) mAdult.setText("adult: true")
        else mAdult.setText("adult: false")

        mVoteAvg.setText("rating: "+movie.voteAverage.toString()+"/10")

        if (movie.voteCount!! >= 1000) mVotes.setText("votes: "+("%.2f".format(movie.voteCount!!.toFloat().div(1000)))+"k")
        else mVotes.setText("votes: "+movie.voteCount.toString())

    }
    private fun setOverViewData(){
        mDetailOverView.setText(movie.overview)
        mDetailGenre.setText(movie.genreString)
        mDetailRatingBar.rating = movie.voteAverage!!.div(2)
    }
    private fun setRuntimeAndBudget(runtime: Int, budget: Int){

        if (runtime == 0) mRunTimeTextView.setText("runtime: unavailable")
        else mRunTimeTextView.setText("runtime: "+runtime+"mins")

        if (budget == 0) mBudgetTextView.setText("budget: unavailable")
        else mBudgetTextView.setText("budget: $"+(budget.div(1000))+"k")
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
            movieDetail = MovieDetail("", "", 0, 0, "", "", 0)

            try {
                movieDetail.homePage = jsonObject.getString("homepage")
                movieDetail.imdbId = jsonObject.getString("imdb_id")
                movieDetail.budget = jsonObject.getInt("budget")
                movieDetail.revenue = jsonObject.getInt("revenue")
                movieDetail.runtime =  jsonObject.getInt("runtime")
                movieDetail.releaseStatus = jsonObject.getString("status")
                movieDetail.tagLine = jsonObject.getString("tagline")
            } catch (e:Exception){
                Log.i(TAG,e.message)
            }

            setRuntimeAndBudget(movieDetail.runtime, movieDetail.budget)

            val videosObject: JSONObject = jsonObject.getJSONObject("videos")
            val videosArray: JSONArray = videosObject.getJSONArray("results")
            if (videosArray.length() == 0){
                //stop call to pagination in any case
                mTrailerProgressBar.visibility = View.GONE
            }
            for (i in 0 until videosArray.length()) {
                val jresponse: JSONObject = videosArray.getJSONObject(i)
                val trailer = Video()
                trailer.key = jresponse.getString("key")
                trailer.id = jresponse.getString("id")
                trailer.iso_3166 = jresponse.getString("iso_3166_1")
                trailer.iso_639 = jresponse.getString("iso_639_1")
                trailer.name = jresponse.getString("name")
                trailer.site = jresponse.getString("site")
                trailer.size = jresponse.getInt("size")
                trailer.type = jresponse.getString("type")
                trailerData.add(trailer)
            }
            mTrailerAdapter.notifyItemRangeInserted(trailerData.size - videosArray.length(),videosArray.length())
            mTrailerProgressBar.visibility = View.GONE
            

        }, Response.ErrorListener { error ->
            Log.i(TAG,error.message+" is the volley error")
            mReviewProgressBar.visibility = View.GONE
            mTrailerProgressBar.visibility = View.GONE
        })
        jsonObjectRequest.setShouldCache(mSharedPreferences.getBoolean(getString(R.string.pref_cache_data_key),true))
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }
    private fun fetchMovieReviews(){
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET,
                buildMovieReviewUrl(movie.id.toString(), 1),null, Response.Listener { response ->

            val jsonArray: JSONArray = response.getJSONArray(RESULTS)
            if (jsonArray.length() == 0){
                //stop call to pagination in any case
                mReviewProgressBar.visibility = View.GONE
            }

            for (i in 0 until jsonArray.length()) {
                val jresponse: JSONObject = jsonArray.getJSONObject(i)

                val review = MovieReview()

                review.author = jresponse.getString("author")
                review.content = jresponse.getString("content")
                review.contentType = CONTENT_REVIEW
                review.id = jresponse.getString("id")
                review.url = jresponse.getString("url")

                data.add(review)
            }

            mReviewReviewAdapter.notifyItemRangeInserted(data.size - jsonArray.length(),jsonArray.length())
            mReviewProgressBar.visibility = View.GONE

        }, Response.ErrorListener { error ->
            Log.i(TAG,error.message+" is the volley error")
            mReviewProgressBar.visibility = View.GONE
        })
        jsonObjectRequest.setShouldCache(mSharedPreferences.getBoolean(getString(R.string.pref_cache_data_key),true))
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }
    private fun fetchMovieCast(){
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET,
                buildMovieCastUrl(movie.id.toString()),null, Response.Listener { response ->

            //Getting cast
            val jsonArray: JSONArray = response.getJSONArray(CAST)

            if (jsonArray.length() == 0){
                //stop call to pagination in any case
                mCastProgressBar.visibility = View.GONE
            }

            for (i in 0 until jsonArray.length()) {
                val jresponse: JSONObject = jsonArray.getJSONObject(i)

                val cast = Cast()

                cast.castId = jresponse.getInt("cast_id")
                cast.character = jresponse.getString("character")
                cast.creditId = jresponse.getString("credit_id")
                cast.id = jresponse.getInt("id")
                cast.name = jresponse.getString("name")
                cast.order = jresponse.getInt("order")
                cast.profilePath = jresponse.getString("profile_path")


                if(cast.profilePath!=null)
                    castData.add(cast)

            }

            mCastAdapter.notifyItemRangeInserted(castData.size - jsonArray.length(),jsonArray.length())
            mCastProgressBar.visibility = View.GONE


            //Getting crew
            val jsonCrewArray: JSONArray = response.getJSONArray(CREW)
            if (jsonArray.length() == 0){
                //stop call to pagination in any case
                mCrewProgressBar.visibility = View.GONE
            }
            for (i in 0 until jsonCrewArray.length()) {
                val jCrewresponse: JSONObject = jsonCrewArray.getJSONObject(i)
                val crew = Cast()
                    crew.character = jCrewresponse.getString("job")
                    //crew.department = jCrewresponse.getString("department")
                    crew.creditId = jCrewresponse.getString("credit_id")
                    crew.id = jCrewresponse.getInt("id")
                    crew.name = jCrewresponse.getString("name")
                    crew.profilePath = jCrewresponse.getString("profile_path")
                if (crew.profilePath!=null)
                    crewData.add(crew)
            }
            mCrewAdapter.notifyItemRangeInserted(crewData.size - jsonArray.length()-1,jsonArray.length()-1)
            mCrewProgressBar.visibility = View.GONE

        }, Response.ErrorListener { error ->
            Log.i(TAG,error.message+" is the volley error")
            mCrewProgressBar.visibility = View.GONE
            mCastProgressBar.visibility = View.GONE
        })

        jsonObjectRequest.setShouldCache(mSharedPreferences.getBoolean(getString(R.string.pref_cache_data_key),true))
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }
    private fun setOnClickListenersOnWikiImdnb(){
        mWikipediaBtn.setOnClickListener(View.OnClickListener {

            val dateArray = movie.title!!.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            var processedTitle:String = ""
            for (i in 0 until dateArray.size){
                if (i == dateArray.size-1){
                    processedTitle+=dateArray[i].capitalize()
                } else{
                    processedTitle+=dateArray[i].capitalize()+"_"
                }
            }

            val wikiIntent: Intent = Intent(Intent.ACTION_VIEW,Uri.parse(buildWikiUrl(processedTitle)))

            val title = "Select a browser"
            // Create intent to show the chooser dialog
            val chooser = Intent.createChooser(wikiIntent, title)

            // Verify the original intent will resolve to at least one activity
            if (wikiIntent.resolveActivity(packageManager) != null) {
                startActivity(chooser)
            }

        })
        mImdbBtn.setOnClickListener(View.OnClickListener {
                val imdbIntent: Intent = Intent(Intent.ACTION_VIEW,Uri.parse(buildImdbUrl(movieDetail.imdbId)))
                val title = "Select a browser"
                // Create intent to show the chooser dialog
                val chooser = Intent.createChooser(imdbIntent, title)

                // Verify the original intent will resolve to at least one activity
                if (imdbIntent.resolveActivity(packageManager) != null) {
                    startActivity(chooser)
                }
            })

    }
    //Showing bottom sheet onClick review read more
    private fun showReviewReadMoreBottomSheet(review: MovieReview){
        val view = layoutInflater.inflate(R.layout.review_read_more_bottom_sheet_layout, null)
        mReviewReadMoreBottomSheet = BottomSheetDialog(this)
        mReviewReadMoreBottomSheet.setContentView(view)

        mReviewReadMoreAuthor = mReviewReadMoreBottomSheet.findViewById(R.id.review_read_more_author)!!
        mReviewReadMoreContent = mReviewReadMoreBottomSheet.findViewById(R.id.review_read_more_content)!!
        mReviewReadMoreContent.movementMethod = ScrollingMovementMethod()

        mReviewReadMoreAuthor.setText(review.author)
        mReviewReadMoreContent.setText(review.content)

        mReviewReadMoreBottomSheet.setCancelable(false)
        mReviewReadMoreBottomSheet.setCanceledOnTouchOutside(true)
        mReviewReadMoreBottomSheet.show()
    }
    private fun setFavouriteOnClickListener(){
        mAddToFavourite.setOnClickListener(View.OnClickListener {
            val movieEntry = MovieEntry()
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
            movieEntry.contentType = movie.contentType
            movieEntry.totalPages = movie.totalPages
            movieEntry.genreString = movie.genreString
            movieEntry.timeAdded = Date()

            if (mAddToFavourite.isChecked){
                AppExecutors.getInstance().diskIO().execute(Runnable {
                    kotlin.run {
                        mDatabase.movieDao().insertFavourite(movieEntry)
                    }
                })
                Toast.makeText(this,"Added", Toast.LENGTH_SHORT).show()
            } else{
                AppExecutors.getInstance().diskIO().execute(Runnable {
                    mDatabase.movieDao().deleteFavourite(movieEntry)
                })
            }
        })
    }
    private fun restartActivity(){
        this.recreate()
    }

    override fun onReviewReadMoreClickListener(review: MovieReview) {
        showReviewReadMoreBottomSheet(review)
    }
    override fun onVideoClickListener(video: Video) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(Helpers.buildYoutubeURL(video.key!!))
        startActivity(Intent.createChooser(intent, "View Trailer:"))
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
