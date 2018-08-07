package kashish.com.viewholders

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import kashish.com.R
import kashish.com.interfaces.OnMovieClickListener
import kashish.com.models.Movie

/**
 * Created by Kashish on 07-08-2018.
 */
class MoreViewHolder(itemView: View?,
                      val context: Context,
                      val movieList: List<Movie>,
                      val listener: OnMovieClickListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    var moreTitle: TextView
    var moreSubtitle: TextView
    var morePoster: ImageView

    init {
        moreTitle = itemView!!.findViewById(R.id.item_more_name)
        moreSubtitle = itemView.findViewById(R.id.item_more_subtitle)
        morePoster = itemView.findViewById(R.id.item_more_image)

        itemView.setOnClickListener(this)

    }

    override fun onClick(p0: View?) {
        val position: Int = adapterPosition
        if (position != RecyclerView.NO_POSITION) {
            listener.onMovieClickListener(movieList.get(position))
        }
    }
}