package kashish.com.viewholders

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import kashish.com.R
import kashish.com.models.Movie
import kashish.com.ui.Activities.DetailActivity
import kashish.com.utils.Constants

/**
 * Created by Kashish on 04-08-2018.
 */
class DiscoverViewHolder(itemView: View?, context: Context, movieList: List<Movie>) : RecyclerView.ViewHolder(itemView)  {
    var movieTitle: TextView
    var movieRating: RatingBar
    var movieType: TextView
    var movieReleaseDate: TextView
    var moviePoster: ImageView
    var movieDetails: LinearLayout

    init{
        movieTitle = itemView!!.findViewById(R.id.discover_single_item_movie_title)
        movieRating = itemView.findViewById(R.id.discover_single_item_movie_rating)
        movieType = itemView.findViewById(R.id.discover_single_item_movie_type)
        movieReleaseDate = itemView.findViewById(R.id.discover_single_item_movie_release_date)
        moviePoster = itemView.findViewById(R.id.single_item_discover_movie_image)
        movieDetails = itemView.findViewById(R.id.single_item_discover_movie_linear_layout)

        itemView.setOnClickListener(View.OnClickListener {

            val movie: Movie = movieList.get(adapterPosition)
            var movieType = ""
            for (i in movie.genreIds!!) {
                if (i == movie.genreIds!!.last()) movieType += Constants.getGenre(i)
                else movieType += Constants.getGenre(i) + ", "
            }

            val detailIntent = Intent(context, DetailActivity::class.java)
            detailIntent.putExtra("movie",movie)
            detailIntent.putExtra("genre",movieType)
            context.startActivity(detailIntent)
        })

    }
}