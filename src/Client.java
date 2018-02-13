import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

import javax.imageio.ImageIO;

public class Client {

	private static String username;
	
	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {

		Socket clientSocket = null;
		Scanner sc = new Scanner(System.in);
		ObjectOutputStream objectOutput = null;
		ObjectInputStream objectInput = null;

		try {
			//System.out.println("Entrez une adresse IP: ");
			//String IPAddress = sc.nextLine();
			String IPAddress = "127.0.0.1"; // A ENLEVERRRRRRRRRRRRRRRRRRRRRR
			System.out.println("Entrez un port entre 5000 et 5050: ");
			int port = sc.nextInt();
			sc.nextLine();
			System.out.println("Enter user: ");
			username = sc.nextLine();
			System.out.println("Enter pass: ");
			String pass = sc.nextLine();

			if (verifyIP(IPAddress) && verifyPort(port)){
				clientSocket = new Socket(IPAddress, port);
				if (login(objectOutput, objectInput, clientSocket, username, pass)){
					convertImage(clientSocket);
				}
			}
		} finally {
			// Fermeture du socket.
			System.out.println("Closing client socket");
			clientSocket.close();
		}
	}

	public static boolean verifyIP(String ip) {
		//check if ip is valid
		String ipArray[] = ip.split("\\.");
		boolean IPisValid = true;
		if (ipArray.length < 5) {
			for (int i = 0; i < 4; i++) {
				if (!(Integer.valueOf(ipArray[i]) >= 0) && !(Integer.valueOf(ipArray[i]) < 256)) {
					System.out.println("IP not valid (bytes not between 0 and 255)");
					IPisValid = false;
				}
			}
			return IPisValid;
		}
		else {
			System.out.println("IP is not valid (not 4 bytes)");
			return false;
		}
	}

	public static boolean verifyPort(int port) {
		// check if port is positive integer and in 5000 and 5050
		if (port >= 5000 && port <= 5050){
			return true;
		}
		else {
			System.out.println("Port is not valid (not between 5000 and 5050)");
			return false;
		}
	}
	
	public static boolean login(ObjectOutputStream objectOutput, ObjectInputStream objectInput, Socket clientSocket, String user, String pass) throws IOException, ClassNotFoundException{
		objectOutput = new ObjectOutputStream(clientSocket.getOutputStream());
		objectOutput.writeObject(user);
		objectOutput.writeObject(pass);
		objectOutput.flush();
		
		objectInput = new ObjectInputStream(clientSocket.getInputStream());
		Boolean boolLogin = (Boolean) objectInput.readObject();
		if (!boolLogin)
			System.out.println("Erreur dans la saisie du mot de passe.");
		return boolLogin;	
	}
	
	public static void convertImage(Socket clientSocket) throws IOException{
		Scanner sc = new Scanner(System.in);
		System.out.println("Entrez le nom de l'image (n'oubliez pas de mettre .jpg): ");
		String imageName = sc.nextLine();
		System.out.println("Entrez le nom pour la nouvelle image (n'oubliez pas de mettre .jpg): ");
		String newImageName = sc.nextLine();
		
		ObjectOutputStream objectO = new ObjectOutputStream(clientSocket.getOutputStream());
		objectO.writeObject(username);
		objectO.writeObject(imageName);
		objectO.flush();
		
		OutputStream outputStream = clientSocket.getOutputStream();
		BufferedImage image = ImageIO.read(new File(imageName));
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", byteArrayOutputStream);
        byte[] sizeO = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
        outputStream.write(sizeO);
        outputStream.write(byteArrayOutputStream.toByteArray());
        outputStream.flush();
        System.out.println("L'image est envoyé...");
        
        InputStream inputStream = clientSocket.getInputStream();
        byte[] sizeAr = new byte[4];
        inputStream.read(sizeAr);
        int sizeI = ByteBuffer.wrap(sizeAr).asIntBuffer().get();
        byte[] imageAr = new byte[sizeI];
        inputStream.read(imageAr);
        BufferedImage newImage = ImageIO.read(new ByteArrayInputStream(imageAr));
        File f = new File(newImageName);
        ImageIO.write(newImage, "jpg", f );
        System.out.println("L'image traitée recue... (Location: " + f.getAbsolutePath() + ")");
	}
}