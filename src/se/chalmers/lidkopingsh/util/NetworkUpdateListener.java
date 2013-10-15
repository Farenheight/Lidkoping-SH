package se.chalmers.lidkopingsh.util;

public interface NetworkUpdateListener {
	
	public void startUpdate();
	public void endUpdate();
	public void noNetwork(String message);
	
}
