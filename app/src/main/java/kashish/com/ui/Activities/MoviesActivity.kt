package kashish.com.ui.Activities

import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.view.ViewPager
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kashish.com.R
import kashish.com.adapters.MovieViewPagerAdapter
import kashish.com.ui.Fragments.*
import java.util.*

class MoviesActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var mViewPager : ViewPager
    private lateinit var mTabLayout : TabLayout
    private lateinit var mToolBar : Toolbar
    private lateinit var mSharedPreferences: SharedPreferences

    //Fragments
    private lateinit var mUpcomingMoviesFragment   : UpcomingMoviesFragment
    private lateinit var mTopRatedMoviesFragment   : TopRatedMoviesFragment
    private lateinit var mPopularMoviesFragment    : PopularMoviesFragment
    private lateinit var mNowShowingMoviesFragment : NowShowingMoviesFragment
    private lateinit var mMovieViewPagerAdapter    : MovieViewPagerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (mSharedPreferences.getBoolean(getString(R.string.pref_night_mode_key)
                ,resources.getBoolean(R.bool.pref_night_mode_default_value))) {
            setTheme(R.style.AppThemeDark)
        } else{
            setTheme(R.style.AppTheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movies)

        initViews()
        setupToolBar()
        setupTabLayout()
        setupViewPager()

    }

    override fun onBackPressed() {
        if (mViewPager.currentItem == 0) super.onBackPressed()
        else mViewPager.currentItem = mViewPager.currentItem - 1
    }
    private fun initViews(){
        mToolBar = findViewById(R.id.activity_movies_toolbar)
        mViewPager = findViewById(R.id.activity_movies_view_pager)
        mTabLayout = findViewById(R.id.activity_movies_tab_layout)

        mSharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }
    private fun setupToolBar(){
        mToolBar.title = "Movies"
        setSupportActionBar(mToolBar)
    }
    private fun setupViewPager() {
        mMovieViewPagerAdapter = MovieViewPagerAdapter(supportFragmentManager)
        mUpcomingMoviesFragment = UpcomingMoviesFragment()
        mTopRatedMoviesFragment = TopRatedMoviesFragment()
        mPopularMoviesFragment = PopularMoviesFragment()
        mNowShowingMoviesFragment = NowShowingMoviesFragment()

        mMovieViewPagerAdapter.addFragment(mNowShowingMoviesFragment,"Now Showing")
        mMovieViewPagerAdapter.addFragment(mUpcomingMoviesFragment, "Upcoming")
        mMovieViewPagerAdapter.addFragment(mPopularMoviesFragment, "Popular")
        mMovieViewPagerAdapter.addFragment(mTopRatedMoviesFragment, "Top Rated")
        mViewPager.adapter = mMovieViewPagerAdapter

        mViewPager.offscreenPageLimit = 4
    }
    private fun setupTabLayout(){
        mTabLayout.setupWithViewPager(mViewPager)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.action_favourite -> startActivity(Intent(this, FavouritesActivity::class.java))
            R.id.action_search -> startActivity(Intent(this, SearchActivity::class.java))

        }
        return super.onOptionsItemSelected(item)
    }

    private fun restartActivity(){
        this.recreate()
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, key: String?) {
        if(key.equals(getString(R.string.pref_night_mode_key))){
            restartActivity()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this)
    }

}
