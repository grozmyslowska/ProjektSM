package com.example.projektsm.database;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface QuoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Quote quote);

    @Update
    void update(Quote quote);

    @Delete
    void delete(Quote quote);

    @Query("DELETE FROM quote")
    void deleteAll();

    @Query("SELECT * FROM quote")
    LiveData<List<Quote>> findAll();

    @Query("SELECT * FROM quote WHERE book_title LIKE :title")
    List<Quote> findQuoteWithTitle(String title);

    @Query("SELECT * FROM quote WHERE content LIKE :s OR book_title LIKE :s OR author LIKE :s")
    List<Quote>  findQuote(String s);

    @Query("SELECT * FROM quote")
    List<Quote> findQuoteAny();

}