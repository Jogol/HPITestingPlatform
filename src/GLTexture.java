
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.imageio.ImageIO;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

/**
 * A class for simple static creation and handling of GL textures. This class
 * keeps no reference to the textures created but they will be kept in video
 * memory until deleted by the user. A texture is referenced by an integer id.
 * The reference id is used to handle the texture against the GL context.
 * <p>
 * Instances of GLTexture can represent a squared texture of any size. The
 * texture is created and adjusted in size so that it contains an non scaled
 * version of the specified pixel data. The class has four pairs of floating
 * point UV texture coordinates (<code>uv_x, uv_y</code>). These point out the
 * four corners of the specified pixel data in the texture. The GL texture
 * itself may be much larger than the specified pixel data. This is the case if
 * only power-of-two-textures are allowed and the specified pixel data does not
 * have a width and height that are both powers of two.
 * 
 * @author Erik Borgstrom
 * 
 */
public final class GLTexture {

	/**
	 * The x coordinate of the 1st (top left) texture coordinate.
	 */
	public final float uv_x1;

	/**
	 * The y coordinate of the 1st (top left) texture coordinate.
	 */
	public final float uv_y1;

	/**
	 * The x coordinate of the 2nd (top right) texture coordinate.
	 */
	public final float uv_x2;

	/**
	 * The y coordinate of the 2nd (top right) texture coordinate.
	 */
	public final float uv_y2;

	/**
	 * The x coordinate of the 3rd (bottom right) texture coordinate.
	 */
	public final float uv_x3;

	/**
	 * The y coordinate of the 3rd (bottom right) texture coordinate.
	 */
	public final float uv_y3;

	/**
	 * The x coordinate of the 4th (bottom left) texture coordinate.
	 */
	public final float uv_x4;

	/**
	 * The y coordinate of the 4th (bottom left) texture coordinate.
	 */
	public final float uv_y4;

	public final float scaleX;
	public final float scaleY;

	public final int glTexture;

	private GLTexture(int glTexture, float offsetX, float offsetY,
			float scaleX, float scaleY) {
		this.glTexture = glTexture;
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.uv_x1 = 0;
		this.uv_y1 = scaleY;
		this.uv_x2 = scaleX;
		this.uv_y2 = scaleY;
		this.uv_x3 = scaleX;
		this.uv_y3 = 0;
		this.uv_x4 = 0;
		this.uv_y4 = 0;
	}

	public void bind() {
		GLTexture.bind(glTexture);
	}

	public void delete() {
		GLTexture.delete(glTexture);
	}

	public float getXCoord(float x) {
		return x * scaleX;
	}

	public float getYCoord(float y) {
		return y * scaleY;
	}

	public void adjust(float[] coords) {
		assert (coords.length % 2 == 0);
		if (coords == null) {
			return;
		}
		int length = coords.length;
		for (int i = 0; i < length; i += 2) {
			float x = coords[i];
			float y = coords[i + 1];
			coords[i] = x * uv_x2;
			coords[i + 1] = y * uv_y2;
		}
	}

	/**
	 * Represents no texture.
	 */
	public static final int NO_TEXTURE = 0;

	/**
	 * Specifies a linear filtering technique that will scale the texture
	 * smoothly.
	 */
	public static final int FILTER_LINEAR = 1;

	/**
	 * Specifies a nearest neighbor filtering technique that will make the
	 * texture pixelated when enlarged.
	 */
	public static final int FILTER_NEAREST = 2;

	/**
	 * Specifies whether or not only textures that has a width and height that
	 * are powers of two should be allowed.
	 */
	public static boolean forcePowerOfTwo = false;

	public static GLTexture createObj(BufferedImage data, int filter)
			throws IllegalArgumentException {
		return createObj(data, 0, filter);
	}

	public static GLTexture createObj(BufferedImage data, int margin, int filter)
			throws IllegalArgumentException {
		int dataWidth = data.getWidth();
		int dataHeight = data.getHeight();
		if (margin < 0 || margin > dataWidth || margin > dataHeight) {
			throw new IllegalArgumentException(
					"margin < 0 || margin > data.getWidth() || margin > data.getHeight()");
		}
		int width = dataWidth;
		int height = dataHeight;
		if (forcePowerOfTwo) {
			width = toPowerOfTwo(dataWidth);
			height = toPowerOfTwo(dataHeight);
		}
		int glTexture = create(data, width, height, filter);
		float uv_x3 = (float) (dataWidth - margin) / (float) width;
		float uv_y3 = (float) (dataHeight - margin) / (float) height;
		return new GLTexture(glTexture, 0, 0, uv_x3, uv_y3);
	}

	public static GLTexture createObj(int width, int height, int filter)
			throws IllegalArgumentException {
		int textureWidth = width;
		int testureHeight = height;
		if (forcePowerOfTwo) {
			textureWidth = toPowerOfTwo(width);
			testureHeight = toPowerOfTwo(height);
		}
		int glTexture = create(textureWidth, testureHeight, filter);
		float uv_x3 = (float) width / (float) textureWidth;
		float uv_y3 = (float) height / (float) testureHeight;
		return new GLTexture(glTexture, 0, 0, uv_x3, uv_y3);
	}

	/**
	 * Create a new GL texture from the data contained in the specified
	 * <code>BufferedImage</code>. The texture will be created with the
	 * specified width and height. The data will be placed in the top left
	 * corner of the texture. The width or height of the texture can not be less
	 * than the width or height of the data. The <code>filter</code> argument
	 * specifies the filter used by OpenGL when rendering the texture. It can be
	 * one of {@link #FILTER_LINEAR} and {@link #FILTER_NEAREST}.
	 * 
	 * @param data
	 *            The pixel data of the texture
	 * @param width
	 *            The width of the texture
	 * @param height
	 *            The height of the texture
	 * @param filter
	 *            The filter of the texture
	 * @return A reference to the texture
	 * 
	 * @throws IllegalArgumentException
	 *             If <code>data</code> is <code>null</code>
	 * @throws IllegalArgumentException
	 *             If {@link #forcePowerOfTwo} is <code>true</code> and
	 *             <code>width</code> and <code>height</code> are not both
	 *             powers of two
	 */
	public static int create(BufferedImage data, int width, int height,
			int filter) throws IllegalArgumentException {
		checkDimesions(width, height);
		return createFromImage(data, width, height, filter);
	}

	/**
	 * Create a new empty GL texture. The texture will be created with the
	 * specified width and height. The <code>filter</code> argument specifies
	 * the filter used by OpenGL when rendering the texture. It can be one of
	 * {@link #FILTER_LINEAR} and {@link #FILTER_NEAREST}.
	 * 
	 * @param width
	 *            The width of the texture
	 * @param height
	 *            The height of the texture
	 * @param filter
	 *            The filter of the texture
	 * @return A reference to the texture
	 * 
	 * @throws IllegalArgumentException
	 *             If {@link #forcePowerOfTwo} is <code>true</code> and
	 *             <code>width</code> and <code>height</code> are not both
	 *             powers of two
	 */
	public static int create(int width, int height, int filter)
			throws IllegalArgumentException {
		checkDimesions(width, height);
		return createFromBuffer(null, width, height, filter);
	}

	private static void checkDimesions(int width, int height) {
		if (forcePowerOfTwo) {
			if (!isPowerOfTwo(width) || !isPowerOfTwo(height)) {
				throw new IllegalArgumentException(
						"width and height must be a power of two");
			}
		}
	}

	private static int createFromImage(BufferedImage data, int width,
			int height, int filter) throws IllegalArgumentException {
		if (data == null) {
			throw new IllegalArgumentException("data == null");
		}
		int dataWidth = data.getWidth();
		int dataHeight = data.getHeight();
		if (width < dataWidth || height < dataHeight) {
			throw new IllegalArgumentException(
					"width < data.getWidth() || height < data.getHeight()");
		}
		int[] imgData = data
				.getRGB(0, 0, dataWidth, dataHeight, null, 0, width);
		byte[] byteData = new byte[width * height * 4];
		int length = imgData.length;
		for (int i = 0; i < length; i++) {
			int index = i * 4;
			byteData[index + 0] = (byte) (imgData[i] >> 16 & 0xff);
			byteData[index + 1] = (byte) (imgData[i] >> 8 & 0xff);
			byteData[index + 2] = (byte) (imgData[i] & 0xff);
			byteData[index + 3] = (byte) (imgData[i] >> 24 & 0xff);
		}
		ByteBuffer dataBuffer = ByteBuffer.allocateDirect(byteData.length);
		dataBuffer.order(ByteOrder.nativeOrder());
		dataBuffer.put(byteData);
		dataBuffer.flip();
		return createFromBuffer(dataBuffer, width, height, filter);
	}

	private static int createFromBuffer(ByteBuffer buf, int width, int height,
			int filter) {
		int currentBinding = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
		int glTexture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, glTexture);
		switch (filter) {
		case FILTER_LINEAR:
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
					GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
					GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			break;
		case FILTER_NEAREST:
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
					GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
					GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			break;
		default:
			System.err.println("Invalid texture filter \"" + filter + "\"");
			break;
		}
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height,
				0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, currentBinding);
		return glTexture;
	}

	/**
	 * Tests whether or not the specified value is a power of two.
	 * 
	 * @param value
	 *            The value to test
	 * @return <code>true</code> if the specified value is a power of two else
	 *         <code>false</code>
	 */
	public static boolean isPowerOfTwo(int value) {
		return (value & -value) == value;
	}

	public static int toPowerOfTwo(int value) {
		int i = value;
		while ((i & -i) != i) {
			i++;
		}
		return i;
	}

	/**
	 * Enables textures in the GL context.
	 */
	public static void enable() {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
	}

	/**
	 * Disables textures in the GL context.
	 */
	public static void disable() {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}

	/**
	 * Deletes the specified texture from video memory.
	 * 
	 * @param glTexture
	 *            The texture to delete
	 */
	public static void delete(int glTexture) {
		GL11.glDeleteTextures(glTexture);
	}

	/**
	 * Gets the texture currently bound.
	 * 
	 * @return The texture currently bound
	 */
	public static int getCurrentBinding() {
		return GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
	}

	/**
	 * Binds the specified texture.
	 * 
	 * @param glTexture
	 *            The texture to bind
	 */
	public static void bind(int glTexture) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, glTexture);
	}

	/**
	 * Sets the filter of the specified texture. The filter can be one of
	 * {@link #FILTER_LINEAR} and {@link #FILTER_NEAREST}.
	 * 
	 * @param glTexture
	 *            The texture which filter to set
	 * @param filter
	 *            the filter to set
	 */
	public static void setFilter(int glTexture, int filter) {
		int currentBind = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
		if (glTexture != currentBind) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, glTexture);
		}
		switch (filter) {
		case FILTER_LINEAR:
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
					GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
					GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			break;
		case FILTER_NEAREST:
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
					GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
					GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			break;
		default:
			System.err.println("Invalid texture filter \"" + filter + "\"");
		}
		if (currentBind != glTexture) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, currentBind);
		}
	}

	private static float offsetX = 0;
	private static float offsetY = 0;

	public static void main(String[] args) throws LWJGLException, IOException {
		int width = 1920;
		int height = 1080;
		// int frameWidth = width / 8;
		// int frameHeight = height / 8;
		// Display.setDisplayMode(new DisplayMode(width, height));
		Display.setFullscreen(true);
		Display.create();

		GLTexture bg1 = GLTexture
				.createObj(ImageIO.read(new File(
						"C:/Users/Erik Holm/Pictures/game1/bg1.png")), 2,
						FILTER_LINEAR);
		GLTexture bg2 = GLTexture
				.createObj(ImageIO.read(new File(
						"C:/Users/Erik Holm/Pictures/game1/bg2.png")), 2,
						FILTER_LINEAR);
		GLTexture bg3 = GLTexture
				.createObj(ImageIO.read(new File(
						"C:/Users/Erik Holm/Pictures/game1/bg3.png")), 2,
						FILTER_LINEAR);
		GLTexture bg4 = GLTexture
				.createObj(ImageIO.read(new File(
						"C:/Users/Erik Holm/Pictures/game1/bg4.png")), 2,
						FILTER_LINEAR);
		GLTexture ground = GLTexture.createObj(ImageIO.read(new File(
				"C:/Users/Erik Holm/Pictures/game1/ground.png")), 2,
				FILTER_LINEAR);
		GLTexture border = GLTexture.createObj(ImageIO.read(new File(
				"C:/Users/Erik Holm/Pictures/game1/border.png")), 2,
				FILTER_LINEAR);

		// GLTexture backTexture = GLTexture.createObj(frameWidth, frameHeight,
		// FILTER_LINEAR);

		// int fb = GL30.glGenFramebuffers();
		// GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fb);
		// GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER,
		// GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D,
		// backTexture.glTexture, 0);

		while (!Display.isCloseRequested()) {
			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				break;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
				offsetX -= 2f;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
				offsetX += 2f;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
				offsetY -= 2f;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
				offsetY += 2f;
			}
			// GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fb);
			GL11.glClearColor(0, 0, 0, 1);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

			// GLRenderer.enterOrtho(frameWidth, frameHeight);
			// GLRenderer.setViewPort(frameWidth, frameHeight);

			// GLTexture.enable();
			// texture.bind();

			// GL11.glBegin(GL11.GL_QUADS);
			// GL11.glTexCoord2f(texture.uv_x1, texture.uv_y1);
			// GL11.glVertex2f(0, 0);
			// GL11.glTexCoord2f(texture.uv_x2, texture.uv_y2);
			// GL11.glVertex2f(frameWidth, 0);
			// GL11.glTexCoord2f(texture.uv_x3, texture.uv_y3);
			// GL11.glVertex2f(frameWidth, frameHeight);
			// GL11.glTexCoord2f(texture.uv_x4, texture.uv_y4);
			// GL11.glVertex2f(0, frameHeight);
			// GL11.glEnd();

			// GLTexture.disable();
			//
			// GL11.glBegin(GL11.GL_QUADS);
			// GL11.glColor3f(1, 0, 0);
			// GL11.glVertex2f(10, 10);
			// GL11.glColor3f(0, 0, 1);
			// GL11.glVertex2f(30, 10);
			// GL11.glColor3f(0, 1, 0);
			// GL11.glVertex2f(30, 30);
			// GL11.glColor3f(1, 1, 0);
			// GL11.glVertex2f(10, 30);
			// GL11.glEnd();
			//
			// GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
			//
			// GLRenderer.enterOrtho(width, height);
			// GLRenderer.setViewPort(width, height);
			//
			// GLTexture.enable();
			// backTexture.bind();

			GLRenderer.enterOrtho(width, height);
			GLRenderer.setViewPort(width, height);

			// GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_SMOOTH);

			GLTexture.disable();

			// GLRenderer.drawCenteredQuadZ(new Color(Color.RED), 200, 200, 10,
			// 10, 1);
			// GLRenderer.drawCenteredQuadZ(new Color(Color.BLUE), 210, 210, 10,
			// 10, 2);
			// GLRenderer.drawCenteredQuadZ(new Color(Color.GREEN), 220, 220,
			// 10,
			// 10, 3);

			GLTexture.enable();
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glColor4f(1, 1, 1, 1);

			GL11.glLoadIdentity();
			bg1.draw(0, 0, width, height, 0);

			GL11.glTranslatef(offsetX / 2f, offsetY / 2f, 0);
			bg2.draw(0, 0, width, height, 0);

			GL11.glLoadIdentity();
			GL11.glTranslatef(offsetX / 1.75f, offsetY / 1.75f, 0);
			bg3.draw(0, 0, width, height, 0);

			GL11.glLoadIdentity();
			GL11.glTranslatef(offsetX / 1.5f, offsetY / 1.5f, 0);
			bg4.draw(0, 0, width, height, 0);

			GL11.glLoadIdentity();
			GL11.glTranslatef(offsetX, offsetY, 0);
			ground.draw(0, 0, width, height, 0);

			GL11.glLoadIdentity();
			border.draw(0, 0, width, height, 0);

			Display.update();
			Display.sync(60);
		}
		// backTexture.delete();
		bg1.delete();
		bg2.delete();
		bg3.delete();
		bg4.delete();
		ground.delete();
		border.delete();
	}

	public void draw(float x, float y, float width, float height, float z) {
		draw(x, y, x + width, y, x + width, y + height, x, y + height, z);
	}

	public void drawCentered(float x, float y, float halfWidth,
			float halfHeight, float z) {
		draw(x - halfWidth, y - halfHeight, x + halfWidth, y - halfHeight, x
				+ halfWidth, y + halfHeight, x - halfWidth, y + halfHeight, z);
	}

	private void draw(float x1, float y1, float x2, float y2, float x3,
			float y3, float x4, float y4, float z) {
		bind();
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(uv_x1, uv_y1);
		GL11.glVertex3f(x1, y1, z);
		GL11.glTexCoord2f(uv_x2, uv_y2);
		GL11.glVertex3f(x2, y2, z);
		GL11.glTexCoord2f(uv_x3, uv_y3);
		GL11.glVertex3f(x3, y3, z);
		GL11.glTexCoord2f(uv_x4, uv_y4);
		GL11.glVertex3f(x4, y4, z);
		GL11.glEnd();
	}

}
