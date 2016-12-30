import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ComboByteClient{
	public static void main(String[] args){
		String host = JOptionPane.showInputDialog("Enter the Host IP address: \nx.x.x.x");
		client charlie;
		charlie = new client(host);
		charlie.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		charlie.startRunning();
	}
}
