package rmi;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import common.*;

public class TrivialSolitaireImpl extends UnicastRemoteObject implements TrivialSolitaire {

	public static void main (String [] args) throws Exception {
		// launcher
		Registry registry  = LocateRegistry.createRegistry(1998);
		registry.bind("TrivialSolitaire", new TrivialSolitaireImpl());
		System.out.println("Trivial solitaire service running (registry in port 1998)...");
	}
	
	
	//---------------------------------------------------------------------
	
	private static List<Question> art, geo, science; // shouldn't modify these lists...
	
	private List<ClientInfo> clientList;
	private int id;
	 
	// static initializer (initializes the lists)
	static {
		art = new LinkedList<Question>();
		geo = new LinkedList<Question>();
		science = new LinkedList<Question>();
		try {
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
		}
		catch (IOException ioex) {
			System.err.println("static initialization failed!!!");
			System.err.println(ioex);
			System.exit(0);
		}
	}
	
	public TrivialSolitaireImpl() throws RemoteException {
		clientList = new LinkedList<ClientInfo>();
		id = 0;
	}

	@Override
	public int Hello() throws RemoteException {
		int identifier = id;
		id += 1;
		clientList.add(new ClientInfo(identifier, geo, art, science));
		return identifier;
	}

	@Override
	public Question next(int id, String type) throws RemoteException {
		for (ClientInfo client : clientList) {
			if(client.getId() == id) return client.getQuestion(type);
		}
		return null;
	}

	@Override
	public void stop(int id) throws RemoteException {
		for (ClientInfo client : clientList) {
			if(client.getId() == id) {
				clientList.remove(client);
				break;
			}
		}
	}
}


// consider using instances of this class to store relevant information regarding a particular client
// (like the questions that have not been been sent to it yet...)
class ClientInfo {
	private Random alea;
	private int identifier;
	private List<Question> artList, geoList, scienceList;
	
	public ClientInfo (int id, List<Question> geo,List<Question> art, List<Question> science) {
		alea = new Random();
		identifier = id;
		artList = new LinkedList<Question>(art);
		geoList = new LinkedList<Question>(geo);
		scienceList = new LinkedList<Question>(science);
	}
	
	public Question getQuestion(String type) {
		
		Question question = null;
		
		switch (type) {
		case "GEO":
			if (geoList.size() > 0) {
				question = geoList.get(alea.nextInt(geoList.size()));
				geoList.remove(question);
			}
			break;
			
		case "ART":
			if (artList.size() > 0) {
				question = artList.get(alea.nextInt(artList.size()));
				artList.remove(question);
			}
			break;

		case "SCIENCE":
			if (scienceList.size() > 0) {
				question = scienceList.get(alea.nextInt(scienceList.size()));
				scienceList.remove(question);
			}
			break;

		}
		return question;
	}
	
	public int getId() {return identifier;}
}