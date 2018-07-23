import java.awt.*;
import javax.swing.*;

public class GUI extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	Server server = null;
	MessageDistributer msgDist = null;
	
	JTextField messageField;
	JTextArea chatHistoryArea;
	JButton sendButton;
	JTextField portField;
	JButton openServerButton;
	JButton closeServerButton;

	public void setServer(Server server) {
		this.server = server;
	}

	public void setMsgDist(MessageDistributer msgDist) {
		this.msgDist = msgDist;
	}

	public GUI() {
		setTitle("Simple TCP Chat Server");
		setSize(500, 250);
		setVisible(true);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
		JPanel panel = (JPanel) getContentPane();
		
		JPanel northPanel = new JPanel();
		panel.add(northPanel, BorderLayout.NORTH);
		
		JLabel portLabel = new JLabel("Server Port:");
		northPanel.add(portLabel);
		
		portField = new JTextField();
		portField.setColumns(5);
		northPanel.add(portField);
		
		openServerButton = new JButton("Open");
		northPanel.add(openServerButton);
		
		closeServerButton = new JButton("Close");
		northPanel.add(closeServerButton);
		closeServerButton.setEnabled(false);
		
		chatHistoryArea = new JTextArea();
		chatHistoryArea.setEditable(false);
		JScrollPane messageScrollPane = new JScrollPane(chatHistoryArea);
		add(messageScrollPane, BorderLayout.CENTER);
		
		JPanel southPanel = new JPanel();
		panel.add(southPanel, BorderLayout.SOUTH);
		
		southPanel.add(new JLabel("Message:"));
		JPanel messageFieldPanel = new JPanel();
		southPanel.add(messageFieldPanel);
		
		messageField = new JTextField(20);
		messageFieldPanel.add(messageField);
		sendButton = new JButton("Send");
		sendButton.setEnabled(false);
		messageFieldPanel.add(sendButton);
		
		// Events listeners
		openServerButton.addActionListener((eventObject) -> {
			boolean validPort = true;
			int port = -1;
			try {
				port = Integer.parseInt(portField.getText());
			} catch (NumberFormatException e) {
				addMessage("ERROR", "Please enter valid port!");
				validPort = false;
			}
			if (validPort && (port < 1 || port > 65535)) {
				validPort = false;
			}
			if (validPort) {
				openServerButton.setEnabled(false);
				server = new Server(this);
				server.open(port);
				closeServerButton.setEnabled(true);
				
				sendButton.setEnabled(true);
			}
		});
		
		closeServerButton.addActionListener((eventObject) -> {
			closeServerButton.setEnabled(false);
			sendButton.setEnabled(false);
			
			server.setRunning(false);
			server.close();
			
			openServerButton.setEnabled(true);
		});
		
		sendButton.addActionListener((eventObject) -> {
			if (msgDist != null) {
				try {
					msgDist.putMessage("SERVER", messageField.getText());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
				}
			}
		});
	}
	
	public void addMessage(String msg) {
		SwingUtilities.invokeLater(() -> {
			chatHistoryArea.append(msg + "\n");
		});
	}
	
	public void addMessage(String author, String msg) {
		String printMsg;
		
		switch (author) {
		case "Client": case "Server":
			printMsg = author + ": " + msg;
			break;
		default:
			printMsg = "### " + author + ": " + msg;
			break;
		}
		
		addMessage(printMsg);
	}
}
