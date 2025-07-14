import java.util.*;

public class KarpReduction {

    public static void main(String[] args) {
        
        Scanner scanner = new Scanner(System.in);        
        int vertices = scanner.nextInt();
        int edges = scanner.nextInt();
        int colors = scanner.nextInt();
        
        ArrayList<int[]> edge = new ArrayList<>(); // Håller koll på kanterna.
        
        // Mappning mellan icke-isolerade noders index -> roller med sammanhängande index.
        HashMap<Integer, Integer> vertexToRole = new HashMap<>(); 
       

       /*
        * Från reduktionen av det NP-fullständiga problemet graffärgning till rollbesättningsproblemet
        * visar vi att rollbesättningsproblemet är NP-svårt.
        *
        * Reduktionen är att:
        * - Varje roll motsvarar icke-isolerade noder.
        * - Varje scen motsvarar kanterna.
        * - Antalet färger kan minst vara 1 och maximalt vara antalet icke-isolerade noder.
        * 
        * Inga monologer får förekomma och kan endast förekomma genom isolerade noder.
        * Isolerade noder kan alltid färgas med m färgaer, vilket innebär att de inte påverkar frågan
        * om grafen går att färga eller inte. Därför tar vi bort de isolerade noderna.
        *
        *
        * Graffärgningen reduceras till rollbesättningen genom att.
        * Vi har en komponent med de reserverade rollerna och sedan lägger vi till grafen utan de isolerade noderna.
        * Då får vi att de reserverade scenerna och rollerna alltid kan besättas med de reserverade personerna.
        * Då blir frågan om den hela rollbesättningen beroende av svaret på frågan om
        * den andra komponenten, d.v.s., grafen i sig går att färga med m färger.
        *
        * Ytterligare för effektivisering:
        * En graf kan alltid färgas med |V|-färger.
        * Därför sätter vi att om m > roller, så sätter vi skådespelera till antalet roller + 3.
        * Detta är för att en rollbesättning alltid kan färga roller + 3, färger.
        *
        * Rollerna ska vara numrerade från 1, ..., n.
        * De icke-isolerade noderna har godtyckliga index från 1, ..., |V|.
        * vertexToRole är mappningen mellan icke-isolerade noder och roller med sammanhängande index från 4, ..., y
        * där y är antalet icke-isolerade noder.
        *
	*
	* Tidskomplexitet.
	*
	* Samla in data:
	* För varje kant. - O(E), E är kanter, 
	* 
	* Skriv ut indata: - O(V * V + E), där V är noder.
	*
	* Summerat:
	*  O(E + V * V), där V dominerar.
	*
	* */ 

        int roleNumber = 4;

        // O(E)
        
        //Vi går igenom varje kant och spara varje nod (unik) i en hashmap samt mappar
        //tillhörande roll till den noden (4 till antalet icke isolerade noder). Vi sparar också varje kant i 
        //edge array som en array inom array

        for(int i=0; i<edges; i++) {
            int u = scanner.nextInt(); 
            int v = scanner.nextInt();
            if (!vertexToRole.containsKey(u)) vertexToRole.put(u, roleNumber++);
            if (!vertexToRole.containsKey(v)) vertexToRole.put(v, roleNumber++);
            edge.add(new int[]{u, v});
        }


        //Tagits fram från enklaste fallet, har vi endast isolerade noder är det en ja instans
        //För rollbesättningsproblemet är enklaste fallet 3 roller, 2 scener och 3 skådespelare
        //Antalet roller är då 3 + antalet icke isolerade noder
        //Scener är då 2 + antalet kanter
        //Skådespelare är minst 3 stycken + minsta antalet icke isolerade noder eller antalet färger
        //Har vi många isolerade noder och många färger då får vi fler skådespelare än antalet roller
        //om vi åtgår enbart från antalet färger samtidigt får vi inte överskrida antalet färger

        int roles = vertexToRole.size() + 3;
        int scenes = edges + 2; 
        int actors = (colors > roles ? roles : colors ) + 3;
       	

        System.out.println(roles); // 1, ..., n
        System.out.println(scenes); // 1, ..., s
        System.out.println(actors); // 1, ..., k

        System.out.println("1 1");
        System.out.println("1 2");
        System.out.println("1 3");
      	
	// O(V^2)

        //Övriga roller utöver de reserverade kan besättas av alla övriga skådespelare
        //Alltså totala antalet skådespelare - 3 är möjliga besättningar och sen har vi varje skådespelare från 4 till totalen. 
        //1 till 3 är de reserverade som printas ovan.

        for(int i=4; i<=roles; i++) { // 4, ..., n
           System.out.print(actors-3);
           for(int j=4; j<=actors; j++) { // 4, ..., k
                System.out.print(" " + j);
           }
           System.out.print("\n");
        }

        System.out.println("2 1 3");
        System.out.println("2 2 3");
	// O(E)
        

        //reserverade scener där 1 och 2 inte får spela mot varandra printas först här ovan
        //sen printas övriga scener (kanter) från indata grafen. Varje övrig roll är sparad i hashmap och mappad
        //till en nod i
        //VertexToRole från roll 4 och uppåt då vi inte vill ha med de reserverade rollerna
        //Scenerna (kanterna) måste då gå att besätta med roll 4 till y, roll 4 till y motsvarar
        //antalet icke isolerade noder (finns i en kant)

        for(int[] scene: edge) { // 3, ..., s 
            int u = vertexToRole.get(scene[0]);
            int v = vertexToRole.get(scene[1]);
            System.out.println("2 " + u + " " +  v);
        }
    }
}
