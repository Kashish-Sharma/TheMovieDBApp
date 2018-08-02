package kashish.com.viewholders

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.TextView
import kashish.com.R
import kotlinx.android.synthetic.main.review_single_item.view.*

/**
 * Created by Kashish on 02-08-2018.
 */
class ReviewViewHolder(itemView: View?, context: Context) : RecyclerView.ViewHolder(itemView) {

    var mReviewAuthor: TextView
    var mReviewContent: TextView
    var mReadMoreButton: TextView

    init{
        mReviewAuthor = itemView!!.findViewById(R.id.review_author_name)
        mReviewContent = itemView.findViewById(R.id.review_content)
        mReadMoreButton = itemView.findViewById(R.id.review_read_more)
    }

}