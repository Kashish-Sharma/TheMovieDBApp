package kashish.com.database.Dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import kashish.com.database.Entities.NowShowingEntry

/**
 * Created by Kashish on 13-08-2018.
 */
@Dao
 interface NowShowingDao {

    @Query("SELECT * FROM nowshowing ORDER BY timeAdded DESC")
    fun loadAllNowShowing(): LiveData<MutableList<NowShowingEntry>>

    @Query("SELECT * FROM nowshowing WHERE movieId = :id ORDER BY timeAdded")
    fun checkIfNowShowing(id: Int):Boolean

    @Insert
    fun insertNowShowing(nowShowingEntry: NowShowingEntry)

    @Delete
    fun deleteNowShowing(nowShowingEntry: NowShowingEntry)

}