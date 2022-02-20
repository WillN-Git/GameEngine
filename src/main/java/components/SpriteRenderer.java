package components;

import jade.Component;
import org.joml.Vector4f;

public class SpriteRenderer extends Component {
    /*******************************
                 PROPS
     ******************************/
    private Vector4f color;

    public SpriteRenderer(Vector4f color) {
        this.color = color;
    }

    /*******************************
                 METHODS
     ******************************/
    @Override
    public void start() {

    }

    @Override
    public void update(float dt) {

    }

    public Vector4f getColor() {
        return this.color;
    }


}
