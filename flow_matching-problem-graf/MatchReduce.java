/**
 * Exempel på in- och utdatahantering för maxflödeslabben i kursen
 * ADK.
 *
 * Använder Kattio.java för in- och utläsning.
 * Se http://kattis.csc.kth.se/doc/javaio
 *
 * Fil har ändrats lite men ursprungsförfattare är.
 * @author: Per Austrin
 */
import java.util.HashMap; // G: För kapacitet.
import java.util.Map;


public class MatchReduce {
    Kattio io;

	// G: Globala variabler som behövs för problemöversättning
    HashMap<Integer, HashMap<Integer, Integer>> capacity = new HashMap<>();
	HashMap<Integer, HashMap<Integer, Integer>> flow = new HashMap<>();
    int source; // G: Källnod
    int sink; // G: Sänknod
	int x; // G: Antal noder i nodmängd X
	int y; // G: Antal noder i nodmängd Y
	int edges; // G: Antal kanter
    int maxflow;


    void readBipartiteGraph() {
	// Läs antal hörn och kanter
	x = io.getInt();
	y = io.getInt();
    edges = io.getInt();

    source = x + y + 1;
    sink = x + y + 2;


	// Läs in kanterna
	for (int i = 0; i < edges; ++i) {
	    int a = io.getInt();
	    int b = io.getInt();

        // G: Lägg till kant mellan a och b, samt kapacitetrr till 1.
		if (!capacity.containsKey(a)) capacity.put(a, new HashMap<>());     

		// G: Lägg till kant mellan X och Y, med kapacitet 1.
		capacity.get(a).put(b, 1);
		
	}
    }
    
    
    void writeFlowGraph() {

	int vertices = x + y + 2;

	// Skriv ut antal hörn och kanter samt källa och sänka
	io.println(vertices);
	io.println(source + " " + sink);
	io.println(edges + x + y);

	// G: Översättning till flödesproblemet
    for(int i=1; i<=x; i++) {
        io.println(source + " " + i + " " + 1);
    }

    for(int i=x+1; i<=y+x; i++) {
        io.println(i + " " + sink + " " + 1);
    }

	for (Map.Entry<Integer, HashMap<Integer, Integer>> nodes : capacity.entrySet()) {
	    Integer nodeX = nodes.getKey();  
	    HashMap<Integer, Integer> edges = nodes.getValue();
		for(Map.Entry<Integer, Integer> edge: edges.entrySet()) {
			Integer nodeY = edge.getKey();
			Integer capXY = edge.getValue();
			io.println(nodeX + " " + nodeY + " " + capXY);
		}
	}
    

	// G: Vi tömmer mapen.
	capacity.clear();

	// Var noggrann med att flusha utdata när flödesgrafen skrivits ut!
	io.flush();

    }
    
    
    void readMaxFlowSolution() {
	// Läs in antal hörn, kanter, källa, sänka, och totalt flöde
	// (Antal hörn, källa och sänka borde vara samma som vi i grafen vi
	// skickade iväg)
	int v = io.getInt();
	int s = io.getInt();
	int t = io.getInt();
	int totflow = io.getInt();
    maxflow = totflow;
	int e = io.getInt();
	
	for (int i = 0; i < e; ++i) {
	    // Flöde f från a till b
	    int a = io.getInt();
	    int b = io.getInt();
	    int f = io.getInt();

        // G: Lägg till kant mellan a och b, samt flöde till f.
        if (!(a == source || a == sink) && !(b == source || b == sink)) {
            if (!flow.containsKey(a)) flow.put(a, new HashMap<>());     
		    // G: Lägg till kant mellan X och Y med flöde f.
		    flow.get(a).put(b, f);
        }
	}
    }
    
    
    void writeBipMatchSolution() {
	int maxMatch = maxflow;
	
	// Skriv ut antal hörn och storleken på matchningen
	io.println(x + " " + y);
	io.println(maxMatch);

	// G: Översättning från flödesresultat till matchningsresultat
	for (Map.Entry<Integer, HashMap<Integer, Integer>> nodes : flow.entrySet()) {
	    Integer nodeX = nodes.getKey();  
	    HashMap<Integer, Integer> edges = nodes.getValue();
		for(Map.Entry<Integer, Integer> edge: edges.entrySet()) {
			Integer nodeY = edge.getKey();
			Integer flowXY = edge.getValue();
			if(flowXY == 1) io.println(nodeX + " " + nodeY);
		}
	}

    // Var noggrann med att flusha utdata när flödesgrafen skrivits ut!
	io.flush();	
    }
    
    MatchReduce() {
	io = new Kattio(System.in, System.out);
	
	readBipartiteGraph();
	
	writeFlowGraph();
	
	readMaxFlowSolution();
	
	writeBipMatchSolution();


	// Kom ihåg att stänga ner Kattio-klassen
	io.close();
    }
    
    public static void main(String args[]) {
	new MatchReduce();
    }
}
