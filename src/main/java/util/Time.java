package util;

public class Time {
    /*******************************
                 PROPS
     ******************************/
    private static float timeStarted = System.nanoTime();

    /*******************************
                METHODS
     ******************************/
    public static float getTime() {
        return (float)((System.nanoTime() - timeStarted) * 1E-9);
    }
}
