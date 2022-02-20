package jade;

import org.joml.Vector2f;

public class Transform {
    /*******************************
                PROPS
     ******************************/
    public Vector2f position, scale;
    public float rotation;

    /*******************************
              CONSTRUCTOR
     ******************************/
    public Transform() {
        init(new Vector2f(), new Vector2f());
    }

    public Transform(Vector2f position) {
        init(position, new Vector2f());
    }

    public Transform(Vector2f position, Vector2f scale) {
        init(position, scale);
    }


    /*******************************
                METHODS
     ******************************/
    public void init(Vector2f position, Vector2f scale) {
        this.position = position;
        this.scale = scale;
    }
}
