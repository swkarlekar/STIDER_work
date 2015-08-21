//Written by Sweta Karlekar, August 2015, Thomas Jefferson High School

import javax.swing.JFrame;


public class TwitterLookUpDriver{

	public static void main(String[] args){
		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame frame = new JFrame("Twitter Search Tool");
		frame.setSize(820,300);
		frame.setLocation(0, 0);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		TwitterLookUpPanel panel = new TwitterLookUpPanel();
		frame.setContentPane(panel);
		frame.setVisible(true);
			}

}
