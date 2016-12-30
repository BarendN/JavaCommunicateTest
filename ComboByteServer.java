import javax.swing.JFrame;

public class ComboByteServer{
	public static void main(String[] args){
		server sarah = new server();
		sarah.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		sarah.startRunning();
	}
}
