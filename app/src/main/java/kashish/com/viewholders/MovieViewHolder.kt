package kashish.com.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import kashish.com.R

/**
 * Created by Kashish on 30-07-2018.
 */
class MovieViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
    var movieTitle: TextView
    var movieRating: TextView
    var movieType: TextView
    var moviePopularity: TextView
    var movieReleaseDate: TextView
    var moviePoster: ImageView
    var movieDetails: LinearLayout

    init{
        movieTitle = itemView!!.findViewById(R.id.single_item_movie_title)
        movieRating = itemView.findViewById(R.id.single_item_movie_rating)
        moviePopularity = itemView.findViewById(R.id.single_item_movie_popularity)
        movieType = itemView.findViewById(R.id.single_item_movie_type)
        movieReleaseDate = itemView.findViewById(R.id.single_item_movie_release_date)
        moviePoster = itemView.findViewById(R.id.single_item_movie_image)
        movieDetails = itemView.findViewById(R.id.single_item_movie_details)
    }

}