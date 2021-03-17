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
	
	/* COMPLETE 1a add other necessary attributes */
	
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
		/* COMPLETE if needed 1b: Constructor ... */
	}

	/* COMPLETE 2: implement interface and other helper methods */
	

}


// consider using instances of this class to store relevant information regarding a particular client
// (like the questions that have not been been sent to it yet...)
class ClientInfo {
	/* COMPLETE */

}
