package applet;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JApplet;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
final public class upload extends JApplet{static final long serialVersionUID=1;
	public String host;
	public String port;
	public String sesid;
	public String rootpath="";
	public stsb sts=new stsbjapplet(this);
	private JFileChooser flc=new JFileChooser();
	private final uploader upl=new uploader();
	{	flc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		flc.setMultiSelectionEnabled(true);
		flc.setBounds(0,0,getWidth(),getHeight());
		flc.setApproveButtonText("Upload");
		flc.setCurrentDirectory(new File(rootpath));
		setContentPane(flc);
		addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent e){
				final int wi=getWidth();
				final int hi=getHeight();
				flc.setBounds(0,0,wi,hi);
			}
			public void componentHidden(ComponentEvent e){}
			public void componentMoved(ComponentEvent e){}
			public void componentShown(ComponentEvent e){}
		});
		flc.addActionListener(new ActionListener(){public void actionPerformed(final ActionEvent e){
			if (JFileChooser.APPROVE_SELECTION.equals(e.getActionCommand())){
				upl.cancel(false);
				upl.perfstart();
				new Thread(new Runnable(){public void run(){try{
					for(final File f:flc.getSelectedFiles()){
						if(upl.cancelled())
							break;
						upl.send(host,Integer.parseInt(port),sesid,rootpath,f,"-",sts);
						upl.perfstop();
						sts.sts(f.getName()+": "+(upl.cancelled()?"cancelled":"done")+"   "+upl.kbps+" kB/s   "+upl.dt+" ms");
						sts.flush();
					}
				}catch(final IOException e1){
					sts.sts(e1.getMessage());
					sts.flush();
				}}}).start();
		}else if(JFileChooser.CANCEL_SELECTION.equals(e.getActionCommand())){
				upl.cancel(true);
				sts.sts("cancelled");
				sts.flush();
			}
		}});
	}
	public void init(){
		host=getParameter("host");
		port=getParameter("port");
		sesid=getParameter("session");
		if(sesid==null)sesid="";
		rootpath=getParameter("rootpath");
		if(rootpath==null)rootpath="";
	}
	public static void main(String[]a)throws NumberFormatException,IOException{
		if(a.length<4){
			System.out.println("i.e. java "+upload.class.getName()+" localhost 8888 \"\" aaaa-sessionid");
			return;
		}
		final upload upl=new upload();
		upl.host=a[0];
		upl.port=a[1];
		upl.sesid=a[2];
		upl.rootpath=a[3];
		final JFrame frame=new JFrame("upload");
		upl.sts=new stsbjframe(frame);
		frame.setVisible(true);
		frame.setContentPane(upl);		
		frame.setSize(400,640);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
