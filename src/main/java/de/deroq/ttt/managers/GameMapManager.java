package de.deroq.ttt.managers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import de.deroq.ttt.TTT;
import de.deroq.ttt.database.misc.TTTDatabaseMethods;
import de.deroq.ttt.models.GameMap;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class GameMapManager extends TTTDatabaseMethods {

    private final MongoCollection<GameMap> collection;
    private final Map<String, GameMap> mapCache;

    public GameMapManager(TTT ttt) {
        this.collection = ttt.getTTTDatabase().getCollection("maps", GameMap.class);
        this.mapCache = new HashMap<>();

        cacheMaps().thenAcceptAsync(b -> ttt.getGameManager().setCurrentGameMap(pickRandomMap()));
    }

    public CompletableFuture<Boolean> createMap(String muid) {
        return onInsert(collection, Filters.eq("muid", muid), GameMap.create(muid));
    }

    public CompletableFuture<Boolean> deleteMap(String muid) {
        return onDelete(collection, Filters.eq("muid", muid));
    }

    public CompletableFuture<Boolean> updateMap(GameMap gameMap) {
        return onUpdate(collection, Filters.eq("muid", gameMap.getMuid()), gameMap);
    }

    public CompletableFuture<GameMap> getAsyncMap(String muid) {
        return getAsync(collection, Filters.eq("muid", muid));
    }

    public CompletableFuture<Boolean> cacheMaps() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        getAsyncCollection(collection).thenAcceptAsync(gameMaps -> {
            gameMaps.forEach(gameMap -> mapCache.put(gameMap.getMuid(), gameMap));
            future.complete(false);
        });

        return future;
    }

    public GameMap pickRandomMap() {
        int random = new Random().nextInt(mapCache.size());
        return new ArrayList<>(mapCache.values()).get(random);
    }

    public Map<String, GameMap> getMapCache() {
        return mapCache;
    }
}
