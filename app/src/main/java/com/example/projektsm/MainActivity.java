package com.example.projektsm;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.projektsm.database.Quote;
import com.example.projektsm.database.QuoteViewModel;
import com.google.android.material.snackbar.Snackbar;


import java.util.List;

public class MainActivity extends AppCompatActivity {
    private QuoteViewModel quoteViewModel;
    private Quote selectedQuote;
    public static final int NEW_QUOTE_ACTIVITY_REQUEST_CODE = 1;
    public static final int EDIT_QUOTE_ACTIVITY_REQUEST_CODE = 2;
    QuoteAdapter quoteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        quoteAdapter = new QuoteAdapter();
        recyclerView.setAdapter(quoteAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        quoteViewModel = ViewModelProviders.of(this).get(QuoteViewModel.class);
        quoteViewModel.findAll().observe(this, new Observer<List<Quote>>() {
            @Override
            public void onChanged(List<Quote> quotes) {
                quoteAdapter.setQuotes(quotes);
            }
        });


        EditText searchProduct = findViewById(R.id.productSearch);
        searchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                new SearchTaskAsynch().execute(s.toString());
            }
        });
    }

//    @Override
//    public void onBackPressed() {
//        moveTaskToBack(true);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.new_task:
                intent = new Intent(MainActivity.this, EditQuoteActivity.class);
                startActivityForResult(intent, NEW_QUOTE_ACTIVITY_REQUEST_CODE);
                break;
            case R.id.info:
                intent = new Intent(MainActivity.this, InfoActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private class QuoteHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        private TextView quoteContentTextView;
        private TextView bookTitleTextView;
        private TextView bookAuthorTextView;
        private Quote quote;

        public QuoteHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.quote_list_item, parent, false));

            quoteContentTextView = itemView.findViewById(R.id.content);
            bookTitleTextView = itemView.findViewById(R.id.book);
            bookAuthorTextView = itemView.findViewById(R.id.author);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(Quote g){
            quoteContentTextView.setText(g.getContent());
            bookTitleTextView.setText(g.getBookTitle());
            bookAuthorTextView.setText(g.getAuthor());
            quote = g;
        }

        @Override
        public void onClick(View view) {
            selectedQuote = quote;

            Intent intent = new Intent(MainActivity.this, EditQuoteActivity.class);
            intent.putExtra("CONTENT", quoteContentTextView.getText().toString());
            intent.putExtra("BOOK_TITLE", bookTitleTextView.getText().toString());
            intent.putExtra("BOOK_AUTHOR", bookAuthorTextView.getText().toString());
            startActivityForResult(intent, EDIT_QUOTE_ACTIVITY_REQUEST_CODE);
        }

        @Override
        public boolean onLongClick(View view) {
            quoteViewModel.delete(quote);
            return true;
        }
    }

    private class QuoteAdapter extends RecyclerView.Adapter<QuoteHolder> {
        private List<Quote> quotes;

        @NonNull
        @Override
        public QuoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new QuoteHolder(getLayoutInflater(), parent);
        }

        @Override
        public void onBindViewHolder(@NonNull QuoteHolder holder, int position) {
            if(quotes != null) {
                Quote quote = quotes.get(position);
                holder.bind(quote);
            }
            else {
                Log.d("MainActivity", "No quotes");
            }

        }

        @Override
        public int getItemCount() {
            if(quotes != null){
                return quotes.size();
            }
            else {
                return 0;
            }
        }

        public void setQuotes(List<Quote> quotes){
            this.quotes = quotes;
            notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == NEW_QUOTE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            Quote quote = new Quote(data.getStringExtra(EditQuoteActivity.EXTRA_EDIT_CONTENT),
                    data.getStringExtra(EditQuoteActivity.EXTRA_EDIT_BOOK_AUTHOR),
                    data.getStringExtra(EditQuoteActivity.EXTRA_EDIT_BOOK_TITLE));
            quoteViewModel.insert(quote);
            Snackbar.make(findViewById(R.id.main_layout), getString(R.string.quote_added), Snackbar.LENGTH_LONG).show();
        }
        else if(requestCode == EDIT_QUOTE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            selectedQuote.setContent(data.getStringExtra(EditQuoteActivity.EXTRA_EDIT_CONTENT));
            selectedQuote.setAuthor(data.getStringExtra(EditQuoteActivity.EXTRA_EDIT_BOOK_AUTHOR));
            selectedQuote.setBookTitle(data.getStringExtra(EditQuoteActivity.EXTRA_EDIT_BOOK_TITLE));
            quoteViewModel.update(selectedQuote);
            Snackbar.make(findViewById(R.id.main_layout), getString(R.string.quote_updated), Snackbar.LENGTH_LONG).show();
        }
        else {
            //Toast.makeText(getApplicationContext(), R.string.empty_not_saved, Toast.LENGTH_LONG).show();
        }

    }


    private class SearchTaskAsynch extends AsyncTask<String,Void,List<Quote>> {

        String SearchingText;

        @Override
        protected List<Quote> doInBackground(String ...Id){
            SearchingText=Id[0];
            List<Quote> quotes = quoteViewModel.findQuote("%"+SearchingText+"%");
            return quotes;
        }

        @Override
        protected void onPostExecute(List<Quote> result){// ta funkcja odpala sie na zakończenie, result to wynik
            quoteAdapter.setQuotes(result);
            quoteAdapter.notifyDataSetChanged();
        }

    }


}
