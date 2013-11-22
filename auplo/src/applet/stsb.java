package applet;
public class stsb{
	protected String sts;
	private long t;
	public long updt=100;
	public stsb(){}
	public stsb(final long update_intervall_ms){this.updt=update_intervall_ms;}
	public final void sts(final String s){
		sts=s;
		final long tnow=System.currentTimeMillis();
		if(tnow-t>updt){
			t=tnow;
			flush();
		}
	}
	public void flush(){}
}