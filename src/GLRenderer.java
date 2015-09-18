import java.awt.Color;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GLRenderer {

	private GLRenderer() { 

	}

	public static final int BLEND_MODE_NORMAL = 0;
	public static final int BLEND_MODE_ADDATIVE = 1;

	private static float width;
	private static float height;
	private static float mouseXScale;
	private static float mouseYScale;

	public static void enterOrtho(int width, int height) {
		GLRenderer.width = width;
		GLRenderer.height = height;
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, width, 0, height, 0, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}

	// public static void enterPerspective(int width, int height) {
	// GLRenderer.width = width;
	// GLRenderer.height = height;
	// glMatrixMode(GL_PROJECTION);
	// glLoadIdentity();
	// gluPerspective(60.0f, ((float) width) / ((float) height), 0.1f, 100f);
	// glMatrixMode(GL_MODELVIEW);
	// }

	public static void setViewPort(int width, int height) {
		mouseXScale = GLRenderer.width / (float) width;
		mouseYScale = GLRenderer.height / (float) height;
		GL11.glViewport(0, 0, width, height);
	}

	public static int getMouseX() {
		return (int) (Mouse.getX() * mouseXScale);
	}

	public static int getMouseY() {
		return (int) (Mouse.getY() * mouseYScale);
	}

	public static void clear() {
		clear(0, 0, 0, 1);
	}

	public static void clear(float red, float green, float blue, float alpha) {
		GL11.glClearColor(red, green, blue, alpha);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
	}

	public static void setBlendMode(int blendMode) {
		GL11.glEnable(GL11.GL_BLEND);
		switch (blendMode) {
		case BLEND_MODE_NORMAL:
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			break;
		case BLEND_MODE_ADDATIVE:
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			break;
		default:
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			break;
		}
	}

	public static void setColor(float red, float green, float blue) {
		GL11.glColor3f(red, green, blue);
	}

	public static void setColor(float red, float green, float blue, float alpha) {
		GL11.glColor4f(red, green, blue, alpha);
	}

	public static void beginTexturedQuads(int glTexture) {
		GLTexture.bind(glTexture);
		GL11.glBegin(GL11.GL_QUADS);
	}

	public static void beginQuads() {
		GL11.glBegin(GL11.GL_QUADS);
	}

	public static void end() {
		GL11.glEnd();
	}

	public static void quad(float x, float y, float width, float height) {
		GL11.glVertex2f(x, y);
		GL11.glVertex2f(x, y + height);
		GL11.glVertex2f(x + width, y + height);
		GL11.glVertex2f(x + width, y);
	}

	public static void drawQuad(float x, float y, float width, float height,
			float red, float green, float blue, float alpha) {
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glColor4f(red, green, blue, alpha);
		quad(x, y, width, height);
		GL11.glEnd();
	}

	public static void centeredQuad(float x, float y, float halfWidth,
			float halfHeight, float z) {
		GL11.glVertex3f(x - halfWidth, y - halfHeight, z);
		GL11.glVertex3f(x - halfWidth, y + halfHeight, z);
		GL11.glVertex3f(x + halfWidth, y + halfHeight, z);
		GL11.glVertex3f(x + halfWidth, y - halfHeight, z);
	}

	public static void drawCenteredQuadZ(float x, float y, float halfWidth,
			float halfHeight, float z) {
		GL11.glBegin(GL11.GL_QUADS);
		centeredQuad(x, y, halfWidth, halfHeight, z);
		GL11.glEnd();
	}

	public static void drawCenteredQuadZ(Color color, float x, float y,
			float halfWidth, float halfHeight, float z) {
		GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(),
				color.getAlpha());
		GL11.glBegin(GL11.GL_QUADS);
		centeredQuad(x, y, halfWidth, halfHeight, z);
		GL11.glEnd();
	}

	public static void drawCenteredQuad(float x, float y, float halfWidth,
			float halfHeight, float angle) {
		GL11.glTranslatef(x, y, 0);
		GL11.glRotatef(angle, 0, 0, 1);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2f(-halfWidth, -halfHeight);
		GL11.glVertex2f(-halfWidth, halfHeight);
		GL11.glVertex2f(halfWidth, halfHeight);
		GL11.glVertex2f(halfWidth, -halfHeight);
		GL11.glEnd();
		GL11.glRotatef(-angle, 0, 0, 1);
		GL11.glTranslatef(-x, -y, 0);
	}

	public static void drawCenteredQuad(Color color, float x, float y,
			float halfWidth, float halfHeight, float angle) {
		GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(),
				color.getAlpha());
		drawCenteredQuad(x, y, halfWidth, halfHeight, angle);
	}

	public static void texturedQuad(float x, float y, float width, float height) {
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2f(x, y);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex2f(x, y + height);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex2f(x + width, y + height);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex2f(x + width, y);
	}

	public static void drawTexturedQuad(int glTexture, float x, float y,
			float width, float height) {
		GLTexture.bind(glTexture);
		GL11.glBegin(GL11.GL_QUADS);
		texturedQuad(x, y, width, height);
		GL11.glEnd();
	}

	public static void centeredTexturedQuad(float x, float y, float halfWidth,
			float halfHeight) {
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2f(x - halfWidth, y - halfHeight);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex2f(x - halfWidth, y + halfHeight);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex2f(x + halfWidth, y + halfHeight);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex2f(x + halfWidth, y - halfHeight);
	}

	public static void drawCenteredTexturedQuad(int glTexture, float x,
			float y, float halfWidth, float halfHeight) {
		GLTexture.bind(glTexture);
		GL11.glBegin(GL11.GL_QUADS);
		centeredTexturedQuad(x, y, halfWidth, halfHeight);
		GL11.glEnd();
	}

	public static void drawCenteredTexturedQuad(float x, float y,
			float halfWidth, float halfHeight, float angle) {
		GL11.glTranslatef(x, y, 0);
		GL11.glRotatef(angle, 0, 0, 1);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2f(-halfWidth, -halfHeight);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex2f(-halfWidth, halfHeight);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex2f(halfWidth, halfHeight);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex2f(halfWidth, -halfHeight);
		GL11.glEnd();
		GL11.glRotatef(-angle, 0, 0, 1);
		GL11.glTranslatef(-x, -y, 0);
	}

	public static void drawCenteredTexturedQuad(int glTexture, float x,
			float y, float halfWidth, float halfHeight, float angle) {
		GLTexture.bind(glTexture);
		drawCenteredTexturedQuad(x, y, halfWidth, halfHeight, angle);
	}

}
