package applet;

import javax.swing.JApplet;

public class stsbjapplet extends stsb{
	private JApplet o;
	public stsbjapplet(final JApplet a){o=a;}
	public void flush(){o.showStatus(sts);}
}