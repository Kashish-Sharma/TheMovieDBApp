package kashish.com.viewholders

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.TextView
import kashish.com.R
import kashish.com.interfaces.OnReviewReadMoreClickListener
import kashish.com.models.MovieReview
import kotlinx.android.synthetic.main.review_single_item.view.*

/**
 * Created by Kashish on 02-08-2018.
 */
class ReviewViewHolder(itemView: View?, context: Context,
                       val listener: OnReviewReadMoreClickListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    var mReviewAuthor: TextView
    var mReviewContent: TextView
    var mReadMoreButton: TextView
    private var mReview: MovieReview? = null

    init{
        mReviewAuthor = itemView!!.findViewById(R.id.review_author_name)
        mReviewContent = itemView.findViewById(R.id.review_content)
        mReadMoreButton = itemView.findViewById(R.id.review_read_more)
        itemView.setOnClickListener(this)
    }

    fun bindReviewData(movie: MovieReview?) {
        if (movie == null) {
            return
        } else {
            this.mReview = movie
            mReviewAuthor.setText(mReview!!.author)
            mReviewContent.setText(mReview!!.content)

        }
    }

    override fun onClick(p0: View?) {
        val position: Int = adapterPosition
        if (position!=RecyclerView.NO_POSITION){
                listener.onReviewReadMoreClickListener(mReview!!)
            }
        }
}
