package com.hjc.runday.model;

import com.hjc.runday.view.GameFrame;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.InputStream;

public class Barrs_1 {
	private Image image;
	private Image [] images;
	public static final int WIDTH=100;
	public static final int HEIGHT=110;
	private int x,y;
	int  index;
	private int speed;

	public Barrs_1() {//		螃蟹！
		images = new Image[2];
		try (
				InputStream input1 = new ClassPathResource("Image/a2.png").getInputStream();
				InputStream input2 = new ClassPathResource("Image/a4.png").getInputStream();
				){
			images[0]=ImageIO.read(input1);
			images[1]=ImageIO.read(input2);
		} catch (Exception e) {
			// TODO: handle exception
		}
		image = images[0];
		x=GameFrame.WIDTH+100;
		y=580;
		speed =30;
		index = 0;
	}

	public  void step() {//切换图片
		image =images[index++/5%images.length];
		x-=speed;//切换图片实现螃蟹爪子张合的动态效果的同时，使其向左移动
	}
	public void paintBarrs(Graphics g) {
		g.drawImage(image, x,y,WIDTH,HEIGHT, null);
	}
	public boolean outofBounds(){
		return this.x <=-WIDTH;
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
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
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
