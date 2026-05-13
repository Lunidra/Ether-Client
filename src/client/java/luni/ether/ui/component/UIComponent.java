package luni.ether.ui.component;

import luni.ether.ui.render.UIRenderer;

public abstract class UIComponent {

    protected float x, y;
    protected float width, height;

    protected boolean dragging;
    protected float dragOffsetX;
    protected float dragOffsetY;
    protected final String id;

    public UIComponent(String id, float x, float y, float width, float height) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public String getId() {
        return id;
    }

    public boolean isMovable() {
        return true;
    }

    public abstract void render(UIRenderer r, int mouseX, int mouseY);

    // --- dragging support (future-proof) ---
    public void mouseClicked(float mouseX, float mouseY, int button) {
        if (isHovered(mouseX, mouseY) && button == 0) {
            dragging = true;
            dragOffsetX = mouseX - x;
            dragOffsetY = mouseY - y;
        }
    }

    public void mouseReleased(int button) {
        if (button == 0) dragging = false;
    }

    public void mouseDragged(float mouseX, float mouseY) {
        if (dragging) {
            x = mouseX - dragOffsetX;
            y = mouseY - dragOffsetY;
        }
    }

    protected boolean isHovered(float mx, float my) {
        return mx >= x && mx <= x + width &&
                my >= y && my <= y + height;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}