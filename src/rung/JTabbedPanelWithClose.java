package rung;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class JTabbedPanelWithClose extends JTabbedPane {

	private Map<Integer, String> tabTitle;
	private Map<String,Integer> FtabTitle;
	private Map<String, JTextArea> textAreaMap;

	public JTabbedPanelWithClose() {
	}

	public JTabbedPanelWithClose(int tabPlacement) {
		this.setTabPlacement(tabPlacement);
	}

	public JTabbedPanelWithClose(int tabPlacement, int tabLayoutPolicy) {
		this.setTabPlacement(tabPlacement);
		this.setTabLayoutPolicy(tabLayoutPolicy);
	}

	public void addTabWithClose(String title, Component component) {
		addTab(title, component);
		int index = indexOfComponent(component);
		setTabComponentAt(index, getJPanelWithClose(index, title));

	}

	private JPanel getJPanelWithClose(int index, String text) {
		JPanel jpanel = new JPanel();
		BorderLayout bl = new BorderLayout();
		bl.setHgap(9);
		jpanel.setLayout(bl);
		JLabel textJL = new JLabel(text);
		JLabel close = new JLabel("×");
		JLabel up = new JLabel("   ");
		up.setFont(new Font("楷体", Font.BOLD, 1));
		jpanel.add(up, BorderLayout.NORTH);
		close.setFont(new Font("黑体", Font.BOLD, 13));
		JTabbedPane jtp = this;
		close.setBackground(Color.RED);
		close.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				jtp.remove(index);
				tabTitle.remove(index);
				FtabTitle.remove(text);
				textAreaMap.remove(text);
			}

			public void mouseEntered(MouseEvent e) {
				setJLabel(close);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				setJLabel(close);
			}

			private void setJLabel(JLabel jlbael) {
				jlbael.setOpaque(!jlbael.isOpaque());
				jlbael.setText("");
				jlbael.setText("×");
			}
		});
		jpanel.add(textJL, BorderLayout.CENTER);
		jpanel.add(close, BorderLayout.EAST);
		jpanel.setBackground(new Color(0, 0, 0, 0));// 仅限于JDK7
		return jpanel;
	}

	public Map<Integer, String> getTabTitle() {
		return tabTitle;
	}

	public void setTabTitle(Map<Integer, String> tabTitle) {
		this.tabTitle = tabTitle;
	}

	public Map<String, JTextArea> getTextAreaMap() {
		return textAreaMap;
	}

	public void setTextAreaMap(Map<String, JTextArea> textAreaMap) {
		this.textAreaMap = textAreaMap;
	}

	public Map<String, Integer> getFtabTitle() {
		return FtabTitle;
	}

	public void setFtabTitle(Map<String, Integer> ftabTitle) {
		FtabTitle = ftabTitle;
	}
	

}
