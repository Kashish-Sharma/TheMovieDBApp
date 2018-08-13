package kashish.com.database.Dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import kashish.com.database.Entities.PopularEntry

/**
 * Created by Kashish on 13-08-2018.
 */
@Dao
interface PopularDao {

    @Query("SELECT * FROM nowshowing ORDER BY timeAdded DESC")
    fun loadAllPopular(): LiveData<MutableList<PopularEntry>>

    @Query("SELECT * FROM nowshowing WHERE movieId = :id ORDER BY timeAdded")
    fun checkIfPopular(id: Int):Boolean

    @Insert
    fun insertPopular(popularEntry: PopularEntry)

    @Delete
    fun deletePopular(popularEntry: PopularEntry)

}