package org.csc133.a5.gameobjects;

import com.codename1.ui.Transform;
import org.csc133.a5.GameWorld;
import org.csc133.a5.interfaces.Observer;
import org.csc133.a5.interfaces.Subject;
import java.util.ArrayList;

public class FireDispatch implements Subject {
    private final ArrayList<Observer> observers;
    private Fire selected;

    public FireDispatch() {
        observers = new ArrayList<>();
    }

    @Override
    public void attach(Observer o) {
        observers.add(o);
    }

    @Override
    public void detach(Observer o) {
        observers.remove(o);
    }

    void setSelectedFire(Fire selected) {
        this.selected = selected;
        notifyObservers();
        updateSelectedFire();
    }

    @Override
    public void notifyObservers() {
        for(Observer o : observers) {
            o.update(selected);
        }
    }

    private void updateSelectedFire() {
        Transform fire = Transform.makeIdentity();
        fire.translate(selected.getX(), selected.getY());
        GameWorld.getInstance().updateSelectedFire(fire);
    }
}
