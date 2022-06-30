package de.deroq.ttt.database.misc;

import com.mongodb.client.MongoCollection;
import de.deroq.ttt.TTT;
import de.deroq.ttt.utils.Constants;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public abstract class TTTDatabaseMethods {

    protected <T> CompletableFuture<Boolean> onInsert(MongoCollection<T> collection, Bson filter, T t) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            Bukkit.getScheduler().runTaskAsynchronously(JavaPlugin.getPlugin(TTT.class), () -> {
                T document = collection.find(filter).first();
                if (document != null) {
                    future.complete(true);
                    return;
                }

                collection.insertOne(t);
                future.complete(false);
            });
        }, Constants.EXECUTOR_SERVICE);
        return future;
    }

    protected <T> CompletableFuture<Boolean> onDelete(MongoCollection<T> collection, Bson filter) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            T document = collection.find(filter).first();
            if (document == null) {
                future.complete(true);
                return;
            }

            collection.deleteOne(filter);
            future.complete(false);
        }, Constants.EXECUTOR_SERVICE);

        return future;
    }

    protected <T> CompletableFuture<Boolean> onUpdate(MongoCollection<T> collection, Bson filter, T t) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            T document = collection.find(filter).first();
            if (document == null) {
                future.complete(true);
                return;
            }

            collection.replaceOne(filter, t);
            future.complete(false);
        }, Constants.EXECUTOR_SERVICE);

        return future;
    }

    protected <T> CompletableFuture<T> getAsync(MongoCollection<T> collection, Bson filter) {
        CompletableFuture<T> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            T document = collection.find(filter).first();
            if (document == null) {
                future.complete(null);
                return;
            }

            future.complete(document);
        }, Constants.EXECUTOR_SERVICE);

        return future;
    }

    protected <T> CompletableFuture<Collection<T>> getAsyncCollection(MongoCollection<T> collection) {
        CompletableFuture<Collection<T>> future = new CompletableFuture<>();
        Collection<T> list = new ArrayList<>();

        CompletableFuture.runAsync(() -> {
            collection.find().forEach(list::add);
            future.complete(list);
        }, Constants.EXECUTOR_SERVICE);

        return future;
    }
}
