import java.util.*;


public class ConstructProduction {

	static int numRoles;
	static int numScenes;
	static int numActors;
	static int p1 = 0;
	static int p2 = 1;

	static int[][] rolesMatrix;
	static int[][] scenesMatrix;
	static HashMap<Integer, ArrayList<Integer>> castedMap;


	public static void printOutput() {
		int castedCounter = 0;
		
		/*
		 * Se antalet skådisar som blivit tilldelade åtminstone en roll.
		 * */
		for(Map.Entry<Integer, ArrayList<Integer>> entry: castedMap.entrySet()) {
			if (entry.getValue().size() != 0  && (entry.getValue() != null)) {
				castedCounter++;
			}
		}

		System.out.println(castedCounter);


		int superActor = numActors;
		
		for(Map.Entry<Integer, ArrayList<Integer>> entry: castedMap.entrySet()) {
			if (entry.getValue().size() != 0) {
				int actor = entry.getKey();
				if (actor >= 0) {
					System.out.print((entry.getKey()+1) + " " + entry.getValue().size());
					for(int role: entry.getValue()) {
						System.out.print(" " + (role+1));
					}
					System.out.println();
				} else {
					actor = superActor;
					System.out.print((superActor+1) + " " + entry.getValue().size());
					for(int role: entry.getValue()) {
						System.out.print(" " + (role+1));
					}
					System.out.println();
					superActor++;
				}
			}
		}
	}



	public static void main(String[] args) {

		Kattio io = new Kattio(System.in);

		/* INITIALIZATION */
		numRoles = io.getInt();
		numScenes = io.getInt();
		numActors = io.getInt();
		rolesMatrix = new int[numRoles][numActors];
		scenesMatrix = new int[numScenes][numRoles];
		castedMap = new HashMap<>();
		/* Skapa en HashMap baserat på antalet skådisar*/
		for(int i=0; i<numActors; i++) {
			castedMap.put(i, new ArrayList<>());
		}
		/* Skapa 2d-array för roller där varje array motsvarar de skådisar som kan spela rollen */
        for(int i=0; i<numRoles; i++) {
            int actors = io.getInt();
            for(int j=0; j<actors; j++) {
                int actor = io.getInt();
                rolesMatrix[i][actor-1] = 1;
            } 
        }
		/* Skapa 2d-array för scener där varje array motsvarar de roller som finns i scenen */
        for(int i=0; i<numScenes; i++) {
            int roles = io.getInt();
            for(int j=0; j<roles; j++) {
                int role = io.getInt();
                scenesMatrix[i][role-1] = 1;
            }
    	}

	
		/*
		 * Följande är i syfte om att hitta roller där p1 och p2 kan spela där de inte spelar mot varandra.
		 *
		 * Vi lagrar information om vilka roller p1 respektive p2 kan spela och i vilka scener dessa roller finns med i.
		 *
		 * Om summan av en array i nedanstående arrayer är 0 innebär det att p1 respektive p2 inte kan spela dessa roller.
		 *
		 * */
		int[][] pOneCanPlay = new int[numRoles][numScenes];
		int[][] pTwoCanPlay =  new int[numRoles][numScenes];
		for(int i=0; i<numRoles; i++) {
			if (rolesMatrix[i][p1] == 1) {
				for(int j=0; j<numScenes; j++) {
					if(scenesMatrix[j][i] == 1) {
						/* p1 can play role i where role i is in scene j */
						pOneCanPlay[i][j] = 1;
					}
				}
			}
			
			if (rolesMatrix[i][p2] == 1) {
				for(int j=0; j<numScenes; j++) {
					if(scenesMatrix[j][i] == 1) {
						/* p1 can play role i where role i is in scene j */
						pTwoCanPlay[i][j] = 1;
					}
				}
			}
		} 
		/* Följande är i syfte om att hitta roller där p1 och p2 inte spelar mot varandra i någon scen. 
		 * Varje möjlig kombination av roller för p1 och p2 undersöks
		 * OBS: Hittar första bästa, skulle kunna maximeras så roller i så många scener som möjligt ansätts på en gång.
		 * 
		 * Problem om p1 och p2 kan spela exakt samma roller.
		 * */
		int pOneRole = -1;
		int pTwoRole = -1;
		for(int i=0; i<numRoles; i++) {
			int pOneRoleSum = Arrays.stream(pOneCanPlay[i]).sum();
			if (pOneRoleSum != 0) {
				for(int j=0; j<numRoles; j++) {
					int pTwoRoleSum = Arrays.stream(pTwoCanPlay[j]).sum();
					if (pTwoRoleSum != 0) {
						/* Nu har vi hittat en roll som p1 och p2 kan spela, dags att kolla om de spelar mot varandra eller inte */

						/* Vi gör en AND-operation, om rollerna inte delar scen ger AND-operationen en array-summa på 0. */
						int[] shareScene = new int[numScenes];
						for (int k=0; k<numScenes; k++) {
							shareScene[k] = pOneCanPlay[i][k] & pTwoCanPlay[j][k];
						}
						int sharedScenes = Arrays.stream(shareScene).sum();
						if(sharedScenes == 0) {
							pOneRole = i;
							pTwoRole = j;
							break;
						}
					}
				}
			}
			if(pOneRole != -1 & pTwoRole != -1) {
				break;
			}
		}
		castedMap.get(p1).add(pOneRole);
		castedMap.get(p2).add(pTwoRole);
		/* TEST: EFter att ha gett divorna egna roller, låt oss begränsa dem från att ha någon annan roll */
		for(int i=0; i<numRoles; i++) {
			rolesMatrix[i][p1] = 0;
			rolesMatrix[i][p2] = 0;
		}
	
		//System.out.println(pOneRole + " goes to p1");
		//System.out.println(pTwoRole + " goes to p2");


		/* Uppdatera rollmatrisen
		 * 1) Om rollen tilldelats - sätt -1 på alla platser.
		 * 2) Om en skådis blivit ansatt för en roll.
		 * - Iterera genom alla scener och undersök vilka roller som spelar tillsammans med rollen som tilldelats,
		 *   skådisen kan nu inte spela dessa roller
		 * 3) Uppdatera rollmatrisen och sätt skådisens index till 0 för de roller den inte kan spela.
		 * */

		/* Uppdatera rollmatrisen för p1 */

		for(int i=0; i<numActors; i++) {
			rolesMatrix[pOneRole][i] = -1;
		}
		for(int i=0; i<numActors; i++) {
			rolesMatrix[pTwoRole][i] = -1;
		}
		
		/*
		 * Här hittas roller till alla andra skådisar
		 * 
		 * 1) Iterera genom alla roller.
		 * 2) Ansätt den skådis som har lägst skådespelarnummer
		 * 3) Om ingen skådis kan ansättas, använd superskådis.
		 *  - Superskådisar är negativt indexerade för att ta hänsyn till hur många riktiga skådisar som kunde ansättas.
		 *		Ex. om vi lyckats ansätta skådis 1, 3, 5, och måste använda - 1, - 2
		 *   	Då säger vi i slutet att 1, 2, 3, 4 (-1), 5 (-2), 
		 * 4) Uppdatera rollmatrisen för att uttrycka vilka skådisar som inte kan spela rollerna längre, p.g.a. konflikter i scener med de roller som tilldelats.
		 * */
		int superActor = -1;
		for(int i=0; i<numRoles; i++) {
			/* Kontrollera att rollen inte redan ansatts */
			if (Arrays.stream(rolesMatrix[i]).sum() >= 0) {
				int foundActor = -1;
				for (int j=0; j<numActors; j++) {
					/* Om vi kan ansätta skådisen */
					if (rolesMatrix[i][j] == 1) {
						foundActor = j;
					}
				}

				/* Om vi lyckades ansätta skådis */
				if (foundActor != -1) {
					castedMap.get(foundActor).add(i);
					/* Uppdatera rollmatrisen */
					for(int k=0; k<numScenes; k++) {
						if (scenesMatrix[k][i] == 1) {
							for(int l=0; l<numRoles; l++) {
								if (scenesMatrix[k][l] == 1 ) {
									rolesMatrix[l][foundActor] = 0;
								}
							}
						}
					}
				} else {
					/* Ingen skådis kunde ansättas, ansätt superskådis */
					castedMap.put(superActor, new ArrayList<>());
					castedMap.get(superActor).add(i);
					superActor--;
				}

				/* Markera att rollen redan tilldelats */	
				for(int k=0; k<numActors; k++) {
						rolesMatrix[i][k] = -1;
					}
			}
		}
	


	printOutput();
	}
}