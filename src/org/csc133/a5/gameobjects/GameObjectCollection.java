package org.csc133.a5.gameobjects;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameObjectCollection<T> implements Iterable<T> {
    private final CopyOnWriteArrayList<T> gameObjects;

    private class GameObjectIterator implements Iterator<T> {
        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < gameObjects.size();
        }

        @Override
        public T next() {
            return gameObjects.get(index++);
        }
    }

    public GameObjectCollection() {
        gameObjects = new CopyOnWriteArrayList<>();
    }

    // Return a sub-collection of objects of the same type.
    //
    private CopyOnWriteArrayList<T> getMonoObjects(Class<?> classType) {
        CopyOnWriteArrayList<T> go = new CopyOnWriteArrayList<>();
        gameObjects.stream()
                   .filter(classType::isInstance)
                   .forEach(go::add);
        return go;
    }

    /* Following castings are legal as long as the getMonoObjects method
       returns a collection of objects of the specified type.
    */
    @SuppressWarnings("unchecked")
    public CopyOnWriteArrayList<Fire> getFires() {
        return (CopyOnWriteArrayList<Fire>) getMonoObjects(Fire.class);
    }

    @SuppressWarnings("unchecked")
    public CopyOnWriteArrayList<River> getRiver() {
        return (CopyOnWriteArrayList<River>) getMonoObjects(River.class);
    }

    @SuppressWarnings("unchecked")
    public CopyOnWriteArrayList<Helipad> getHelipad() {
        return (CopyOnWriteArrayList<Helipad>) getMonoObjects(Helipad.class);
    }

    @SuppressWarnings("unchecked")
    public CopyOnWriteArrayList<Building> getBuildings() {
        return (CopyOnWriteArrayList<Building>) getMonoObjects(Building.class);
    }

    public void add(T gameObject) {
        gameObjects.add(gameObject);
    }

    public void add(int index, T gameObject) {
        gameObjects.add(index, gameObject);
    }

    @Override
    public Iterator<T> iterator() {
        return new GameObjectIterator();
    }
}
