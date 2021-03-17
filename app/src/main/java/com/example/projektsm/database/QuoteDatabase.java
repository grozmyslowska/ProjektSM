package com.example.projektsm.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Quote.class}, version = 1, exportSchema = false)
public abstract class QuoteDatabase extends RoomDatabase {
    public abstract QuoteDao quoteDao();

    private static volatile QuoteDatabase INSTANCE;
    public static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static QuoteDatabase getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (QuoteDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            QuoteDatabase.class, "quote_db").addCallback(sRoomDatabaseCallback).build();
                }
            }
        }

        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback(){
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            databaseWriteExecutor.execute(() -> {
                QuoteDao dao = INSTANCE.quoteDao();

                if(dao.findQuoteAny().isEmpty()) {

                    Quote quote1 = new Quote("Bo serce nie jest sługa, nie zna, co to pany,\n" +
                            "I nie da się przemocą okuwać w kajdany. ",
                            "Adam Mickiewicz",
                            "Pan Tadeusz");

                    Quote quote2 = new Quote("Nazywam się Milijon - bo za milijony\n" +
                            "Kocham i cierpię katusze. [Konrad]",
                            "Adam Mickiewicz",
                            "Dziady cz. III");

                    Quote quote3 = new Quote("- My, Krzyżacy, nie boim się nikogo - odparł dumnie komtur.\n" +
                            "A stary kasztelan dodał z cicha:\n" +
                            "- A zwłaszcza Boga. ",
                            "Henryk Sienkiewicz",
                            "Krzyżacy");

                    Quote quote4 = new Quote("Ludzi o nowych ideach, nawet tych, którzy w ogóle są zdolni " +
                            "powiedzieć coś nowego, rodzi się bardzo mało, wprost zdumiewająco mało.",
                            "Fiodor Dostojewski",
                            "Zbrodnia i kara");

                    Quote quote5 = new Quote("Po co ma żyć? Do czego dążyć? Żyć tylko po to, żeby istnieć? " +
                            "Przecież on i dawniej gotów był tysiąc razy oddać swoje istnienie za jakąś ideę, nadzieję lub " +
                            "choćby nawet fantazję. Samo istnienie nigdy mu nie wystarczało, zawsze pragnął czegoś więcej.",
                            "Fiodor Dostojewski",
                            "Zbrodnia i kara");

                    dao.insert(quote1);
                    dao.insert(quote2);
                    dao.insert(quote3);
                    dao.insert(quote4);
                    dao.insert(quote5);
                }
            });
        }
    };

}




//@android.arch.persistence.room.Database(entities = {Quote.class}, version = 3)
//public abstract class QuoteDatabase extends RoomDatabase {
//    public abstract QuoteDao quoteDao();
//}
