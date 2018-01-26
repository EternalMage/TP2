import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class Client {

	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {

		Socket clientSocket = null;
		Scanner sc = new Scanner(System.in);

		try {
			// Création d'un socket client vers le serveur. Ici 127.0.0.1 est indicateur que
			// le serveur s'exécute sur la machine locale. Il faut changer 127.0.0.1 pour
			// l'adresse IP du serveur si celui-ci ne s'exécute pas sur la même machine.
			//System.out.println("Entrez une adresse IP: ");
			//String IPAddress = sc.nextLine();
			String IPAddress = "127.0.0.1"; // A ENLEVERRRRRRRRRRRRRRRRRRRRRR
			System.out.println("Entrez un port entre 5000 et 5050: ");
			int port = sc.nextInt();
			sc.nextLine();
			System.out.println("Enter user: ");
		    String user = sc.nextLine();
		    System.out.println("Enter pass: ");
		    String pass = sc.nextLine();
		    
			if (verifyIP(IPAddress) && verifyPort(port)){
				clientSocket = new Socket(IPAddress, port);
				ObjectOutputStream objectOutput = new ObjectOutputStream(clientSocket.getOutputStream());
				objectOutput.writeObject(user);
				objectOutput.writeObject(pass);
				objectOutput.flush();
				
				ObjectInputStream obj = new ObjectInputStream(clientSocket.getInputStream());
				String login = (String) obj.readObject();
				System.out.println("Login is: " + login);
				// Ici, on suppose que le fichier que vous voulez inverser se nomme text.txt
				/*List<String> linesToSend = readFile("text.txt");
				// Écriture de l'objet à envoyer dans le output stream. Attention, la fonction
				// writeObject n'envoie pas l'objet vers le serveur! Elle ne fait qu'écrire dans
				// le output stream.
				objectOutput.writeObject(linesToSend);
				// Envoi des lignes du fichier texte vers le serveur sous forme d'une liste.
				objectOutput.flush();
				// Création du input stream, pour recevoir les données traitées du serveur.
				ObjectInputStream obj = new ObjectInputStream(clientSocket.getInputStream());
				@SuppressWarnings("unchecked")
				// Noté bien que la fonction readObject est bloquante! Ainsi, l'exécution du
				// client s'arrête jusqu'à la réception du résultat provenant du serveur!
				Stack<String> receivedStack = (Stack<String>) obj.readObject();
				// Écriture du résultat dans un fichier nommée FichierInversee.txt
				writeToFile(receivedStack, "FichierInversee.txt");*/
			}
		} finally {
			// Fermeture du socket.
			System.out.println("Closing client socket");
			clientSocket.close();
		}
	}

	// Fonction permettant de lire un fichier et de stocker son contenu dans une liste.
	private static List<String> readFile(String nomFichier) throws IOException {
		List<String> listOfLines = new ArrayList<String>();
		String line = null;
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		try {
			fileReader = new FileReader(nomFichier);

			bufferedReader = new BufferedReader(fileReader);

			while ((line = bufferedReader.readLine()) != null) {
				listOfLines.add(line);
			}
		} finally {
			fileReader.close();
			bufferedReader.close();
		}
		return listOfLines;
	}

	// Fonction permettant d'écrire dans un fichier les données contenues dans la
	// stack reçu du serveur.
	private static void writeToFile(Stack<String> myStack, String nomFichier) throws IOException {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(nomFichier));
			while (!myStack.isEmpty()) {
				out.write(myStack.pop() + "\n");
			}
		} finally {
			out.close();
		}
	}

	public static boolean verifyIP(String ip) {
		//check if ip is valid
		System.out.println(ip);
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
}