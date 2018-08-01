package kashish.com.ui.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.ActionBar
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import kashish.com.R
import kashish.com.models.Movie
import kashish.com.utils.DateUtils
import kashish.com.utils.Helpers.buildBackdropImageUrl
import kashish.com.utils.Helpers.buildImageUrl
import kashish.com.utils.Helpers.setUpTransparentStatusBar


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
    private lateinit var mAdult: TextView
    private lateinit var mVoteAvg: TextView
    private lateinit var mVotes: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.DetailTheme)
        setUpTransparentStatusBar(window)
        setContentView(R.layout.activity_detail)

        getMovie()
        initToolBar()
        setupCollapsingToolbar()

        initViews()
        setRatingsData()

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

        setSupportActionBar(mToolbar)
        mActionBar = supportActionBar!!
        mActionBar.setDisplayHomeAsUpEnabled(true)
    }
    private fun setupCollapsingToolbar(){
        Glide.with(this).load(buildBackdropImageUrl(movie.backdropPath!!)).into(mBackdropImageView)
        Glide.with(this).load(buildImageUrl(movie.posterPath!!)).into(mToolbarMoviePoster)
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
    }

    private fun setRatingsData(){
        if (movie.adult!!) mAdult.setText("adult: false")
        else mAdult.setText("adult: false")

        mVoteAvg.setText("rating: "+movie.voteAverage.toString()+"/10")



        if (movie.voteCount!! >= 1000) mVotes.setText("votes: "+("%.2f".format(movie.voteCount!!.toFloat().div(1000))).toString()+"k")
        else mVotes.setText("votes: "+movie.voteCount.toString())

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item!!.getItemId()
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}
