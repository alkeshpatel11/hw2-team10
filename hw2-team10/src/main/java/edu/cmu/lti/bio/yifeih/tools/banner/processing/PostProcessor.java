package edu.cmu.lti.bio.yifeih.tools.banner.processing;

import edu.cmu.lti.bio.yifeih.tools.banner.Sentence;
import edu.cmu.lti.bio.yifeih.tools.banner.tagging.Mention;

/**
 * Instances of {@link PostProcessor} take {@link Sentence}s which have been tagged and modify the set of {@link Mention}s according to some
 * criteria.
 * 
 * @author Bob
 */
public interface PostProcessor
{
    /**
     * @param sentence
     *        The {@link Sentence} to perform post-processing on
     */
    public void postProcess(Sentence sentence);

}