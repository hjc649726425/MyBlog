package com.hjc.runday.model;

import com.hjc.runday.view.GameFrame;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.InputStream;

public class Barrs_3 {// 		导弹！
	private Image image;
	private int x,y;
	public static final int WIDTH = 150;
	public static final int HEIGHT=70;
	private int speed;
	public Barrs_3() {
		try (InputStream inputStream = new ClassPathResource("Image/daodan.png")
				.getInputStream();) {
			image = ImageIO.read(inputStream);
		} catch (Exception e) {
			// TODO: handle exception
		}
		x = GameFrame.WIDTH + 1000;
		y = 450;
		speed = 25;
	}
	public void step(){
		x-=speed;
	}
	public void paintBarrs(Graphics g) {
		g.drawImage(image, x, y, WIDTH, HEIGHT, null);

	}
	public boolean outofBounds(){
		return this.x<=-WIDTH;
	}
	public Image getImage() {
		return image;
	}
	public void setImage(Image image) {
		this.image = image;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	public static int getWidth() {
		return WIDTH;
	}
	public static int getHeight() {
		return HEIGHT;
	}


}
