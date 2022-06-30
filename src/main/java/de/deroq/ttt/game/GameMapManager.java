package de.deroq.ttt.game;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import de.deroq.database.services.mongo.MongoDatabaseServiceMethods;
import de.deroq.ttt.TTT;
import de.deroq.ttt.game.models.GameMap;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class GameMapManager {

    private final MongoDatabaseServiceMethods databaseServiceMethods;
    private final MongoCollection<GameMap> collection;
    private final Map<String, GameMap> mapCache;

    public GameMapManager(TTT ttt) {
        this.databaseServiceMethods = ttt.getDatabaseService().getMongoDatabaseServiceMethods();
        this.collection = ttt.getDatabaseService().getCollection("maps", GameMap.class);
        this.mapCache = new HashMap<>();

        cacheMaps().thenAcceptAsync(b -> ttt.getGameManager().setCurrentGameMap(pickRandomMap()));
    }

    public CompletableFuture<Boolean> createMap(String muid) {
        return databaseServiceMethods.onInsert(collection, Filters.eq("muid", muid), GameMap.create(muid));
    }

    public CompletableFuture<Boolean> deleteMap(String muid) {
        return databaseServiceMethods.onDelete(collection, Filters.eq("muid", muid));
    }

    public CompletableFuture<Boolean> updateMap(GameMap gameMap) {
        return databaseServiceMethods.onUpdate(collection, Filters.eq("muid", gameMap.getMuid()), gameMap);
    }

    public CompletableFuture<GameMap> getMap(String muid) {
        return databaseServiceMethods.getAsync(collection, Filters.eq("muid", muid));
    }

    public CompletableFuture<Boolean> cacheMaps() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        databaseServiceMethods.getAsyncCollection(collection).thenAcceptAsync(gameMaps -> {
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
