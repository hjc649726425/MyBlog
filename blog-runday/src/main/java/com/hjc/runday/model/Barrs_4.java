package com.hjc.runday.model;

import com.hjc.runday.view.GameFrame;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.InputStream;
import java.util.Random;

public class Barrs_4 {//		鱼叉障碍物！
	private Image image;
	private Image images[];
	public static final int WIDTH =150;
	public static final int HEIGHT =350;
	private int x,y;

	public Barrs_4() {//构造方法
		Random random = new Random();
		images = new Image[4];
		try (
				InputStream input1 = new ClassPathResource("Image/11.png").getInputStream();
				InputStream input2 = new ClassPathResource("Image/12.png").getInputStream();
				InputStream input3 = new ClassPathResource("Image/13.png").getInputStream();
				InputStream input4 = new ClassPathResource("Image/14.png").getInputStream();
		) {
			images[0] = ImageIO.read(input1);
			images[1] = ImageIO.read(input2);
			images[2] = ImageIO.read(input2);
			images[3] = ImageIO.read(input2);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		image = images[random.nextInt(4)];
		x = GameFrame.WIDTH + 1500;
		y = 0;
	}
	public void step(){
		x-=20;
	}
	public void paintBarrs(Graphics g){
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
	public Image[] getImages() {
		return images;
	}
	public void setImages(Image[] images) {
		this.images = images;
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
	public static int getWidth() {
		return WIDTH;
	}
	public static int getHeight() {
		return HEIGHT;
	}

}
	