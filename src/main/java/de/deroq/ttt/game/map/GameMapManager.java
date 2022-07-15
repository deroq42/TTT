package de.deroq.ttt.game.map;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import de.deroq.database.services.mongo.MongoDatabaseServiceMethods;
import de.deroq.ttt.TTT;
import de.deroq.ttt.game.map.models.GameMap;
import de.deroq.ttt.utils.Constants;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class GameMapManager {

    private final MongoDatabaseServiceMethods databaseServiceMethods;
    private final MongoCollection<GameMap> collection;
    private final Map<String, GameMap> mapCache;

    public GameMapManager(TTT ttt) {
        this.databaseServiceMethods = ttt.getDatabaseService().getDatabaseServiceMethods();
        this.collection = ttt.getDatabaseService().getCollection("tttMaps-" + Constants.SERVER_GROUP, GameMap.class);
        this.mapCache = new HashMap<>();

        cacheMaps().thenAcceptAsync(b -> ttt.getGameManager().setCurrentGameMap(pickRandomMap()));
    }

    /**
     * Creates a GameMap.
     *
     * @param muid The id of the map to insert.
     * @return a Future with a Boolean which returns false if the GameMap has been inserted.
     */
    public CompletableFuture<Boolean> createMap(String muid) {
        return databaseServiceMethods.onInsert(
                collection,
                Filters.eq("muid", muid),
                GameMap.create(muid, Constants.SERVER_GROUP));
    }

    /**
     * Deletes a GameMap.
     *
     * @param muid The id of the map to delete.
     * @return a Future with a Boolean which returns false if the GameMap has been deleted.
     */
    public CompletableFuture<Boolean> deleteMap(String muid) {
        return databaseServiceMethods.onDelete(
                collection,
                Filters.eq("muid", muid));
    }

    /**
     * Updates a GameMap.
     *
     * @param gameMap The GameMap to update
     * @return a Future with a Boolean which returns false if the GameMap has been updated.
     */
    public CompletableFuture<Boolean> updateMap(GameMap gameMap) {
        return databaseServiceMethods.onUpdate(
                collection,
                Filters.eq("muid", gameMap.getMuid()), gameMap);
    }

    /**
     * Gets a GameMap by its id.
     *
     * @param muid The id of the map to get.
     * @return a Future with a GameMap.
     */
    public CompletableFuture<GameMap> getMap(String muid) {
        return databaseServiceMethods.getAsync(
                collection,
                Filters.eq("muid", muid));
    }

    /**
     * Caches all GameMaps.
     *
     * @return a Future with a Boolean which returns false if all maps has been cached.
     */
    public CompletableFuture<Boolean> cacheMaps() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        databaseServiceMethods.getAsyncCollection(collection).thenAcceptAsync(gameMaps -> {
            gameMaps.forEach(gameMap -> mapCache.put(gameMap.getMuid(), gameMap));
            future.complete(false);
        });

        return future;
    }

    /**
     * @return a random GameMap from the cache.
     */
    public GameMap pickRandomMap() {
        int random = new Random().nextInt(mapCache.size());
        return new ArrayList<>(mapCache.values()).get(random);
    }

    public Map<String, GameMap> getMapCache() {
        return mapCache;
    }
}
