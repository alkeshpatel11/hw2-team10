package edu.cmu.lti.oaqa.openqa.test.team10.passage;

import edu.cmu.lti.oaqa.openqa.hello.passage.KeytermWindowScorer;
import edu.cmu.lti.oaqa.openqa.hello.passage.PassageCandidateFinder;

public class LuceneBioPassageCandidateFinder extends PassageCandidateFinder{

	public LuceneBioPassageCandidateFinder(String docId, String text,
			KeytermWindowScorer scorer) {
		super(docId, text, scorer);
		
	}

}
