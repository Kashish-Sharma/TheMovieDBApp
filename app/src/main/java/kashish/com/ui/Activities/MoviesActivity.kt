package kashish.com.ui.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.design.widget.TabLayout
import android.support.v7.widget.Toolbar
import kashish.com.R
import kashish.com.adapters.MovieViewPagerAdapter
import kashish.com.ui.Fragments.DiscoverMoviesFragment
import kashish.com.ui.Fragments.TopRatedMoviesFragment
import kashish.com.ui.Fragments.UpcomingMoviesFragment
import kashish.com.utils.Helpers

class MoviesActivity : AppCompatActivity() {

    private lateinit var mViewPager : ViewPager
    private lateinit var mTabLayout : TabLayout
    private lateinit var mToolBar : Toolbar

    //Fragments
    internal lateinit var mUpcomingMoviesFragment : UpcomingMoviesFragment
    internal lateinit var mTopRatedMoviesFragment : TopRatedMoviesFragment
    internal lateinit var mDiscoverMoviesFragment : DiscoverMoviesFragment
    internal lateinit var mMovieViewPagerAdapter : MovieViewPagerAdapter

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

        mMovieViewPagerAdapter.addFragment(mUpcomingMoviesFragment, "Upcoming")
        mMovieViewPagerAdapter.addFragment(mTopRatedMoviesFragment, "Top Rated")
        mMovieViewPagerAdapter.addFragment(mDiscoverMoviesFragment, "Discover")
        mViewPager.adapter = mMovieViewPagerAdapter
    }
    private fun setupTabLayout(){
        mTabLayout.setupWithViewPager(mViewPager)
    }

}
