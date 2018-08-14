package kashish.com.database.Dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import kashish.com.database.Entities.TopRatedEntry

/**
 * Created by Kashish on 13-08-2018.
 */
@Dao
interface TopRatedDao {

    @Query("SELECT * FROM toprated ORDER BY voteCount DESC")
    fun loadAllToprated(): LiveData<List<TopRatedEntry>>

    @Query("SELECT * FROM toprated WHERE movieId = :id ORDER BY timeAdded")
    fun checkIfToprated(id: Int):Boolean

    @Insert
    fun insertToprated(topRatedEntry: TopRatedEntry)

    @Delete
    fun deleteToprated(topRatedEntry: TopRatedEntry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(searches: List<TopRatedEntry>)

    @Query("DELETE FROM toprated")
    fun deleteAll()

}