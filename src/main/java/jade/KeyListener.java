package jade;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class KeyListener {
    /*******************************
                  PROPS
     ******************************/
    private static KeyListener instance;
    private boolean keyPressed[] = new boolean[350];

    /*******************************
              CONSTRUCTOR
     ******************************/
    private KeyListener() {

    }

    /*******************************
                METHODS
     ******************************/

    //==================== GETTERS
        public static KeyListener get() {
            if( instance == null )
                instance = new KeyListener();

            return instance;
        }

    //==================== CALLBACKS
    public static void keyCallback(long window, int key, int scanCode, int action, int mods) {
        if( action == GLFW_PRESS ) {
            get().keyPressed[key] = true;
        } else if( action == GLFW_RELEASE ) {
            get().keyPressed[key] = false;
        }
    }

    public static boolean isKeyPressed(int keyCode) {
        return get().keyPressed[keyCode];
    }
}
