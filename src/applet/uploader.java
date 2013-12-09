package applet;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
public final class uploader{
	public static void main(final String[]a)throws Throwable{
		if(a.length<5){
			System.out.println("try: java "+upload.class.getName()+" localhost 8888 aaaa-110307-064915.110-cb48e04f upload file.txt q");
			return;
		}
		final boolean silent=a.length>5&&"q".equals(a[5]);
		final uploader upl=new uploader();
		upl.perfstart();
		try{upl.send(a[0],Integer.parseInt(a[1]),a[2],a[3],new File(a[4]),null,silent?new stsb():new stsbarcon());}catch(final Throwable t){t.printStackTrace();System.exit(1);}
		upl.perfstop();
		if(!silent)System.out.println("\ndone in "+upl.dt+" ms   "+upl.kbps+" kB/s");
	}
	private boolean cancel;
	public int totalbytes;
    public int totalfiles;
	public int kbps;
	private long t0;
	public long dt;
	public void perfstart(){
		t0=System.currentTimeMillis();
		totalbytes=0;
	}
	public void perfstop(){
		dt=System.currentTimeMillis()-t0;
		kbps=(int)(totalbytes/dt);
	}
	private Map<String,Socket>socks=new HashMap<String,Socket>();
	private synchronized Socket sock(final String host,final int port)throws UnknownHostException, IOException{
		final String key=host+":"+port;
		Socket sock=socks.get(key);
		if(sock==null){
			sock=new Socket(host, port);
			socks.put(key,sock);
		}
		if(!sock.isConnected())
			throw new Error();
		return sock;
	}
	public void cancel(final boolean b){
		cancel=b;
		if(b)
			synchronized(socks){socks.clear();}
	}
	public boolean cancelled(){return cancel;}
	private DecimalFormat dcf=new DecimalFormat("### ### ### ###");
	public synchronized void send(final String host,final int port,final String session,final String dir,final File fl,final String md5sum,final stsb pb)throws IOException{
		if(cancel)return;
		final String md5=md5sum==null?"-":md5sum;
		final SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd--HH:mm:ss.SSS");
		final Socket sock=sock(host,port);
		final OutputStream os=sock.getOutputStream();
		if(fl.isDirectory()){
			os.write(("POST "+URLEncoder.encode(dir+"/"+fl.getName(),"UTF-8")+" HTTP/1.1\r\n").getBytes());
			os.write(("Host: "+host+"\r\n").getBytes());
			os.write(("Connection: Keep-Alive\r\n").getBytes());
			os.write(("Cookie: i="+session+"\r\n").getBytes());
			final String dateupd=df.format(fl.lastModified());
			os.write(("Content-Type: dir;"+dateupd+"\r\n").getBytes());
			os.write(("Content-Length: 0\r\n\r\n").getBytes());
            for(final File f:fl.listFiles())
                send(host,port,session,dir+"/"+fl.getName(),f,md5,pb);
			return;
		}
		final long filelen=fl.length();
		final FileInputStream is=new FileInputStream(fl);
		os.write(("POST "+URLEncoder.encode(dir+"/"+fl.getName(),"UTF-8")+" HTTP/1.1\r\n").getBytes());
		os.write(("Host: "+host+"\r\n").getBytes());
		os.write(("Connection: Keep-Alive\r\n").getBytes());
		os.write(("Cookie: i="+session+"\r\n").getBytes());
		final String dateupd=df.format(fl.lastModified());
		os.write(("Content-Type: file;"+dateupd+"\r\n").getBytes());
		os.write(("Content-Length: "+fl.length()+"\r\n").getBytes());
		os.write("\r\n".getBytes());
		long n=0;
		final byte[]buf=new byte[1024*1024];//? bigbuf
        totalfiles++;
		while(true){
			if(cancel)break;
			final int count=is.read(buf);
			if(count==-1)break;
            if(count==0){
                System.out.println("count0");
                continue;
            }
			os.write(buf,0,count);
			n+=count;
			totalbytes+=count;
			pb.sts(totalfiles+" files, "+(totalbytes>>10)+" KB  "+dcf.format(n*100/filelen)+" %  "+fl.getName()+"    "+dcf.format(n)+"/"+dcf.format(filelen)+" bytes");
        }
		is.close();
        pb.flush();
	}
}