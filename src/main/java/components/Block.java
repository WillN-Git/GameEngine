package components;

import jade.Component;
import org.jbox2d.dynamics.contacts.Contact;
import jade.GameObject;
import org.joml.Vector2f;

public abstract class Block extends Component {

    private transient boolean bopGoingUp = true,
                              doBopAnimation = false;

    private transient Vector2f bopStart,
                               topBopLocation;

    private transient boolean active = true;

    public float bopSpeed = 0.4f;

    @Override
    public void start() {
        this.bopStart = new Vector2f(this.gameObject.transform.position);
        this.topBopLocation = new Vector2f(bopStart).add(0.0f, 0.02f);
    }

    @Override
    public void update(float dt) {
        if(doBopAnimation) {
            if(bopGoingUp) {
                if (this.gameObject.transform.position.y < topBopLocation.y) {
                    this.gameObject.transform.position.y += bopSpeed * dt;
                } else {
                    bopGoingUp = false;
                }
            } else {
                if(this.gameObject.transform.position.y > bopStart.y) {
                    this.gameObject.transform.position.y -= bopSpeed * dt;
                } else {
                    this.gameObject.transform.position.y = this.bopStart.y;
                    bopGoingUp = true;
                    doBopAnimation = false;
                }
            }
        }
    }

    @Override
    public void beginCollision(GameObject obj, Contact contact, Vector2f contactNormal) {
        // TODO : To be completed
    }

    public void setInactive() {
        this.active = false;
    }

    abstract void playerHit(PlayerController playerController);
}
