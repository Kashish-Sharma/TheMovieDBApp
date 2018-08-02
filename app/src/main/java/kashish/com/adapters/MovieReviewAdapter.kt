package kashish.com.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kashish.com.R
import kashish.com.models.MovieReview
import kashish.com.utils.Constants.Companion.CONTENT_REVIEW
import kashish.com.viewholders.ProgressBarViewHolder
import kashish.com.viewholders.ReviewViewHolder

/**
 * Created by Kashish on 02-08-2018.
 */
class MovieReviewAdapter(private var reviewList: List<MovieReview>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view: View
        mContext = parent.context

        view = LayoutInflater.from(parent.context)
                .inflate(R.layout.review_single_item, parent, false)
        return ReviewViewHolder(view,mContext)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

                val reviewViewHolder = holder as ReviewViewHolder
                val review: MovieReview = reviewList.get(holder.adapterPosition)

                reviewViewHolder.mReviewAuthor.setText(review.author)
                reviewViewHolder.mReviewContent.setText(review.content)

                reviewViewHolder.mReadMoreButton.setOnClickListener(View.OnClickListener {

                })
    }

    override fun getItemCount(): Int = reviewList.size
}