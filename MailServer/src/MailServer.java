import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class MailServer {
    private DatagramSocket serverSocket;
    private byte[] receiveData;
    private byte[] sendData;

    public MailServer(int port) throws SocketException {
        serverSocket = new DatagramSocket(port);
        receiveData = new byte[1024];
        sendData = new byte[1024];
    }

    public void startServer() throws IOException {
        System.out.println("Server is running...");
        
        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            
            byte[] data = receivePacket.getData(); // Dữ liệu từ gói tin nhận được
           
            String request = new String(data).trim(); // Tạo chuỗi mới và loại bỏ khoảng trắng
            data = null;
            receiveData = new byte[1024];
          


            
//            String request = new String(receivePacket.getData()).trim();
            System.out.println(request);
            
            InetAddress clientAddress = receivePacket.getAddress();
            int clientPort = receivePacket.getPort();
            
            Thread clientThread = new Thread(() -> {
			    // Xử lý yêu cầu từ Client
			    String response = processRequest(request);

			    // Gửi phản hồi về Client
			    sendData = response.getBytes();
			    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
			   
			        try {
						serverSocket.send(sendPacket);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			        
			    
			});
			// Start the client thread
			clientThread.start();
       
//        // Xử lý yêu cầu từ Client
//        String response = processRequest(request);
//
//        // Gửi phản hồi về Client
//        sendData = response.getBytes();
//        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
//        serverSocket.send(sendPacket);

       
        }
    }

    private String processRequest(String request) {
        String response = "";

        String[] parts = request.split("#");
        String command = parts[0];

        if (command.equals("CREATE_ACCOUNT")) {
            String accountName = parts[1];
            String passWord = parts[2];
            createAccount(accountName, passWord);
            response = "Account created successfully!";
        } else if (command.equals("SEND_EMAIL")) {
            String accountName = parts[1];
            String emailContent = parts[2];
            sendEmail(accountName, emailContent);
            response = "Email sent successfully!";
        } else if (command.equals("LOGIN")) {
            String accountName = parts[1];
            System.out.println(accountName);
            response = getAccountFiles(accountName);
        }

        return response;
    }

    private void createAccount(String accountName, String passWord) {
        File accountFolder = new File("D:/BuiCongSang/VKU/Nam_3/HocKy1/LapTrinhMang/Network Programming Java/Source/MailServer/MailDatabase/"+accountName);
        if (!accountFolder.exists()) {
            accountFolder.mkdir();
            
            try {
                FileWriter fileWriter = new FileWriter("D:/BuiCongSang/VKU/Nam_3/HocKy1/LapTrinhMang/Network Programming Java/Source/MailServer/MailDatabase/"+accountName + "/new_email.txt");
                fileWriter.write("Thank you for using this service. We hope that you will feel comfortable.");
                fileWriter.close();
                FileWriter filepassWord = new FileWriter("D:/BuiCongSang/VKU/Nam_3/HocKy1/LapTrinhMang/Network Programming Java/Source/MailServer/MailDatabase/"+accountName + "/passWord.txt");
                filepassWord.write(passWord);
                filepassWord.close();
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendEmail(String accountName, String emailContent) {
        try {
            FileWriter fileWriter = new FileWriter("D:/BuiCongSang/VKU/Nam_3/HocKy1/LapTrinhMang/Network Programming Java/Source/MailServer/MailDatabase/"+accountName + "/"+accountName+"_email.txt");
            Date currentDate = new Date();
            // Định dạng ngày tháng
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            // Ép kiểu Date thành String
            String dateString = dateFormat.format(currentDate);
            
            fileWriter.write("Thời gian gửi tin: "+dateString+"\n");
            fileWriter.write(emailContent);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private String getAccountFiles(String accountName) {
//        File accountFolder = new File("E:/VKU3/Smesster1/Laptrinhmang5_Cam/lab/databaselab5/" + accountName);
//        File[] files = accountFolder.listFiles();
//        StringBuilder fileNames = new StringBuilder();
//
//        if (files != null) {
//            for (File file : files) {
//                if (file.isFile()) {
//                    fileNames.append(file.getName()).append(",");
//                }
//            }
//        }else {
//			System.err.println("File nullllllllllllllll");
//		}
//
//        if (fileNames.length() > 0) {
//            fileNames.deleteCharAt(fileNames.length() - 1);
//        }
//        System.out.println(fileNames.toString());
//
//        return fileNames.toString();
//    }
    private String getAccountFiles(String accountName) {
    	System.out.println(accountName);
        File accountFolder = new File("D:/BuiCongSang/VKU/Nam_3/HocKy1/LapTrinhMang/Network Programming Java/Source/MailServer/MailDatabase/" + accountName);
        StringBuilder fileNames = new StringBuilder();

        if (accountFolder.exists() && accountFolder.isDirectory()) {
            File[] files = accountFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        fileNames.append(file.getName()).append(",");
                    }
                }
            } else {
                System.err.println("No files found in the account folder.");
            }
        } else {
            System.err.println("Account folder does not exist or is not a directory.");
        }

        if (fileNames.length() > 0) {
            fileNames.deleteCharAt(fileNames.length() - 1);
        }
        System.out.println(fileNames.toString());

        return fileNames.toString();
    }


    public static void main(String[] args) throws IOException {
        MailServer server = new MailServer(1234);
        server.startServer();
    }
}
