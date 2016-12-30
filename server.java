import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class server extends JFrame{
	
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;

	//Constructor
	public server(){
		super("Bennie's Chat");
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
		add(new JScrollPane(chatWindow));
		setSize(350, 500);
		setVisible(true);
	}

	//set up and run the server
	public void startRunning(){
		try{
			server = new ServerSocket(0);
			while(true){
				try{
					//Connect and have conversation.
					waitForConnection();
					setupStreams();
					whileChatting();
				}catch(EOFException eofException){
					showMessage("\n* Server ended the connection.");
				}finally{
					closeCrap();
				}
			}
		}catch(IOException ioException){
			ioException.printStackTrace();

		}
	}

	//Wait for connection, then display connection info.
	private void waitForConnection() throws IOException{
		showMessage("\n* Waiting For someone to connect...");
		connection = server.accept();
		showMessage("\n* Now Connected to " + connection.getInetAddress().getHostName());
	}

	//Get Stream to send and recieve data;
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();	//Housekeeping
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n* Streams are now set up.");
	}

	//During the conversation
	private void whileChatting() throws IOException{
		String message = "\n* You are now Connected.";
		showMessage(message);
		ableToType(true);
		do{
			//have conversation
			try{
				message = (String) input.readObject();
				showMessage("\n" + message);
			}catch(ClassNotFoundException classNotFoundException){
				showMessage(" \nError: Unknown Object Was Delivered.");
			}
		}while(!message.equals("CLIENT - END"));
	}

	//close Streams and sockets
	private void closeCrap(){
		showMessage("\n* Closing connections...");
		ableToType(false);
		try{
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}

	//Send a message to client
	private void sendMessage(String message){
		try{
			output.writeObject("SERVER - " + message);
			output.flush();
			showMessage("\nSERVER - " + message);
		}catch(IOException ioException){
			chatWindow.append("\n* Error: Message can't be sent.");
		}
	}

	//Show Messages in main chat window
	private void showMessage(final String text){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					chatWindow.append(text);
				}
			}
		);
	}

	//Let the user type stuff into the box
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
