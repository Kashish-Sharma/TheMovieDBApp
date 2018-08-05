package kashish.com.ui.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.ActionBar
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
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
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
import kashish.com.adapters.VideoAdapter
import kashish.com.interfaces.OnReviewReadMoreClickListener
import kashish.com.models.*
import kashish.com.utils.Constants.Companion.CAST
import kashish.com.utils.Constants.Companion.CREW
import kashish.com.utils.Helpers.buildMovieCastUrl


class DetailActivity : AppCompatActivity(), OnReviewReadMoreClickListener {

    private val TAG: String = DetailActivity::class.java.simpleName
    private var movie: Movie = Movie()
    private lateinit var mReviewSnapHelper: SnapHelper
    private lateinit var mCastSnapHelper: SnapHelper
    private lateinit var mCrewSnapHelper: SnapHelper
    private lateinit var mTrailerSnapHelper: SnapHelper

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.DetailTheme)
        setContentView(R.layout.activity_detail)
        setUpTransparentStatusBar(window)

        getMovie()
        getGenre()
        initToolBar()
        setupCollapsingToolbar()

        initViews()
        initReviewRecyclerView()
        initCastRecyclerView()
        initCrewRecyclerView()
        initTrailerRecyclerView()
        fetchMovieDetails()
        //fetchMovieReviews()
        fetchMovieCast()
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
        mCastAdapter = CastCrewAdapter(castData)
        mCastRecyclerView.setAdapter(mCastAdapter)
        mCastSnapHelper.attachToRecyclerView(mCastRecyclerView)
    }
    private fun initCrewRecyclerView(){
        mLinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mCrewRecyclerView.setLayoutManager(mLinearLayoutManager)
        mCrewAdapter = CastCrewAdapter(crewData)
        mCrewRecyclerView.setAdapter(mCrewAdapter)
        mCrewSnapHelper.attachToRecyclerView(mCrewRecyclerView)
    }
    private fun initTrailerRecyclerView(){
        mLinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mTrailerRecyclerView.setLayoutManager(mLinearLayoutManager)
        mTrailerAdapter = VideoAdapter(trailerData)
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
        mDetailGenre.setText(movieGenre)
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
            val movieDetail = MovieDetail("", "", 0, 0, "", "", 0)

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


//            //Getting cast
//            val creditsObject: JSONObject = response.getJSONObject("credits")
//            val castArray: JSONArray = creditsObject.getJSONArray("cast")
//            if (castArray.length() == 0){
//                //stop call to pagination in any case
//                mCastProgressBar.visibility = View.GONE
//            }
//            for (i in 0 until castArray.length()) {
//                val jresponse: JSONObject = castArray.getJSONObject(i)
//                val cast = Cast()
//                cast.castId = jresponse.getInt("cast_id")
//                cast.character = jresponse.getString("character")
//                cast.creditId = jresponse.getString("credit_id")
//                cast.id = jresponse.getInt("id")
//                cast.name = jresponse.getString("name")
//                cast.order = jresponse.getInt("order")
//                cast.profilePath = jresponse.getString("profile_path")
//                if(cast.profilePath!=null)
//                    castData.add(cast)
//            }
//            mCastAdapter.notifyItemRangeInserted(castData.size - castArray.length(),castArray.length())
//            mCastProgressBar.visibility = View.GONE
//
//            //Getting crew
//            val crewArray: JSONArray = creditsObject.getJSONArray("crew")
//            if (crewArray.length() == 0){
//                //stop call to pagination in any case
//                mCrewProgressBar.visibility = View.GONE
//            }
//            for (i in 0 until crewArray.length()) {
//                val jCrewresponse: JSONObject = crewArray.getJSONObject(i)
//                val crew = Cast()
//                crew.character = jCrewresponse.getString("job")
//                //crew.department = jCrewresponse.getString("department")
//                crew.creditId = jCrewresponse.getString("credit_id")
//                crew.id = jCrewresponse.getInt("id")
//                crew.name = jCrewresponse.getString("name")
//                crew.profilePath = jCrewresponse.getString("profile_path")
//                if (crew.profilePath!=null)
//                    crewData.add(crew)
//            }
//            mCrewAdapter.notifyItemRangeInserted(crewData.size - crewArray.length()-1,crewArray.length()-1)
//            mCrewProgressBar.visibility = View.GONE

            //Getting reviews
            val reviewObject: JSONObject = response.getJSONObject("reviews")
            val reviewArray: JSONArray = reviewObject.getJSONArray(RESULTS)
            if (reviewArray.length() == 0){
                //stop call to pagination in any case
                mReviewProgressBar.visibility = View.GONE
            }

            for (i in 0 until reviewArray.length()) {
                val jresponse: JSONObject = reviewArray.getJSONObject(i)

                val review = MovieReview()

                review.author = jresponse.getString("author")
                review.content = jresponse.getString("content")
                review.contentType = CONTENT_REVIEW
                review.id = jresponse.getString("id")
                review.url = jresponse.getString("url")

                data.add(review)
            }

            mReviewReviewAdapter.notifyItemRangeInserted(data.size - reviewArray.length(),reviewArray.length())
            mReviewProgressBar.visibility = View.GONE

        }, Response.ErrorListener { error ->
            Log.i(TAG,error.message)
            mReviewProgressBar.visibility = View.GONE
//            mCastProgressBar.visibility = View.GONE
//            mCrewProgressBar.visibility = View.GONE
            mTrailerProgressBar.visibility = View.GONE
        })

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
            Log.i(TAG,error.message)
            mReviewProgressBar.visibility = View.GONE
        })

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
            Log.i(TAG,error.message)
            mCrewProgressBar.visibility = View.GONE
            mCastProgressBar.visibility = View.GONE
        })

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
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
    override fun onReviewReadMoreClickListener(review: MovieReview) {
        showReviewReadMoreBottomSheet(review)
    }

}
