package com.hjc.runday.view;

import com.hjc.runday.model.Person;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.InputStream;


public class EndFrame extends JFrame implements MouseListener {
	//创建继续游戏按钮、返回主菜单按钮、退出按钮 组件
	JLabel again,back,exit;

	public EndFrame(Person person) {
		try (
				InputStream inputAgain = new ClassPathResource("Image/hh5.png").getInputStream();
				InputStream inputback = new ClassPathResource("Image/hh6.png").getInputStream();
				InputStream inputExit = new ClassPathResource("Image/hh3.png").getInputStream();
				InputStream input115 = new ClassPathResource("Image/115.png").getInputStream();
				) {

			again = new JLabel(new ImageIcon(ImageIO.read(inputAgain)));
			again.setBounds(520, 622, 60, 25);
			again.addMouseListener(this);
			this.add(again);
			back = new JLabel(new ImageIcon(ImageIO.read(inputback)));
			back.setBounds(520, 722, 60, 25);
			back.addMouseListener(this);
			this.add(back);
			exit = new JLabel(new ImageIcon(ImageIO.read(inputExit)));
			exit.setBounds(520, 822, 60, 25);
			exit.addMouseListener(this);
			this.add(exit);

			EndPanel end = new EndPanel(person);
			this.add(end);//将结束面板组件添加到结束窗口上

			this.setSize(1500, 900);
			this.setLocationRelativeTo(null);
			this.setUndecorated(true);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.setIconImage(new ImageIcon(ImageIO.read(input115)).getImage());
			this.setVisible(true);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		//new EndFrame();
	}
	class EndPanel extends JPanel{
		Image background;
		Person p;
		public EndPanel(Person person) {//类比int a
			this.p = person;//创建对象、传值
			try (
					InputStream inputStream = new ClassPathResource("Image/chou.png")
							.getInputStream();
			){
				background = ImageIO.read(inputStream);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		@Override
		public void paint(Graphics g) {
			// TODO Auto-generated method stub
			super.paint(g);
			g.drawImage(background, 0, 0,1500,900 ,null);
			g.setColor(Color.CYAN);
			g.setFont(new Font("宋体",Font.BOLD,30));
			g.drawString(p.getScore()+"",1110,705);// + ” “ 属实妙
			g.drawString(p.getDistance() + " ", 1110, 622);

			g.setFont(new Font("宋体",Font.BOLD,50));
			g.setColor(Color.ORANGE);
			g.drawString(p.getTotalScore() + "", 1075, 500);
		}
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getSource().equals(again)){
			//跳转到下一界面
			new WindowFrame().Start();
			//关闭当前界面
			dispose();
		}	else if(e.getSource().equals(back)){
			new MainFrame();
			dispose();
		}else if(e.getSource().equals(exit)){
			System.exit(0);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
