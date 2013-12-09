package applet;
import javax.swing.*;
final public class stsbjapplet extends stsb{
	final private JApplet o;
	public stsbjapplet(final JApplet a){o=a;}
	public void flush(){o.showStatus(sts);}
}