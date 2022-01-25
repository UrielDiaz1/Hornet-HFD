package org.csc133.a5;

import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.Transform;
import com.codename1.ui.geom.Dimension;
import org.csc133.a5.gameobjects.*;
import org.csc133.a5.sound.BGSound;
import org.csc133.a5.sound.Sound;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class GameWorld {
    private static GameWorld instance;
    private int NUMBER_OF_BUILDINGS;
    private int fireAreaBudget;
    private int numberOfFires;
    private int defaultFireSize;
    private int initFuel;
    private long startOfElapsedTime;
    private Random rand;
    private Building building;
    private Dimension mapSize;
    private Dimension controlSize;
    private ArrayList<Integer> fireSizes;
    private ArrayList<Sound> sounds;
    private GameObjectCollection<GameObject> gameObjectCollection;
    private FireDispatch fireDispatch;
    private FlightPath flightPath;
    private BGSound currentTrack;
    private Sound crashWarning;
    private Sound crash;
    private Sound chopper;
    private Sound steam;
    private Semaphore semCrashWarning;
    private Semaphore semCrash;
    private Semaphore semChopper;
    private Semaphore semSteam;
    private Thread musicThread;

    private GameWorld() {}

    public static GameWorld getInstance() {
        if(instance == null) {
            instance = new GameWorld();
        }
        return instance;
    }

    public void init() {
        initFuel            = 25000;
        fireAreaBudget      = 1000;
        NUMBER_OF_BUILDINGS = 3;
        numberOfFires       = 0;
        defaultFireSize     = 5;
        startOfElapsedTime  = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());

        rand                 = new Random();
        fireDispatch         = new FireDispatch();
        gameObjectCollection = new GameObjectCollection<>();

        populateGameObjectCollection();
        musicThread = new Thread(this::playBackgroundMusic);
        musicThread.start();
        initSemaphores();
        initSounds();
    }

    private void populateGameObjectCollection() {
        gameObjectCollection.add(new River(mapSize));
        gameObjectCollection.add(new Helipad(mapSize));

        fireSizes = new ArrayList<>();
        setNumberOfFires();
        createBuildingAndFires();
        setFireInBuilding();

        flightPath = new FlightPath(getTakeOffPoint(), mapSize);
        gameObjectCollection.add(flightPath.getPathToRiver());
        gameObjectCollection.add(flightPath.getPathToFire());
        gameObjectCollection.add(flightPath.getPathFromFire());
        gameObjectCollection.add(PlayerHelicopter.getInstance());
    }

    private void setNumberOfFires() {
        while(fireAreaBudget > 0) {
            fireSizes.add(expendFireBudget());
            numberOfFires++;
        }
    }

    private int expendFireBudget() {
        int area = rand.nextInt(180 - 80) + 80;

        if(area > fireAreaBudget) {
            area = fireAreaBudget;
            fireAreaBudget = 0;
        }
        else {
            fireAreaBudget -= area;
        }
        return findRadiusFromArea(area);
    }

    private int findRadiusFromArea(int area) {
        return (int) Math.ceil(Math.sqrt(area / Math.PI));
    }

    private void createBuildingAndFires() {
        int firesToCreate = numberOfFires;
        for (int i = NUMBER_OF_BUILDINGS; i > 0; i--) {
            gameObjectCollection.add(new Building(mapSize, i - 1));

            // Divides by i to ensure that each building gets a balanced
            // amount of fires.
            //
            for(int j = firesToCreate / i; j > 0; j--) {
                gameObjectCollection.add(new Fire(mapSize,
                                                  fireSizes.get(0),
                                                  fireDispatch));
                fireSizes.remove(0);
                firesToCreate--;
            }
        }
    }

    private void setFireInBuilding() {
        // Fires are spawned in the nearest left Building in the collection.
        //
        for(GameObject go : gameObjectCollection) {
            if(go instanceof Building) {
                building = (Building)go;
            }
            if(go instanceof Fire) {
                Fire fire = (Fire)go;
                building.setFireInBuilding(fire);
            }
        }
    }

    private void spawnNewFires() {
        // The index ensures that fires are added in the proper location (to
        // the right of its container building in the list) for when it gets
        // referenced.
        //
        int index = 0;
        for (Building building : gameObjectCollection.getBuildings()) {
            if (willFireSpawn(building)) {
                Fire fire = new Fire(mapSize, defaultFireSize, fireDispatch);
                building.setFireInBuilding(fire);
                gameObjectCollection.add(index + 1, fire);
                updateNumOfFires(1);
            }
            index++;
        }
    }

    private boolean willFireSpawn(Building building) {
        if(building.allFiresPutOut()) {
            return false;
        }
        return rand.nextInt((int) (700 - building.getDamagePercentage())) < 1;
    }

    public GameObjectCollection<GameObject> getGameObjectCollection() {
        return gameObjectCollection;
    }

    public void spawnNPH() {
        gameObjectCollection.add(NonPlayerHelicopter.getInstance());
    }

    public int getInitialFuel() {
        return initFuel;
    }

    public Transform getTakeOffPoint() {
        return gameObjectCollection.getHelipad().get(0).takeoffSpot();
    }

    public void layGameMap(Dimension mapSize) {
        this.mapSize = mapSize;
    }

    public void layButtons(int controlWidth, int controlHeight) {
        this.controlSize = new Dimension(controlWidth, controlHeight);
    }

    public Dimension getMapSize() {
        return mapSize;
    }

    public int getControlClusterHeight() {
        return controlSize.getHeight();
    }

    public FlightPath getFlightPath() {
        return flightPath;
    }

    public Transform getRiverOrigin() {
        return gameObjectCollection.getRiver().get(0).getTranslation();
    }

    public Dimension getRiverDimension() {
        return gameObjectCollection.getRiver().get(0).getDimension();
    }

    public void updateSelectedFire(Transform selectedFire) {
        flightPath.updateSelectedFire(selectedFire);
    }

    public int getNumOfBuildings() {
        return NUMBER_OF_BUILDINGS;
    }

    public int getNumOfFires() {
        return numberOfFires;
    }

    /**
     * Just to make it easier to abide to the 80-column limit.
     */
    private PlayerHelicopter getPlayerHelo() {
        return PlayerHelicopter.getInstance();
    }

    public void startOrStopEngine() {
        getPlayerHelo().startOrStopEngine();
    }

    public void turnLeft() {
        getPlayerHelo().steerLeft();
    }

    public void turnRight() {
        getPlayerHelo().steerRight();
    }

    public void accelerate() {
        getPlayerHelo().accelerate();
    }

    public void brake() {
        getPlayerHelo().brake();
    }

    private void depleteFuel() {
        PlayerHelicopter.getInstance().depleteFuel();
        NonPlayerHelicopter.getInstance().depleteFuel();
    }

    private void move(long elapsedTimeInMillis) {
        PlayerHelicopter.getInstance().move(elapsedTimeInMillis);
    }

    private long getElapsedTimeInMillis() {
        long endElapsedTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        long elapsedTimeInMillis = endElapsedTime - startOfElapsedTime;
        startOfElapsedTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        return elapsedTimeInMillis;
    }

    public void drink() {
        for(River river : gameObjectCollection.getRiver()) {
                int w = river.getWidth();
                int h = river.getHeight();
                getPlayerHelo().drink(river.getTranslation(), w, h);
        }
    }

    public void attemptFightFire(Helicopter helicopter) {
        for (Fire fire : gameObjectCollection.getFires()) {
            if (helicopterHasWater(helicopter)
                    && isHelicopterOverFire(helicopter, fire)
                    && !isExtinguished(fire)) {
                fire.shrink(helicopter.getWater());
                semSteam.release();

                if (fire.getSize() <= 0) {
                    updateNumOfFires(-1);
                }
            }
        }
        helicopter.dumpWater();
    }

    private boolean helicopterHasWater(Helicopter helicopter) {
        return helicopter.getWater() > 0;
    }

    private boolean isHelicopterOverFire(Helicopter helicopter, Fire fire) {
        int w = fire.getSize();
        int h = fire.getSize();
        return helicopter.onTopOfObject(fire.getTranslation(), w, h);
    }

    private boolean isExtinguished(Fire fire) {
        return fire.currentState().equals("Extinguished");
    }

    private void updateNumOfFires(int updateValue) {
        numberOfFires += updateValue;
    }

    private void grow() {
        for(Fire fire : gameObjectCollection.getFires()) {
            if(fire.getSize() <= 0 && !isExtinguished(fire)) {
                updateNumOfFires(-1);
            }
            else if(rand.nextInt(8) < 1) {
                fire.grow();
            }
        }
    }

    private void burnBuilding() {
        for(Building building : gameObjectCollection.getBuildings()) {
                building.accumulateFireAreas();
                building.setDamagePercentage();
        }
    }

    private void checkIfWon() {
        if(!areThereFires() && hasLandedOnHelipad() && isThereFuel()) {
            displayWinDialog();
        }
    }

    private boolean areThereFires() {
        for(Fire fire : gameObjectCollection.getFires()) {
            if(!fire.currentState().equals("Extinguished")) {
                return true;
            }
        }
        return false;
    }

    private boolean hasLandedOnHelipad() {
        Helipad helipad = getGameObjectCollection().getHelipad().get(0);
        PlayerHelicopter ph = PlayerHelicopter.getInstance();

        int w = helipad.getWidth();
        int h = helipad.getHeight();
        return ph.onTopOfObject(helipad.getTranslation(), w, h) &&
                ph.currentState().equals("Off");
    }

    private boolean isThereFuel() {
        return  PlayerHelicopter.getInstance().getFuel() > 0 &&
                NonPlayerHelicopter.getInstance().getFuel() > 0;
    }

    public void restartGame() {
        if(Dialog.show("Game Paused", "Are you sure you want to restart " +
                       "the game?", "Yes, restart the game", "No")) {
            resetEverything();
        }
    }

    private void displayWinDialog() {
        if(Dialog.show("Game Over", "You Won!" + "\nScore: " + getScore() +
                       "\nPlay again?", "Heck Yeah!", "Some other time")) {
            resetEverything();
        }
        else {
            exit();
        }
    }

    private int getScore() {
        return (int) (100 - getDamagePercentage());
    }

    private void checkIfLost() {
        String lossReason = "";

        if(!isThereFuel()) {
            lossReason = "One of the helicopters ran out of fuel!";
        }
        else if(allBuildingsBurned()) {
            lossReason = "All buildings burned out!";
        }
        else if(crashed()) {
            semCrash.release();
            lossReason = "You crashed!";
        }

        // If a lose-condition is met, finish game.
        //
        if(!lossReason.isEmpty()) {
            displayLossDialog(lossReason);
        }
    }

    private boolean allBuildingsBurned() {
        return getDamagePercentage() >= 100;
    }

    private double getDamagePercentage() {
        double totalDamagePercentage = 0;
        for(Building building : gameObjectCollection.getBuildings()) {
            totalDamagePercentage += building.getDamagePercentage();
        }
        return totalDamagePercentage / NUMBER_OF_BUILDINGS;
    }

    private boolean crashed() {
        return NonPlayerHelicopter.getInstance().crashed();
    }

    private void displayLossDialog(String lossReason) {
        if(Dialog.show("Game Over", lossReason +
                       "\nScore: 0"  +
                       "\nPlay again?", "Heck Yeah!", "Some other time")) {
            resetEverything();
        }
        else {
            exit();
        }
    }

    private void resetEverything() {
        PlayerHelicopter.getInstance().reset();
        NonPlayerHelicopter.getInstance().reset();
        Thread soundReset = new Thread(this::resetMusic);
        soundReset.start();
        try {
            // Waits for the soundReset thread to finish stopping all music.
            //
            soundReset.join();
            resetMusicThread();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        resetChopperSound();
        init();
    }

    public void exit() {
        if(Dialog.show("Game Paused", "Are you sure you want to quit?",
                       "Yes", "I want to keep playing")) {
            Display.getInstance().exitApplication();
        }
    }

    public void updateLocalTransforms() {
        PlayerHelicopter.getInstance().updateLocalTransforms();
        NonPlayerHelicopter.getInstance().updateLocalTransforms();
    }

    public String getHelicopterState() {
        return getPlayerHelo().currentState();
    }

    void initSounds() {
        sounds = new ArrayList<>();
        int chopperSoundVolume = 6;
        int warningSoundVolume = 10;
        int crashSoundVolume   = 25;
        int steamSoundVolume   = 10;

        while (chopper == null) {
            chopper = new Sound("chopper.wav", chopperSoundVolume);
        }

        while(crashWarning == null) {
            crashWarning = new Sound("warning_sound.wav", warningSoundVolume);
        }

        while(crash == null) {
            crash = new Sound("crash.wav", crashSoundVolume);
        }

        while(steam == null) {
            steam = new Sound("steam.wav", steamSoundVolume);
        }

        sounds.add(chopper);
        sounds.add(crashWarning);
        sounds.add(crash);
        sounds.add(steam);
    }

    public void startSoundThreads() {
        new Thread(this::runChopper).start();
        new Thread(this::runCrashWarning).start();
        new Thread(this::runCrash).start();
        new Thread(this::runSteam).start();
    }

    private void initSemaphores() {
        semChopper      = new Semaphore(0);
        semCrashWarning = new Semaphore(0);
        semCrash        = new Semaphore(0);
        semSteam        = new Semaphore(0);
    }

    public synchronized void initiateChopper() {
        if(isHeloFlying()) {
            semChopper.release();
        }
    }

    public synchronized void stopChopper() {
        if(!isHeloFlying()) {
            semChopper.drainPermits();
        }
    }

    private void resetChopperSound() {
        if(chopper != null) {
            chopper.stop();
        }
    }

    public synchronized void initiateCrashWarning() {
        semCrashWarning.release();
    }

    private boolean isHeloFlying() {
        return  getPlayerHelo().currentState().equals("Ready") ||
                getPlayerHelo().currentState().equals("Can land");
    }

    private void runCrashWarning() {
        int startTime = 2000;
        while(true) {
            try {
                semCrashWarning.acquire();
                playSound(crashWarning, startTime);
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    private void runCrash() {
        while(true) {
            try {
                semCrash.acquire();
                playSound(crash);
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    private void runChopper() {
        int soundEffectLength = 2100;
        while(true) {
            try {
                semChopper.acquire();
                playSound(chopper);
                Thread.sleep(soundEffectLength);

                if(isHeloFlying()) {
                    semChopper.release();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void runSteam() {
        while(true) {
            try {
                semSteam.acquire();
                playSound(steam);
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    private void playSound(Sound sound) {
        playSound(sound, 0);
    }

    private void playSound(Sound sound, int startTime) {
        try {
            if(sound != null) {
                sound.play(startTime);
            }
        } catch (NullPointerException e) { e.printStackTrace(); }
    }

    private void playBackgroundMusic() {
        int volume          = 10;
        long trackOneLength = 242000;

        // Keeps attempting to initialize the track until successful.
        //
        while(currentTrack == null) {
            currentTrack = new BGSound("Honor and Sword (Main).mp3", volume);
        }

        try {
            Thread.sleep(trackOneLength);
            resetMusic();
        } catch (InterruptedException e) {
            // This thread is interrupted when the game restarts.
            resetMusic();
            return;
        }

        while(currentTrack == null) {
            currentTrack = new BGSound("Cinematic Melody (Main).mp3", volume);
        }
    }

    private void resetMusic() {
        if(currentTrack != null) {
            currentTrack.stop();
        }
        currentTrack = null;
    }

    private void resetMusicThread() {
        musicThread.interrupt();
    }

    // Added null checks in case there are compiling or init errors.
    public void toggleVolume(String state) {
        if(state.equals("Sound Off")) {
            for(Sound sound : sounds) {
                if(sound != null) {
                    sound.turnOffVolume();
                }
            }
            if(currentTrack != null) {
                currentTrack.turnOffVolume();
            }
        }
        else if(state.equals("Sound On")) {
            for(Sound sound : sounds) {
                if(sound != null) {
                    sound.turnOnVolume();
                }
            }
            if(currentTrack != null) {
                currentTrack.turnOnVolume();
            }
        }
    }

    public void tick() {
        NonPlayerHelicopter.getInstance().invokeStrategy();
        move(getElapsedTimeInMillis());
        spawnNewFires();
        grow();
        burnBuilding();
        depleteFuel();
        checkIfWon();
        checkIfLost();
    }
}