package kashish.com.viewholders

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import kashish.com.R

/**
 * Created by Kashish on 03-08-2018.
 */
class CastCrewViewHolder(itemView: View?, context: Context): RecyclerView.ViewHolder(itemView) {
    var mCastImage: ImageView
    var mCastName: TextView
    var mCastCharacter: TextView

    init{
        mCastImage = itemView!!.findViewById(R.id.activity_detail_movie_cast_image)
        mCastName = itemView.findViewById(R.id.activity_detail_movie_cast_name)
        mCastCharacter = itemView.findViewById(R.id.activity_detail_movie_cast_character)
    }
}