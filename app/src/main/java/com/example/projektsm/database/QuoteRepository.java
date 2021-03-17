package com.example.projektsm.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class QuoteRepository {
    private QuoteDao quoteDao;
    private LiveData<List<Quote>> quotes;

    public QuoteRepository(Application application) {
        QuoteDatabase bookDatabase = QuoteDatabase.getDatabase(application);
        quoteDao = bookDatabase.quoteDao();
        quotes = quoteDao.findAll();
    }

    LiveData<List<Quote>> findAllQuotes(){
        return quoteDao.findAll();
    }

    void insert(Quote book){
        QuoteDatabase.databaseWriteExecutor.execute(() -> quoteDao.insert(book));
    }

    void update(Quote book){
        QuoteDatabase.databaseWriteExecutor.execute(() -> quoteDao.update(book));
    }

    void delete(Quote book){
        QuoteDatabase.databaseWriteExecutor.execute(() -> quoteDao.delete(book));
    }

    List<Quote> findQuote(String s){
        return quoteDao.findQuote(s);
    }

    List<Quote> findQuoteWithTitle(String title){
        return quoteDao.findQuoteWithTitle(title);
    }
}
