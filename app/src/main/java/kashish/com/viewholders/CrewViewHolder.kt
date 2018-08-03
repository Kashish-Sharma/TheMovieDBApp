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
class CrewViewHolder(itemView: View?, context: Context): RecyclerView.ViewHolder(itemView) {
    var mCrewImage: ImageView
    var mCrewName: TextView
    var mCrewJob: TextView

    init{
        mCrewImage = itemView!!.findViewById(R.id.activity_detail_movie_crew_image)
        mCrewName = itemView.findViewById(R.id.activity_detail_movie_crew_name)
        mCrewJob = itemView.findViewById(R.id.activity_detail_movie_crew_character)
    }
}