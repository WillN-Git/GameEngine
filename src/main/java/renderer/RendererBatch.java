package renderer;

import components.SpriteRenderer;
import jade.GameObject;
import jade.Window;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RendererBatch {
    // Vertex
    // =======
    //Position          Color
    // float, float     float, float, float, float, float
    private final int POS_SIZE = 2, COLOR_SIZE = 4,
                      TEX_COORDS_SIZE = 2, TEX_ID_SIZE = 1,
                      ENTITY_ID_SIZE = 1;

    private final int POS_OFFSET = 0, COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES,
                      TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES,
                      TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_COORDS_SIZE * Float.BYTES,
                      ENTITY_ID_OFFSET = TEX_ID_OFFSET + TEX_ID_SIZE * Float.BYTES,
                      VERTEX_SIZE = 10,
                      VERTEX_SIZE_BYTES = VERTEX_SIZE + Float.BYTES;

    private SpriteRenderer[] sprites;
    private int numSprites;
    private boolean hasRoom;
    private float[] vertices;
    private int[] texSlots = {0, 1, 2, 3, 4, 5, 6, 7};

    private List<Texture> textures;
    private int vaoID, vboID;
    private int maxBatchSize, zIndex;

    private Renderer renderer;

    public RendererBatch(int maxBatchSize, int zIndex, Renderer renderer) {
        this.renderer = renderer;

        this.zIndex = zIndex;
        this.sprites = new SpriteRenderer[newBatchSize];
        this.maxBatchSize = maxBatchSize;

        // 4 vertices quads
            vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];

        this.numSprites = 0;
        this.hasRoom = true;
        this.textures = new ArrayList<>();
    }

    public void start() {
        // Generate and bind a vertex Object
            vaoID = glGenVertexArrays();
            glBindVertexArray(vaoID);

        // Allocate space for vertices
            vboID = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Create and upload indices buffer
            int eboID = glGenBuffers();
            int[] indices = generateIndices();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // Enable the buffer attribute pointers
            glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
            glEnableVertexAttribArray(0);

            glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
            glEnableVertexAttribArray(1);

            glVertexAttribPointer(2, TEX_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORDS_OFFSET);
            glEnableVertexAttribArray(2);

            glVertexAttribPointer(3, TEX_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET);
            glEnableVertexAttribArray(3);

            glVertexAttribPointer(4, ENTITY_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, ENTITY_ID_OFFSET);
            glEnableVertexAttribArray(4);
    }

    public void addSprite(SpriteRenderer spr) {
        // Get index and add renderObject
            int index = this.numSprites;

        this.sprites[index] = spr;
        this.numSprites++;

        if(spr.getTexture() != null) {
            if(!textures.contains(spr.getTexture())) {
                textures.add(spr.getTexture());
            }
        }

        // Add properties to local vertices array
            loadVertexProperties(index);

        if(numSprites >= this.maxBatchSize) {
            this.hasRoom = false;
        }
    }

    public void render() {
        boolean rebufferData = false;

        SpriteRenderer spr;

        for(int i=0; i<numSprites; i++) {
            spr = sprites[i];
            if(spr.isDirty()) {
                if(!hasTexture(spr.getTexture())) {
                    this.renderer.destroyGameObject(spr.gameObject);
                    this.renderer.add(spr.gameObject);
                } else {
                    loadVertexProperties(i);
                    spr.setClean();
                    rebufferData = true;
                }
            }

            // TODO : get better solution for this
            if(spr.gameObject.transform.zIndex != zIndex) {
                destroyIfExists(spr.gameObject);
                renderer.add(spr.gameObject);
                i--;
            }
        }

        if(rebufferData) {
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        }

        // Use shader
            Shader shader = Renderer.getBoundShader();
            shader.uploadMat4f("uProjection", Window.getScene().getCamera().getProjectionMatrix());
            shader.uploadMat4f("uView", Window.getScene().getCamera().getViewMatrix());

            for(int i=0; i<textures.size(); i++) {
                glActiveTexture(GL_TEXTURE0 + i + 1);
                textures.get(i).bind();
            }

        shader.uploadIntArray("uTextures", texSlots);

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, this.numSprites * 6, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        for(int i=0; i<textures.size(); i++) {
            textures.get(i).unbind();
        }

        shader.detach();
    }

    public boolean destroyIfExists(GameObject go) {
        SpriteRenderer sprite = go.getComponent(SpriteRenderer.class);

        for(int i=0; i < numSprites; i++) {
            if(sprites[i] == sprite) {
                for(int j=i; j<numSprites - 1; j++) {
                    sprites[j] = sprites[j + 1];
                    sprites[j].setDirty();
                }
                numSprites--;
                return true;
            }
        }

        return false;
    }

    private void loadVertexProperties(int index) {
        SpriteRenderer spr = this.sprites[index];

        // Find offset within array (4 vertices per sprite)
            int offset = index * 4 * VERTEX_SIZE;

            Vector4f color = spr.getColor();
            Vector2f[] texCoords = spr.getTexCoords();

        int texId = 0;
        if(spr.getTexture() != null) {
            for(int i=0; i<textures.size(); i++) {
                if(textures.get(i).equals(spr.getTexture())) {
                    texId = i + 1;
                    break;
                }
            }
        }

        boolean isRotated = spr.gameObject.transform.rotation != 0.0f;
        Matrix4f transformMatrix = new Matrix4f().identity();

        if(isRotated) {
            transformMatrix.translate(
                    spr.gameObject.transform.position.x,
                    spr.gameObject.transform.position.y,
                    0.0f
            );

            transformMatrix.rotate(
                    (float) Math.toRadians(spr.gameObject.transform.rotation),
                    0,
                    0,
                    1
            );

            transformMatrix.scale(
                    spr.gameObject.transform.scale.x,
                    spr.gameObject.transform.scale.y,
                    1
            );
        }

        // Add vertices with the correct properties
            float xAdd = 0.5f,
                  yAdd = 0.5f;

            Vector4f currentPos;

        for(int i=0; i<4; i++) {
            if(i == 1) {
                yAdd = -0.5f;
            } else if(i == 2) {
                xAdd = -0.5f;
            } else if(i == 3) {
                yAdd = 0.5f;
            }

            currentPos = new Vector4f(
                    spr.gameObject.transform.position.x + (xAdd * spr.gameObject.transform.scale.x),
                    spr.gameObject.transform.position.y + (yAdd * spr.gameObject.transform.scale.y),
                    0,
                    1
            );

            if(isRotated) {
                currentPos = new Vector4f(xAdd, yAdd, 0, 1).mul(transformMatrix);
            }

            // Load position
                vertices[offset] = currentPos.x;
                vertices[offset + 1] = currentPos.y;

            // Load color
                vertices[offset + 2] = color.x;
                vertices[offset + 3] = color.y;
                vertices[offset + 4] = color.z;
                vertices[offset + 5] = color.w;

            // Load texture coordinates
                vertices[offset + 6] = texCoords[i].x;
                vertices[offset + 7] = texCoords[i].y;

            // Load texture id
                vertices[offset + 8] = texId;

            // Load entity id
                vertices[offset + 9] = spr.gameObject.getUid() + 1;

            offset += VERTEX_SIZE;
        }
    }

    private int[] generateIndices() {
        // 6 indices per quad (3 per triangle)
            int[] elements = new int[maxBatchSize * 6];

        for(int i=0; i<maxBatchSize; i++)
            loadElementIndices(elements, i);

        return elements;
    }

    private void loadElementIndices(int[] elements, int index) {
        int offsetArrayIndex = 6 * index,
            offset = 4 * index;

        // 3, 2, 0, 0, 2, 1         7, 6, 4, 4, 6, 5
            // Triangle 1
                elements[offsetArrayIndex] = offset + 3;
                elements[offsetArrayIndex + 1] = offset + 2;
                elements[offsetArrayIndex + 2] = offset + 0;

            // Triangle 2
                elements[offsetArrayIndex + 3] = offset + 0;
                elements[offsetArrayIndex + 4] = offset + 2;
                elements[offsetArrayIndex + 5] = offset + 1;
    }

    public boolean hasRoom() {
        return this.hasRoom;
    }

    public boolean hasTextureRoom() {
        return this.textures.size() < 8;
    }

    public boolean hasTexture(Texture tex) {
        return this.textures.contains(tex);
    }

    public int zIndex() {
        return this.zIndex;
    }

    @Override
    public int compareTo(RendererBatch o) {
        return Integer.compare(this.zIndex, o.zIndex());
    }
}