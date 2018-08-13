package kashish.com.database.Dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import kashish.com.database.Entities.UpcomingEntry


/**
 * Created by Kashish on 13-08-2018.
 */
@Dao
interface UpcomingDao {

    @Query("SELECT * FROM upcoming ORDER BY timeAdded DESC")
    fun loadAllUpcoming(): LiveData<MutableList<UpcomingEntry>>

    @Query("SELECT * FROM upcoming WHERE movieId = :id ORDER BY timeAdded")
    fun checkIfUpcoming(id: Int):Boolean

    @Insert
    fun insertUpcoming(upcomingEntry: UpcomingEntry)

    @Delete
    fun deleteUpcoming(upcomingEntry: UpcomingEntry)

}