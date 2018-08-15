package kashish.com.viewholders

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import kashish.com.R
import kashish.com.models.Cast
import kashish.com.models.Crew
import kashish.com.utils.Constants.Companion.RANDOM_PATH
import kashish.com.utils.Helpers

/**
 * Created by Kashish on 03-08-2018.
 */
class CastCrewViewHolder(itemView: View?,private val mContext: Context,private val mSharedPreferences: SharedPreferences): RecyclerView.ViewHolder(itemView) {
    var mCastImage: ImageView
    var mCastName: TextView
    var mCastCharacter: TextView

    init{
        mCastImage = itemView!!.findViewById(R.id.activity_detail_movie_cast_image)
        mCastName = itemView.findViewById(R.id.activity_detail_movie_cast_name)
        mCastCharacter = itemView.findViewById(R.id.activity_detail_movie_cast_character)
    }


    fun bindCrewData(crew: Crew){

        mCastName.text = crew.name
        mCastCharacter.text = crew.department

        if (crew.profilePath.isNullOrEmpty()) crew.profilePath = RANDOM_PATH

        if (mSharedPreferences.getBoolean(mContext.getString(R.string.pref_cache_data_key),true)){
            Glide.with(mContext).load(Helpers.buildProfileImageUrl(crew.profilePath!!))
                    .apply(RequestOptions().placeholder(R.color.dark_gray))
                    .thumbnail(0.05f)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(mCastImage)
        } else {
            Glide.with(mContext).load(Helpers.buildProfileImageUrl(crew.profilePath!!))
                    .apply(RequestOptions().placeholder(R.color.dark_gray)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true))
                    .thumbnail(0.05f)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(mCastImage)
        }
    }


    fun bindCastData(cast: Cast){

        mCastName.setText(cast.name)
        mCastCharacter.setText(cast.character)

        if (cast.profilePath.isNullOrEmpty()) cast.profilePath = RANDOM_PATH


        if (mSharedPreferences.getBoolean(mContext.getString(R.string.pref_cache_data_key),true)){

            Glide.with(mContext).load(Helpers.buildProfileImageUrl(cast.profilePath!!))
                    .apply(RequestOptions().placeholder(R.color.dark_gray))
                    .thumbnail(0.05f)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(mCastImage)

        } else {
            Glide.with(mContext).load(Helpers.buildProfileImageUrl(cast.profilePath!!))
                    .apply(RequestOptions().placeholder(R.color.dark_gray)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true))
                    .thumbnail(0.05f)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(mCastImage)
        }
    }

}