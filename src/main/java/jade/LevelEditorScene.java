package jade;


import components.SpriteRenderer;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class LevelEditorScene extends Scene {
    /*******************************
                 PROPS
     ******************************/


    /*******************************
              CONSTRUCTOR
     ******************************/
    public LevelEditorScene() {

    }

    /*******************************
                METHODS
     ******************************/

    @Override
    public void init() {
        this.camera = new Camera(new Vector2f());

        int xOffset = 10, yOffset = 10;

        float totalWidth = (float)(600 - xOffset * 2),
              totalHeight = (float)(300 - yOffset * 2),
              sizeX = totalWidth / 100.0f,
              sizeY = totalHeight / 100.0f;

        float xPos, yPos;
        GameObject go;

        for(int x=0; x<100; x++) {
            for(int y=0; y<100; y++) {
                xPos = xOffset + (x * sizeX);
                yPos = yOffset +  (y * sizeY);

                go = new GameObject("Obj" + x + "" + y, new Transform(new Vector2f(xPos, yPos), new Vector2f(sizeX, sizeY)));
                go.addComponent(
                        new SpriteRenderer(
                            new Vector4f(
                                    xPos / totalWidth,
                                    yPos / totalHeight,
                                    1,
                                    1
                            )
                        )
                );

                this.addGameObjectToScene(go);
            }
        }
    }

    @Override
    public void update(float dt) {
        for(GameObject go : this.gameObjects) {
            go.update(dt);
        }

        this.renderer.render();
    }
}
