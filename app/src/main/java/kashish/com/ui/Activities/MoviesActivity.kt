package kashish.com.ui.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.design.widget.TabLayout
import android.support.v7.widget.Toolbar
import kashish.com.R
import kashish.com.adapters.MovieViewPagerAdapter
import kashish.com.ui.Fragments.*

class MoviesActivity : AppCompatActivity() {

    private lateinit var mViewPager : ViewPager
    private lateinit var mTabLayout : TabLayout
    private lateinit var mToolBar : Toolbar

    //Fragments
    internal lateinit var mUpcomingMoviesFragment   : UpcomingMoviesFragment
    internal lateinit var mTopRatedMoviesFragment   : TopRatedMoviesFragment
    internal lateinit var mDiscoverMoviesFragment   : DiscoverMoviesFragment
    internal lateinit var mPopularMoviesFragment    : PopularMoviesFragment
    internal lateinit var mNowShowingMoviesFragment : NowShowingMoviesFragment
    internal lateinit var mMovieViewPagerAdapter    : MovieViewPagerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movies)

        initViews()
        setupToolBar()
        setupTabLayout()
        setupViewPager()
    }

    override fun onBackPressed() {
        if (mViewPager.currentItem == 0) super.onBackPressed()
        else mViewPager.setCurrentItem(mViewPager.currentItem-1)
    }
    private fun initViews(){
        mToolBar = findViewById(R.id.activity_movies_toolbar)
        mViewPager = findViewById(R.id.activity_movies_view_pager)
        mTabLayout = findViewById(R.id.activity_movies_tab_layout)
    }
    private fun setupToolBar(){
        mToolBar.title = "Movies"
        setSupportActionBar(mToolBar)
    }
    private fun setupViewPager() {
        mMovieViewPagerAdapter = MovieViewPagerAdapter(getSupportFragmentManager())
        mUpcomingMoviesFragment = UpcomingMoviesFragment()
        mTopRatedMoviesFragment = TopRatedMoviesFragment()
        mDiscoverMoviesFragment = DiscoverMoviesFragment()
        mPopularMoviesFragment = PopularMoviesFragment()
        mNowShowingMoviesFragment = NowShowingMoviesFragment()

        mMovieViewPagerAdapter.addFragment(mNowShowingMoviesFragment,"Now Showing")
        mMovieViewPagerAdapter.addFragment(mUpcomingMoviesFragment, "Upcoming")
        mMovieViewPagerAdapter.addFragment(mPopularMoviesFragment, "Popular")
        mMovieViewPagerAdapter.addFragment(mTopRatedMoviesFragment, "Top Rated")
        mMovieViewPagerAdapter.addFragment(mDiscoverMoviesFragment, "Discover")
        mViewPager.adapter = mMovieViewPagerAdapter

        mViewPager.offscreenPageLimit = 5
    }
    private fun setupTabLayout(){
        mTabLayout.setupWithViewPager(mViewPager)
    }

}
