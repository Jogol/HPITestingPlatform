import java.awt.Canvas;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class Renderer {

	public File imageToShow;
	public int frames;
	public File file;
	public GLTexture texture;
	public boolean showing = false;
	public boolean timeToShow = false;
	public Canvas canvas;

	public void createContext(Canvas canvas) throws LWJGLException {
		this.canvas = canvas;
		Display.setVSyncEnabled(true);
		//this.canvas.setSize(new Dimension(800,600));
		Display.setParent(this.canvas);
		Display.create();
		
		
		GL11.glEnable(GL11.GL_BLEND);
		System.out.println(this.canvas.getSize());
		GL11.glClearColor(1, 1, 1, 1);
		GLTexture.enable();
	}

	public void showImage(File file, int frames)
			throws IllegalArgumentException, IOException {
		this.file = file;
		this.frames = frames;
		timeToShow = true;
	}

	public void start() throws IllegalArgumentException, IOException {
		System.out.println(canvas.getSize());
		
		int laps = -1;
		while (!Display.isCloseRequested()) {
			//canvas.setSize(new Dimension(800, 600));
			
			GLRenderer.enterOrtho(canvas.getWidth(), canvas.getHeight());
			GLRenderer.setViewPort(canvas.getWidth(), canvas.getHeight());
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			if (timeToShow && laps < frames) {
				if (laps == -1) {
					if (texture != null) {
						texture.delete();
					}
					texture = GLTexture.createObj(ImageIO.read(file),
							GLTexture.FILTER_LINEAR);
				} else {
					// if (!showing) {
					
					showing = true;
					texture.drawCentered(canvas.getWidth()/2, canvas.getHeight()/2, 400, 300, 0);
					// }
				}
				laps++;
			} else {
				laps = -1;
				showing = false;
				timeToShow = false;
			}
			Display.update();
			//Display.sync(60);
		}
	}

}
