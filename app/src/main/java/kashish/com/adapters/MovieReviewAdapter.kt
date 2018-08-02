package kashish.com.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
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

        when(viewType){
            CONTENT_REVIEW -> {
                view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.review_single_item, parent, false)
                return ReviewViewHolder(view,mContext)
            }

            else -> {
                view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.recycler_view_progress_loader, parent, false);
                return ProgressBarViewHolder(view);
            }

        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(holder.itemViewType){
            CONTENT_REVIEW -> {

            }

        }

    }


    override fun getItemViewType(position: Int): Int {
        return reviewList.get(position).contentType
    }

    override fun getItemCount(): Int = reviewList.size
}