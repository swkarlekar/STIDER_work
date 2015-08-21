//Written by Sweta Karlekar, August 2015, Thomas Jefferson High School

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class TwitterLookUpPanel extends JPanel {
	public String pQuery = "", pGeo ="", pLang="",pType="", pCount="", pUntil = "", pOutput="";
	public JTextField query, geocode, language, resultType, count, until, outputFile; 
	public TwitterLookUpPanel()
	{
		setLayout(new BorderLayout());
		
		JPanel subpanel = new JPanel();
			subpanel.setLayout(new GridLayout(8, 2));
			
			JLabel qlabel = new JLabel("*Input query: ");
			 query = new JTextField("");
			subpanel.add(qlabel);
			subpanel.add(query);
			
			JLabel qgeocode = new JLabel("Input geocode: (latitude, longitude, AND radius)");
			 geocode = new JTextField("");
			subpanel.add(qgeocode);
			subpanel.add(geocode);
			
			JLabel qlang = new JLabel("Input language: (two-letter code)");
			 language = new JTextField("");
			subpanel.add(qlang);
			subpanel.add(language);
			
			JLabel qtype = new JLabel("Input result type: (recent, popular, OR mixed)");
			 resultType = new JTextField("");
			subpanel.add(qtype);
			subpanel.add(resultType);
			
			JLabel qcount = new JLabel("Input tweet count: (up to 100)");
			 count = new JTextField("");
			subpanel.add(qcount);
			subpanel.add(count);
			
			JLabel quntil = new JLabel("Input latest retrival date: (YYYY-MM-DD)");
			 until = new JTextField("");
			subpanel.add(quntil);
			subpanel.add(until);
			
			JLabel qoutput = new JLabel("Input output file name: (___.txt)");
			 outputFile = new JTextField("");
			subpanel.add(qoutput);
			subpanel.add(outputFile);
			
			JLabel warning = new JLabel("*mandatory");
			subpanel.add(new JLabel("    "));
			subpanel.add(warning);
			
			JButton submit = new JButton("Submit");
			submit.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
					pQuery = query.getText().trim();
					pGeo = geocode.getText().trim();
					pLang = language.getText().trim(); 
					pType = resultType.getText().trim();
					pCount = count.getText().trim();
					pUntil = until.getText().trim();
					pOutput = outputFile.getText().trim();
					query.setText("");
					geocode.setText("");
					language.setText("");
					resultType.setText("");
					count.setText("");
					until.setText("");
					outputFile.setText("");
					try {
						TweetLookUp.callTwitter(pQuery, pGeo, pLang, pType, pCount, pUntil, pOutput);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
			submit.setPreferredSize(new Dimension(50, 20));
			add(submit, BorderLayout.SOUTH);
		
		add(new JLabel("\t\t\t      "), BorderLayout.EAST);
		add(new JLabel("\t\t\t      "), BorderLayout.WEST);
		add(subpanel, BorderLayout.CENTER);
	}
	

}
