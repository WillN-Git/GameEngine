package components;

import jade.Component;

public class FontRenderer extends Component {
    /*******************************
              CONSTRUCTOR
     ******************************/
    public FontRenderer() {
        gameObject = null;
    }


    /*******************************
                 METHODS
     ******************************/
    @Override
    public void start() {
        if(gameObject.getComponent(SpriteRenderer.class) != null) {
            System.out.println("Found font renderer !");
        }
    }

    @Override
    public void update(float dt) {

    }
}
