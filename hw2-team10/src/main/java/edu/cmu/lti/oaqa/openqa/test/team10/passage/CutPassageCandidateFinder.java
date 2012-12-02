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
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Collections;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;

import edu.cmu.lti.oaqa.framework.data.PassageCandidate;

public class CutPassageCandidateFinder {
	private String text;
	private String docId;

	private int textSize;      // values for the entire text
	private int totalMatches;  
	private int totalKeyterms;

	private KeytermWindowScorer scorer;

	public CutPassageCandidateFinder( String docId , String text , KeytermWindowScorer scorer ) {
		super();
		this.text = text;
		this.docId = docId;
		this.textSize = text.length();
		this.scorer = scorer;
	}
	
	public int binSearch(List<Integer> list,int key){
	  int start = 0, end = list.size()-1;int mid;
	  while(start<=end){
	    mid = (start + end)/2;
	    int value = list.get(mid);
	    if(value==key)
	      return mid;
	    if(key>value){
	      start= mid + 1;        
	       if (start>end)
	         return mid;
	    }
	    else{
	      end = mid -1;
        if (start>end)
          return mid;  
	    }
    }
	  return 0;
	}
  public int checker(int begin, int end,List<Integer> list){
    //begin begin position
    //end the supposed last begin position of key word could be.
    int mid  = binSearch(list,begin);
    int start = -1,ending = -1;
    if (list.get(mid)==begin)
      start = mid;
    else{
      int s = mid-2;
      int e = mid+2;
      if (e >list.size()-1)
        e = list.size()-1;
      if (s < 0)
        s = 0;
      int i;
      for(i=s;i<=e;i++){
        if (list.get(i)>begin){
          start = i;break;
        }
      }
      if (start==-1)
        return 0;
    }
    mid  = binSearch(list,end);
    if (list.get(mid)==end)
      ending = mid;
    else{
      int s = mid-2;
      int e = mid+2;
      if (e >list.size()-1)
        e = list.size()-1;
      if (s < 0)
        s = 0;
      
      for(int i=e;i>=s;i--){
        if (list.get(i)<end){
          ending = i;break;
        }
      }
      if (ending==-1)
        return 0;
    }  
    int result = ending - start + 1;
    if (result<0)
      return 0;
    return result;
  }
	
	public List<PassageCandidate> extractPassages( String[] keyterms,int gap) {
		List<List<PassageSpan>> matchingSpans = new ArrayList<List<PassageSpan>>();
		List<PassageSpan> matchedSpans = null;
		List<List<Integer>> matchingStarts = new ArrayList<List<Integer>>();
		List<Integer> matchedStarts = null;
		int[] emptyAfter = new int[keyterms.length];
    HashSet<Integer> leftEdges = new HashSet<Integer>();
    HashSet<Integer> rightEdges = new HashSet<Integer>();
		// Find all keyterm matches.
		for ( String keyterm : keyterms ) {
			Pattern p = Pattern.compile( keyterm );
			Matcher m = p.matcher( text );
			matchedStarts = new ArrayList<Integer>();
			while ( m.find() ) {
				matchedStarts.add( m.start() );
				leftEdges.add(m.start());
				rightEdges.add(m.end());
				totalMatches++;
			}
			if (! matchedStarts.isEmpty() ) {
				totalKeyterms++;
			}
			matchingStarts.add( matchedStarts );
		}
		int emptyFromEnd = 0;
		for(int i = matchingStarts.size()-1;i>=0;i--){
		  if (matchingStarts.get(i).isEmpty()){
		    emptyFromEnd++;
		  }
		  emptyAfter[i] = emptyFromEnd;		  
		}

		// create set of left edges and right edges which define possible windows.

		// For every possible window, calculate keyterms found, matches found; score window, and create passage candidate.
		List<PassageCandidate> result = new ArrayList<PassageCandidate>();
		for ( Integer begin : leftEdges ) {
			for ( Integer end : rightEdges ) {
				if ( end <= begin||end-begin>gap ) continue; 
				// This code runs for each window.
				int keytermsFound = 0;
				int matchesFound = 0;
				for ( int i=0;i<matchingStarts.size();i++ ) {
				  List<Integer> keytermMatches = matchingStarts.get(i);
				  if (keytermMatches.isEmpty()) {continue;}
					boolean thisKeytermFound = false;
					int l = keyterms[i].length();
					int ee = matchingStarts.size()-i;
					int found = checker(begin,end-l,keytermMatches);
					matchesFound += found*(ee-emptyAfter[i]);
					/*for(int ii=ee-1;ii>=0;ii--)
					{ 
					  List<Integer> ttt = matchingStarts.get(ii);
					  if (ttt.isEmpty())
					    continue;
					  if(checker(begin,end-l,ttt)>0){
					    keytermsFound++;break;
					  }
					}*/
				  if ( found>0 ||keytermsFound>0){
				    keytermsFound++;
					}
				}
				double score = scorer.scoreWindow( begin , end , matchesFound , totalMatches , keytermsFound , totalKeyterms , textSize );
				PassageCandidate window = null;
				try {
					window = new PassageCandidate( docId , begin , end , (float) score , null );
				} catch (AnalysisEngineProcessException e) {
					e.printStackTrace();
				}
				result.add( window );
			}
		}

		// Sort the result in order of decreasing score.
		// Collections.sort ( result , new PassageCandidateComparator() );
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