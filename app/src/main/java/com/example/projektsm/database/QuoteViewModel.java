package com.example.projektsm.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class QuoteViewModel extends AndroidViewModel {

    private QuoteRepository quoteRepository;
    private LiveData<List<Quote>> quotes;

    public QuoteViewModel(@NonNull Application application) {
        super(application);
        quoteRepository = new QuoteRepository(application);
        quotes = quoteRepository.findAllQuotes();
    }

    public LiveData<List<Quote>> findAll(){
        return quotes;
    }

    public void insert(Quote quote){
        quoteRepository.insert(quote);
    }

    public void update(Quote quote){
        quoteRepository.update(quote);
    }

    public void delete(Quote quote){
        quoteRepository.delete(quote);
    }

    public List<Quote> findQuote(String s){
        return quoteRepository.findQuote(s);
    }

    public void findByTitle(String title) {
        quoteRepository.findQuoteWithTitle(title);
    }
}
