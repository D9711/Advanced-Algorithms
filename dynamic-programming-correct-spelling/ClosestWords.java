/* Labb 2 i DD2350 Algoritmer, datastrukturer och komplexitet    */
/* Se labbinstruktionerna i kursrummet i Canvas                  */
/* Ursprunglig författare: Viggo Kann KTH viggo@nada.kth.se      */
import java.util.LinkedList;
import java.util.List;


/**
 * ##############################################################################################################################################################################################
 */
// G & D

// PS C:\Users\Daniel\documents\github\labb2-adk> java Main -t large
// Processing folder: large
// Processing testcase: testmedordlista3
// CPU time for this test: 97 ms
// Processing testcase: testmedordlista4
// CPU time for this test: 119 ms
// PS C:\Users\Daniel\documents\github\labb2-adk> javac Main.java
// PS C:\Users\Daniel\documents\github\labb2-adk> java Main -t large
// Processing folder: large
// Processing testcase: testmedordlista3
// CPU time for this test: 94 ms
// Processing testcase: testmedordlista4
// CPU time for this test: 235 ms

import java.lang.Math;
import java.util.Collections;
/**
 * ##############################################################################################################################################################################################
 */


public class ClosestWords {
  LinkedList<String> closestWords = null;

  int closestDistance = -1;



/**
 * ##############################################################################################################################################################################################
 */
// G & D


  String lastW = "";


  int maxWordLen = 200;
  
  // dynprogmatrisen.. Anta att max ordlängd är 200
  int[][] dynProgMatrix = new int[maxWordLen][maxWordLen];

  /**
   *     0 1 2 3 4
   * 0 [ 0 1 2 3 4 ]
   * 1 [ 1 - - - - ]
   * 2 [ 2 - - - - ]
   * 3 [ 3 - - - - ]
   * 4 [ 4 - - - - ]
   */
  void initRowNColumnZero() {
    // Kolumn 0 och rad 0 är kostanter.
    for(int i = 0 ; i < maxWordLen; i++) {
      dynProgMatrix[i][0] = i;
      dynProgMatrix[0][i] = i;
    }
  } 

  // dynprogapproach
  int calcDist(String w1, String w2, int w1len, int w2len) {


    // Kolla hur många placeringar om w2 stämmer överens med förra ordet: lastW
    int numOfEquals = 0;

    if(lastW != "") {
      while (numOfEquals < lastW.length() && w2.charAt(numOfEquals) == lastW.charAt(numOfEquals)) {
        numOfEquals++;
      }
    } 
    
    numOfEquals = (numOfEquals == 0)? 1 : numOfEquals;

    for (int i=1; i<=w1len; i++) {
      
      for (int j=numOfEquals; j<=w2len; j++) {
        
        // Initiera kostnad
        int c = (w1.charAt(i-1) == w2.charAt(j-1) ? 0 : 1);    

        dynProgMatrix[i][j] = Math.min(Math.min(dynProgMatrix[i-1][j-1] + c, dynProgMatrix[i-1][j] + 1), dynProgMatrix[i][j-1] + 1);
      } 
    }
    return dynProgMatrix[w1len][w2len];
  }

  void printMatrix(String w1, String w2) {
    System.out.println();
    System.out.println(w1 + " -- " + w2);
    for (int i = 0; i<=w1.length(); i++) {
      System.out.println();
      for (int j = 0; j<=w2.length(); j++) {
        System.out.print(dynProgMatrix[i][j] + " ");
      } 
    }
    System.out.println();
  }



/**
 * ##############################################################################################################################################################################################
 */

  int partDist(String w1, String w2, int w1len, int w2len) {
    if (w1len == 0)
      return w2len;
    if (w2len == 0)
      return w1len;
    int res = partDist(w1, w2, w1len - 1, w2len - 1) + 
	(w1.charAt(w1len - 1) == w2.charAt(w2len - 1) ? 0 : 1);
    int addLetter = partDist(w1, w2, w1len - 1, w2len) + 1;
    if (addLetter < res)
      res = addLetter;
    int deleteLetter = partDist(w1, w2, w1len, w2len - 1) + 1;
    if (deleteLetter < res)
      res = deleteLetter;
    return res;
  }

  int distance(String w1, String w2) {
    return calcDist(w1, w2, w1.length(), w2.length());
  }

  public ClosestWords(String w, List<String> wordList) {
    /**
    * ##############################################################################################################################################################################################
    */
    // G & D
    initRowNColumnZero();
    /**
    * ##############################################################################################################################################################################################
    */

    for (String s : wordList) {
      int dist = distance(w, s);
      lastW = s;
      // System.out.println("d(" + w + "," + s + ")=" + dist);
      if (dist < closestDistance || closestDistance == -1) {
        closestDistance = dist;
        closestWords = new LinkedList<String>();
        closestWords.add(s);
      }
      else if (dist == closestDistance)
        closestWords.add(s);
    }
  }

  int getMinDistance() {
    return closestDistance;
  }

  List<String> getClosestWords() {
    return closestWords;
  }
}
