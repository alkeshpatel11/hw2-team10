/*
 *  Copyright 2012 Carnegie Mellon University
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package edu.cmu.lti.oaqa.openqa.test.team10.passage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;

import edu.cmu.lti.oaqa.framework.data.PassageCandidate;
/**
 * This class implements bm25 scoring.
 * Passage windows are all spans composed by all combination of two keywords' position
 * @author Yitei
 *
 */
public class Bm25PassageCandidateFinder {
  private String text;
  private String docId;

  private double totalMatches;  
  private double totalKeyterms;
  private KeytermWindowScorer scorer;

  public Bm25PassageCandidateFinder( String docId , String text , KeytermWindowScorer scorer ) {
    super();
    this.text = text;
    this.docId = docId;
    this.scorer = scorer;
  }
  /**
   * This method returns a list of candidate passages.
   * This class implements bm25 scoring.
   * Passage windows are all spans composed by all combination of two keywords' position
   * @author Yitei
   *
   */
  public List<PassageCandidate> extractPassages( String[] keyterms,Map<String,Double> idf) {
    List<List<PassageSpan>> matchingSpans = new ArrayList<List<PassageSpan>>();
    List<PassageSpan> matchedSpans = new ArrayList<PassageSpan>();
    List<String> keys = new ArrayList<String>();
    // Find all keyterm matches.
    for ( String keyterm : keyterms ) {
      Pattern p = Pattern.compile( keyterm );
      Matcher m = p.matcher( text );
      while ( m.find() ) {
        PassageSpan match = new PassageSpan( m.start() , m.end() ) ;
        matchedSpans.add( match );
        totalMatches = totalMatches + idf.get(keyterm);
      }
      if (! matchedSpans.isEmpty() ) {
        matchingSpans.add( matchedSpans );
        keys.add(keyterm);
        totalKeyterms = totalKeyterms + idf.get(keyterm);
      }
    }

    // create set of left edges and right edges which define possible windows.
    List<Integer> leftEdges = new ArrayList<Integer>();
    List<Integer> rightEdges = new ArrayList<Integer>();
    for ( List<PassageSpan> keytermMatches : matchingSpans ) {
      for ( PassageSpan keytermMatch : keytermMatches ) {
        Integer leftEdge = keytermMatch.begin;
        Integer rightEdge = keytermMatch.end; 
        if (! leftEdges.contains( leftEdge ))
          leftEdges.add( leftEdge );
        if (! rightEdges.contains( rightEdge ))
          rightEdges.add( rightEdge );
      }
    }

    // For every possible window, calculate keyterms found, matches found; score window, and create passage candidate.
    List<PassageCandidate> result = new ArrayList<PassageCandidate>();
    int numOfWindow = 0;
    long totalLength = 0;
    for ( Integer begin : leftEdges ) {
      for ( Integer end : rightEdges ) {
        if ( end <= begin ) continue; 
          numOfWindow++;
          totalLength += (end - begin + 1);
      }
    }
    double k1 = 1.5;
    double b = 0.75;
    double avgdl = totalLength/(double)numOfWindow;
    for ( Integer begin : leftEdges ) {
      for ( Integer end : rightEdges ) {
        double score = 0;
        if ( end <= begin ) continue; 
        double matchesFound = 0;
        int i = 0;
        for ( List<PassageSpan> keytermMatches : matchingSpans ) {

          for ( PassageSpan keytermMatch : keytermMatches ) {
            if ( keytermMatch.containedIn( begin , end ) ){
              matchesFound++; 
            }
          }
          score += 1.0*matchesFound*(k1+1)/(0.0+matchesFound+k1*(1-b+b*(0.0+end-begin+1)/avgdl))*idf.get(keys.get(i));
          i++;
          
        }

        PassageCandidate window = null;
        try {
          window = new PassageCandidate( docId , begin , end , (float) score , null );
        } catch (AnalysisEngineProcessException e) {
          e.printStackTrace();
        }
        result.add( window );
      }
    }
    return result;

  }
  private class PassageCandidateComparator implements Comparator {
    // Ranks by score, decreasing.
    public int compare( Object o1 , Object o2 ) {
      PassageCandidate s1 = (PassageCandidate)o1;
      PassageCandidate s2 = (PassageCandidate)o2;
      if ( s1.getProbability() < s2.getProbability() ) {
        return 1;
      } else if ( s1.getProbability() > s2.getProbability() ) {
        return -1;
      }
      return 0;
    }   
  }

  class PassageSpan {
    private int begin, end;
    public PassageSpan( int begin , int end ) {
      this.begin = begin;
      this.end = end;
    }
    public boolean containedIn ( int begin , int end ) {
      if ( begin <= this.begin && end >= this.end ) {
        return true;
      } else {
        return false;
      }
    }
  }


}
