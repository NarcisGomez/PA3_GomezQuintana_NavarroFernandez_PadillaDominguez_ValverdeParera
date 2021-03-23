package clientServer;

import java.io.*;
import java.net.*;
import java.util.*;

import common.Question;

public class Server extends Thread {

	// LAUNCHER: server(s) spawner
	public static void main(String[] args) throws IOException {
		Socket connection;
		art = new LinkedList<Question>();
		geo = new LinkedList<Question>();
		science = new LinkedList<Question>();
		List<Question> questions = Question.fromFile(new File("Questions.txt"));
		for (Question question : questions) {
			switch (question.getType()) {
			case "GEO":
				geo.add(question);
				break;
			case "ART":
				art.add(question);
				break;
			case "SCIENCE":
				science.add(question);
				break;
			}
		}

		ServerSocket serverSocket = new ServerSocket(4445);
		System.out.println("SOLITRIVIA multi-server is ready and accepting incoming connections (port 4445)");

		while (true) {
			connection = serverSocket.accept();
			new Server(connection).start();
		}
	}
	// LAUNCHER ENDS HERE

	private static List<Question> art, geo, science; // BEWARE! Static.
	// instances shouldn't modify these lists

	private Socket connection;
	private BufferedReader inputChannel;
	private PrintWriter outputChannel;

	private Random alea;
	private List<Question> artList, geoList, scienceList;

	public Server(Socket connection) throws IOException {
		this.connection = connection;
		this.inputChannel = new BufferedReader(new InputStreamReader(this.connection.getInputStream()));
		this.outputChannel = new PrintWriter(this.connection.getOutputStream(), true);

		this.alea = new Random();
		artList = new LinkedList<Question>(art);
		geoList = new LinkedList<Question>(geo);
		scienceList = new LinkedList<Question>(science);
	}

	public void run() {
		try {
			innerRun();
		} catch (IOException ioex) {
			ioex.printStackTrace(System.err);
		}
	}

	public void innerRun() throws IOException {

		Request request = receiveRequest();
		if (!request.type.equals("HELLO")) {
			sendReply("BAD REQUEST");
			disconnect();
			return;
		}
		sendReply("HELLO");

		request = receiveRequest();
		boolean unknownType = false;
		while (request.type.equals("NEXT")) {
			switch (request.info) {
			case "GEO":
				if (geoList.size() > 0) {
					Question q = geoList.get(alea.nextInt(geoList.size()));
					geoList.remove(q);
					sendReply(q.toString());
				} else {
					sendReply("NO MORE");
				}
				break;
			case "ART":
				if (artList.size() > 0) {
					Question q = artList.get(alea.nextInt(artList.size()));
					artList.remove(q);
					sendReply(q.toString());
				} else {
					sendReply("NO MORE");
				}
				break;
			case "SCIENCE":
				if (scienceList.size() > 0) {
					Question q = scienceList.get(alea.nextInt(scienceList.size()));
					scienceList.remove(q);
					sendReply(q.toString());
				} else {
					sendReply("NO MORE");
				}
				break;
			default:
				unknownType = true;
				break;
			}

			if (unknownType) {
				break;
			} else {
				request = receiveRequest();
			}
		}

		if (!request.type.equals("STOP") || unknownType) {
			sendReply("BAD REQUEST");
		}
		disconnect();

	}

	private Request receiveRequest() throws IOException {
		String contents = inputChannel.readLine();
		Request request = new Request();
		int b = contents.indexOf(" "); // position of the first blank
		if (b < 0) {
			request.type = contents;
			request.info = "";
		} else {
			request.type = contents.substring(0, b); // type of requested question goes from the beginning till the first blank
			request.info = contents.substring(b + 1).trim(); // information is everything following the first blank
		}
		return request;
	}

	private void sendReply(String reply) throws IOException {
		this.outputChannel.println(reply);
	}

	private void disconnect() throws IOException {
		this.connection.close();
		this.inputChannel.close();
		this.outputChannel.close();
	}

	/* PRIVATE Server-Side only class to represent requests */
	private class Request {
		public String type; // type of request (HELLO, NEXT, STOP)
		public String info; // additional information (GEO, SCIENCE, ART)
	}

}
