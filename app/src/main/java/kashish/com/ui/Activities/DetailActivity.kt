package kashish.com.ui.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kashish.com.R
import kashish.com.models.Movie

class DetailActivity : AppCompatActivity() {

    private val TAG: String = DetailActivity::class.java.simpleName
    private var movie: Movie = Movie()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)



    }

    private fun getMovie(){
        movie = intent.getParcelableExtra("movie")
    }
    private fun initViews(){

    }

    private fun getMovieDetails(id: Int){

    }

}
