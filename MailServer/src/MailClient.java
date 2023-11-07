import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MailClient extends JFrame {
    private DatagramSocket clientSocket;
    private byte[] sendData;
    private byte[] receiveData;
    private InetAddress serverAddress;
    private int serverPort;

    private JTextArea textArea;
    private JTextField textField;
    private JTextField textField1;
    private JLabel tenDangNhap;
   private JLabel mk;
   private JLabel thongbao;
    private JButton createAccountButton;
    private JButton sendButton;
    private JButton loginButton;

    public MailClient(String serverAddress, int serverPort) throws SocketException, UnknownHostException {
        this.serverAddress = InetAddress.getByName(serverAddress);
        this.serverPort = serverPort;
        clientSocket = new DatagramSocket();
        sendData = new byte[1024];
        receiveData = new byte[1024];

        setTitle("Mail Client");
        setSize(900, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        JPanel nhap = new JPanel(new GridLayout(3,3));
        textField = new JTextField(20);
        textField1 = new JTextField(20);
        thongbao = new JLabel();
        tenDangNhap = new JLabel("Tên Đăng Nhập: ");
        mk = new JLabel("Mật khẩu: ");
        createAccountButton = new JButton("Create Account");
        sendButton = new JButton("Send Email");
        loginButton = new JButton("Login");
        nhap.add(tenDangNhap);
        nhap.add(textField);
        nhap.add(mk);
        nhap.add(textField1);
        nhap.add(thongbao);
        
       
//        bottomPanel.add(textField);
        bottomPanel.add(nhap);
        bottomPanel.add(createAccountButton);
        bottomPanel.add(sendButton);
        bottomPanel.add(loginButton);
        add(bottomPanel, BorderLayout.SOUTH);

        createAccountButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String accountName = textField.getText();
                String passWord = textField1.getText();
             // Biểu thức chính quy để kiểm tra địa chỉ email
                String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

                // Kiểm tra chuỗi có khớp với mẫu địa chỉ email hay không
                boolean isValid = accountName.matches(emailRegex);

                // In kết quả
                if (isValid) {
                	
                    System.out.println("Địa chỉ email hợp lệ");
                    // Biểu thức chính quy để kiểm tra chuỗi
                    String regex = "^(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";

                    // Tạo đối tượng Pattern từ biểu thức chính quy
                    Pattern pattern = Pattern.compile(regex);

                    // Tạo đối tượng Matcher để so khớp chuỗi với biểu thức chính quy
                    Matcher matcher = pattern.matcher(passWord);

                    // Kiểm tra xem chuỗi có chứa cả chữ hoa và ít nhất 8 ký tự và kí tự đặc biệt hay không
                    if (matcher.matches()) {
                        System.out.println("Chuỗi hợp lệ");
                        createAccount(accountName, passWord);
                        thongbao.setText("");
                    } else {
                    	thongbao.setText("Mật khẩu không hợp lệ");
                        System.out.println("Chuỗi không hợp lệ");
                    }
                } else {
                	thongbao.setText("Địa chỉ email không hợp lệ");
                    System.out.println("Địa chỉ email không hợp lệ");
                }

               textField1.setText("");
               
                
                textField.setText("");
            }
        });

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String emailContent = textField.getText();
                sendEmail(emailContent);
                textField.setText("");
                textField1.setText("");
                thongbao.setText("");
            }
        });

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String accountName = textField.getText();
                String passWord = textField1.getText();
                setTitle("User: "+accountName);
                login(accountName, passWord);
                
                textField.setText("");
                textField1.setText("");
                thongbao.setText("");
            }
        });

        setVisible(true);
    }

    private void createAccount(String accountName, String passWord) {
        String request = "CREATE_ACCOUNT#" + accountName +"#"+passWord;
        sendData = request.getBytes();

        try {
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
            clientSocket.send(sendPacket);
            

            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);

            String response = new String(receivePacket.getData()).trim();
            showMessage(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendEmail(String emailContent) {
        String accountName = JOptionPane.showInputDialog("Enter account name:");
        String request = "SEND_EMAIL#" + accountName + "#" + emailContent;
        sendData = request.getBytes();

        try {
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
            clientSocket.send(sendPacket);

            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);

            String response = new String(receivePacket.getData()).trim();
            showMessage(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void login(String accountName, String passWord) {
        String request = "LOGIN#" + accountName + "#"+passWord;
        System.out.println(request);
        sendData = request.getBytes();
   
        try {
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
            clientSocket.send(sendPacket);
            
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
         
            String response = new String(receivePacket.getData()).trim();
            if (response!="") {
				loginButton.setText("Logout");
				tenDangNhap.setText("Tin nhắn: ");
                mk.setText("");
				
			}else {
				thongbao.setText("Sai tên đăng nhập hoặc mật khẩu");
			}
            showMessage("Files: \n" + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showMessage(String message) {
        textArea.append(message + "\n");
    }

    public static void main(String[] args) throws SocketException, UnknownHostException {
        MailClient client = new MailClient("localhost", 1234);
    }
}
