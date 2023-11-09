// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI;
  String loginID;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String loginID, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    try {
    	openConnection();
    } catch (IOException e) {
    	clientUI.display("ERROR - Can't setup connection! Terminating client.");
    	System.exit(1);
    }
    this.loginID = loginID;
    sendToServer("#login " + loginID);
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
    
    
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
	message = message.trim();
	if (message.charAt(0) == ('#')) {
		String[] commandArgs = message.split(" ");
		if (commandArgs[0].equals("#quit")) {
			quit();
		} else if (commandArgs[0].equals("#logoff")) {
			try{
				closeConnection();
			} catch (IOException e) {
				clientUI.display("Failed to close connection due to: " + e.toString());
			}
		} else if (commandArgs[0].equals("#sethost")) {
			if (!isConnected()) {
				try {
					setHost(commandArgs[1]);
				} catch (ArrayIndexOutOfBoundsException e){
					clientUI.display("Please enter an argument after this command.");
				}
			} else {
				clientUI.display("Cannot change port while connected to server.");
			}
		} else if (commandArgs[0].equals("#setport")) {
			if (!isConnected()) {
				try {
					setPort(Integer.parseInt(commandArgs[1]));
				} catch (ArrayIndexOutOfBoundsException e){
					clientUI.display("Please enter an argument after this command.");
				} catch (NumberFormatException e){
					clientUI.display("Please enter a numerical argument after this command.");
				}
			} else {
				clientUI.display("Cannot change port while connected to server.");
			}
		} else if (commandArgs[0].equals("#login")) {
			try{
				openConnection();
			} catch (IOException e) {
				clientUI.display("Failed to open connection due to: " + e.toString());
			}
		} else if (commandArgs[0].equals("#gethost")) {
			clientUI.display(getHost());
		} else if (commandArgs[0].equals("#getport")) {
			clientUI.display(String.valueOf(getPort()));
		}
	} else {
		try
	    {
	      sendToServer(message);
	    }
	    catch(IOException e)
	    {
	      clientUI.display
	        ("Could not send message to server.  Terminating client.");
	      quit();
	    }
	}
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  
  @Override
  protected void connectionClosed() {
	  clientUI.display("Connection to server has closed.");
  }
  
  @Override
  protected void connectionException(Exception exception) {
	  clientUI.display("The server has shut down.");
	  System.exit(1);
	  //exception.printStackTrace();
  }
}
//End of ChatClient class
