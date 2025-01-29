package handler;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    private boolean upPressed, downPressed, leftPressed, rightPressed, spacePressed;

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        setState(e.getKeyCode(), true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        setState(e.getKeyCode(), false);
    }

    private void setState(int keyCode, boolean pressed) {
        if (keyCode == KeyEvent.VK_W) {
            upPressed = pressed;
        } else if (keyCode == KeyEvent.VK_S) {
            downPressed = pressed;
        } else if (keyCode == KeyEvent.VK_A) {
            leftPressed = pressed;
        } else if (keyCode == KeyEvent.VK_D) {
            rightPressed = pressed;
        }

        if (keyCode == KeyEvent.VK_SPACE) {
            spacePressed = pressed;
        }
    }

    public boolean isUpPressed() {
        return upPressed;
    }

    public boolean isDownPressed() {
        return downPressed;
    }

    public boolean isLeftPressed() {
        return leftPressed;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    public boolean isSpacePressed() {
        return spacePressed;
    }
}
