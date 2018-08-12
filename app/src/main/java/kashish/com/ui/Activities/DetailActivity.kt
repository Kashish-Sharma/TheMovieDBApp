package kashish.com.ui.Activities

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
import android.support.v7.widget.*
import android.support.v7.widget.Toolbar
import android.text.method.LinkMovementMethod
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import kashish.com.R
import kashish.com.adapters.MovieReviewAdapter
import kashish.com.utils.DateUtils
import kashish.com.utils.Helpers.buildBackdropImageUrl
import kashish.com.utils.Helpers.buildImageUrl
import kashish.com.utils.Helpers.setUpTransparentStatusBar
import kashish.com.adapters.CastAdapter
import kashish.com.adapters.CrewAdapter
import kashish.com.adapters.VideoAdapter
import kashish.com.database.AppDatabase
import kashish.com.database.AppExecutors
import kashish.com.database.MovieEntry
import kashish.com.interfaces.OnReviewReadMoreClickListener
import kashish.com.interfaces.OnVideoClickListener
import kashish.com.models.*
import kashish.com.requestmodels.MovieCreditRequest
import kashish.com.requestmodels.MovieReviewsRequest
import kashish.com.requestmodels.MovieVideosRequest
import kashish.com.singleton.NetworkService
import kashish.com.utils.Helpers
import kashish.com.utils.Helpers.buildImdbUrl
import kashish.com.utils.Helpers.buildWikiUrl
import kashish.com.utils.Urls
import retrofit2.Call
import retrofit2.Callback
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
    private var movieDetail: MovieDetail = MovieDetail()
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
    lateinit var mCastAdapter: CastAdapter
    var castData: MutableList<Cast> = mutableListOf()
    private lateinit var mCastRecyclerView : RecyclerView
    private lateinit var mCastProgressBar : ProgressBar

    //Crew
    lateinit var mCrewAdapter: CrewAdapter
    var crewData: MutableList<Crew> = mutableListOf()
    private lateinit var mCrewRecyclerView : RecyclerView
    private lateinit var mCrewProgressBar : ProgressBar

    //Trailer
    lateinit var mTrailerAdapter: VideoAdapter
    var trailerData: MutableList<MovieVideo> = mutableListOf()
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
    private lateinit var networkService: NetworkService

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
        networkService = NetworkService.instance

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
        mCastAdapter = CastAdapter(castData,mSharedPreferences)
        mCastRecyclerView.setAdapter(mCastAdapter)
        mCastSnapHelper.attachToRecyclerView(mCastRecyclerView)
    }
    private fun initCrewRecyclerView(){
        mLinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mCrewRecyclerView.setLayoutManager(mLinearLayoutManager)
        mCrewAdapter = CrewAdapter(crewData,mSharedPreferences)
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
        val call: Call<MovieDetail> = networkService.tmdbApi.getDetailMovie(movie.id.toString(),
                Urls.TMDB_API_KEY,"videos")

        call.enqueue(object : Callback<MovieDetail>{
            override fun onFailure(call: Call<MovieDetail>?, t: Throwable?) {
                Log.i(TAG,t!!.message+" is the error")
                mTrailerProgressBar.visibility = View.GONE
            }

            override fun onResponse(call: Call<MovieDetail>?, response: retrofit2.Response<MovieDetail>?) {
                movieDetail = response!!.body()!!

                setRuntimeAndBudget(movieDetail.runtime, movieDetail.budget)

                val videoResult: MovieVideosRequest = movieDetail.videosResult!!

                if (videoResult.videos!!.isEmpty()){
                    mTrailerProgressBar.visibility = View.GONE
                } else{
                    for (i in 0 until videoResult.videos!!.size) {
                        var trailer: MovieVideo
                        trailer = videoResult.videos!!.get(i)
                        trailerData.add(trailer)
                    }

                    mTrailerAdapter.notifyItemRangeInserted(trailerData.size - videoResult.videos!!.size,videoResult.videos!!.size)
                    mTrailerProgressBar.visibility = View.GONE
                }

            }
        })


    }
    private fun fetchMovieReviews(){
        val call: Call<MovieReviewsRequest> = networkService.tmdbApi
                .getMovieReviews(movie.id!!.toLong(), Urls.TMDB_API_KEY)
        call.enqueue(object : Callback<MovieReviewsRequest> {
            override fun onFailure(call: Call<MovieReviewsRequest>?, t: Throwable?) {
            Log.i(TAG,t!!.message+" is the error")
            mReviewProgressBar.visibility = View.GONE
            }

            override fun onResponse(call: Call<MovieReviewsRequest>?, response: retrofit2.Response<MovieReviewsRequest>?) {

                val movieReviews: MovieReviewsRequest = response!!.body()!!

                if (movieReviews.reviews!!.isEmpty()){
                    mReviewProgressBar.visibility = View.GONE
                } else{
                    for (i in 0 until movieReviews.reviews!!.size) {
                        val review: MovieReview = movieReviews.reviews!!.get(i)
                        data.add(review)
                    }
                    mReviewReviewAdapter.notifyItemRangeInserted(data.size - movieReviews.reviews!!.size,movieReviews.reviews!!.size)
                    mReviewProgressBar.visibility = View.GONE
                }

            }

        })
    }
    private fun fetchMovieCast(){
        val call: Call<MovieCreditRequest> = networkService.tmdbApi
                .getMovieCredits(movie.id!!.toLong(), Urls.TMDB_API_KEY)
        call.enqueue(object: Callback<MovieCreditRequest>{
            override fun onFailure(call: Call<MovieCreditRequest>?, t: Throwable?) {
            Log.i(TAG,t!!.message+" is the error")
            mCrewProgressBar.visibility = View.GONE
            mCastProgressBar.visibility = View.GONE            }

            override fun onResponse(call: Call<MovieCreditRequest>?, response: retrofit2.Response<MovieCreditRequest>?) {
                val movieCreditRequest: MovieCreditRequest = response!!.body()!!

                val crewResults: List<Crew> = movieCreditRequest.crewResult!!
                val castResults: List<Cast> = movieCreditRequest.castResult!!

                if (crewResults.isEmpty()) mCrewProgressBar.visibility = View.GONE
                else{
                    for (i in 0 until crewResults.size) {
                        val crew: Crew = crewResults[i]
                        if (crew.profilePath==null) crew.profilePath = "asdfghj"
                        crewData.add(crew)
                    }
                    mCrewAdapter.notifyItemRangeInserted(crewData.size - crewResults.size,crewResults.size)
                    mCrewProgressBar.visibility = View.GONE
                }


                if (castResults.isEmpty()) mCastProgressBar.visibility = View.GONE
                else{
                    for (i in 0 until castResults.size) {
                        val cast: Cast = castResults[i]
                        if (cast.profilePath==null) cast.profilePath = "asdfghj"
                        castData.add(cast)
                    }
                    mCastAdapter.notifyItemRangeInserted(castData.size - castResults.size,castResults.size)
                    mCastProgressBar.visibility = View.GONE
                }

            }

        })
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
        mReviewReadMoreContent.setMovementMethod(LinkMovementMethod.getInstance());

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
    override fun onVideoClickListener(movieVideo: MovieVideo) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(Helpers.buildYoutubeURL(movieVideo.key!!))
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
