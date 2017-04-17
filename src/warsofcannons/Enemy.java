
package warsofcannons;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Enemy {
	BufferedImage image;

	//File fImage = new File("enemy1.png");
	File fImage1 = new File("C:\\Users\\Nahid\\Documents\\NetBeansProjects\\Cannons\\src\\warsofcannons\\images\\cannon1.png");
	int x;
	int y;
	boolean isAlive = true;
	double theta;
	AffineTransform at = new AffineTransform();

	public Enemy( int x ,int y) {
		try {
			image = ImageIO.read(fImage1);
		} catch (Exception e) {
		}
		if (image == null)
			image.createGraphics();

		this.x = x;
                this.y=y;
	}

	public BufferedImage getImage() {
		return image;
	}

	public Rectangle getBounds() {
		return new Rectangle(x, y, image.getWidth() - 5, image.getHeight() - 5);
	}

	public void draw(Graphics2D g2d) {
		at.setToIdentity();
		at.rotate(theta, x + 50, y + 40);
		at.translate(x, y);
		g2d.drawImage(image, at, null);
	}


}
