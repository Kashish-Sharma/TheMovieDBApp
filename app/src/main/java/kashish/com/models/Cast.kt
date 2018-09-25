package kashish.com.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Kashish on 03-08-2018.
 */
class Cast() {
    @SerializedName("cast_id")
    @Expose
    var castId: Int = 0
    @SerializedName("character")
    @Expose
    var character: String? = null
    @SerializedName("credit_id")
    @Expose
    var creditId: String? = null
    @SerializedName("id")
    @Expose
    var id: Int = -1
    var contentType: Int = 0
    @SerializedName("name")
    @Expose
    var name:String? = null
    @SerializedName("order")
    @Expose
    var order:Int? = -1
    @SerializedName("profile_path")
    @Expose
    var profilePath: String? = null
}