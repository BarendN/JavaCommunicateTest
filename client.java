import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class client extends JFrame{
	
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String serverIP;
	private Socket connection;

	//Constructor
	public client(String host){
		super("ComboByte Messenger Client");
		serverIP = host;
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent event){
					sendMessage(event.getActionCommand());
					userText.setText("");
				}
			}
		);
		add(userText, BorderLayout.SOUTH);
		chatWindow = new JTextArea();
		Font font = new Font("Tahoma", Font.PLAIN, 12);
        	chatWindow.setFont(font);
        	chatWindow.setForeground(Color.BLUE);
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setSize(350, 500);
		setVisible(true);
	}

	//Connect to Server
	public void startRunning(){
		try{
			connectToServer();
			setupStreams();
			whileChatting();
		}catch(EOFException eofException){
			showMessage("\n* Client Terminated the connection. ");
		}catch(IOException ioException){
			ioException.printStackTrace();
		}finally{
			closeConnection();
		}
	}

	//Connect to server
	private void connectToServer() throws IOException{
		showMessage("\n* Attempting Connection.. ");
		connection = new Socket(InetAddress.getByName(serverIP), 6789);
		showMessage("\n* Connected to " + connection.getInetAddress().getHostName());
	}

	//Set up streams
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n* Streams are set up.");
	}

	//Chatting
	private void whileChatting() throws IOException{
		ableToType(true);
		do{
			try{
				message = (String) input.readObject();
				showMessage("\n" + message);
			}catch(ClassNotFoundException classNotFoundException){
				showMessage("\n I Did not understand the incoming message.");
			}
		}while(!message.equals("SERVER - END"));
	}

	//Close all the connections and streams
	private void closeConnection(){
		showMessage("\n* Closing things down.");
		ableToType(false);
		try{
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}

	//Send messages to server
	private void sendMessage(String message){
		try{
			output.writeObject("CLIENT - " + message);
			output.flush();
			showMessage("\nCLIENT - " + message);
		}catch(IOException ioException){
			chatWindow.append("\n* Message could not be sent");
		}
	} 

	//Show Message
	private void showMessage(final String text){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					chatWindow.append(text);
				}
			}
		);	
	}

	//Enable User type
	private void ableToType(final boolean tof){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					userText.setEditable(tof);
				}
			}
		);
	}

}
