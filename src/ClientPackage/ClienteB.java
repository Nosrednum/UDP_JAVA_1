package ClientPackage;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import LoggerPackage.Info;
import LoggerPackage.Logger;
import UtilityPackage.FileCheckSumMD5;
import UtilityPackage.Timer;

public class ClienteB {

	public final static int SOCKET_PORT = 13267;
	public final static String SERVER = "18.208.134.243", RUTA = "./data2/";

	public final static int FILE_SIZE = (int) (Math.pow(2, 20) * 251); // Espacio para el archivo

	public static void main(String[] args) throws Exception {
		int bytesRead, current = 0;
		Timer t = new Timer();
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		PrintWriter pw = null;
		BufferedReader bf=null;
		DatagramSocket sock = null;
		String hash = new String();

		try {
			DatagramSocket socket = new DatagramSocket(
					SOCKET_PORT,
					InetAddress.getByName(SERVER));
			System.out.println("Connecting...");
			System.out.println("Connection established successfully");
			int cantidad_iteraciones = 10, i;
			t.start();
			//			InputStream is = sock.getInputStream(); // recepción del socket
			byte mybytearray[] = new byte[FILE_SIZE]; // representación byte a byte del archivo
			fos = new FileOutputStream(RUTA + "fileof" + sock.getLocalPort() + ".mp4"); // Stream de envio de archivos
			bos = new BufferedOutputStream(fos); // Stream de escritura del cliente
			/* Lectura del archivo */
			DatagramPacket dato = new DatagramPacket(new byte[3000], 3000);
			byte[] datos;
			do {
				sock.receive(dato);
				datos = dato.getData();
				for(i=0;i++<datos.length;)
					mybytearray[current+i]=datos[i];
				current += datos.length;
			} while (cantidad_iteraciones-->0);
			bos.write(mybytearray, 0, current);
			bos.flush();
			int size =(int) (new File(RUTA + "fileof" + sock.getLocalPort() + ".mp4").length()), paqs =(int) size/3000 +1;
			//			sock.getOutputStream().flush();
			boolean correcto = hash.equals(FileCheckSumMD5.checksum(RUTA + "fileof" + sock.getLocalPort() + ".mp4"));
			System.out.println("El archivo fue recibido "+((correcto)?"correctamente":"incorrectamente"));
			System.out.println("File downloaded (" + current + " bytes read)");
			Logger.loggear(new Info("socket "+sock.getLocalPort(), t.stop()+"", paqs, paqs, size, size),
					RUTA + "fileof" + sock.getLocalPort() + ".mp4",
					size);
		} finally {
			if (fos != null)
				fos.close();
			if (bos != null)
				bos.close();
			if (sock != null)
				sock.close();
			if (pw != null)
				pw.close();
			if (bf != null)
				bf.close();
		}
	}
}
