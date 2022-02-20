package util;

import renderer.Shader;
import renderer.Texture;

import java.util.HashMap;
import java.util.Map;

public class AssetPool {
    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> Texute = new HashMap<>();
    private static Map<String, Spritesheet> spritesheets = new HashMap<>();
    private static Map<String, Sound> sounds = new HashMap<>();

    public static Shader getShader(String resourceName) {

    }

    public static Texture getTexture(String resourceName) {

    }

    public static void addSpritesheet(String resourceName, Spritesheet spritesheet) {

    }

    public static Spritesheet getSpritesheet(String resourceName) {

    }

    public static Collection<Sound> getAllSounds() {
        return sounds.values();
    }

    public static Sound getSound(String soundFile) {

    }

    public static Sound addSound(String soundFile, boolean loops) {

    }
}
