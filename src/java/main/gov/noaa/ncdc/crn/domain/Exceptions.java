package gov.noaa.ncdc.crn.domain;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

public class Exceptions {
    /**
     * Finds the subset of the {@code Collection<ExceptionResolutionFact>} for a resolution. Returns null if not found.
     * @param stations The {@link ExceptionResolutionFact} to search through
     * @param wbanno The resolutionId to look for
     * @return The {@link ExceptionResolutionFact}s whose resolutionId matches or null if not found
     */
    public static Collection<ExceptionResolutionFact> filterResolutionFactsForResolution(
            Collection<ExceptionResolutionFact> facts, int resolutionId) {
        return Collections2.filter(facts, CrnDomains.matches(RESOLUTION_ID, Lists.newArrayList(resolutionId)));
    }

    /**
     * {@code Function} which returns an {@link ExceptionResolutionFact}'s resolutionId
     */
    public static Function<ExceptionResolutionFact, Integer> RESOLUTION_ID = new Function<ExceptionResolutionFact, Integer>() {
        @Override
        public Integer apply(ExceptionResolutionFact fact) {
            return fact.getResolutionId();
        }
    };
    /**
     * {@code Function} which returns a {@link ResolutionWithFact}'s facts
     */
    public static Function<ResolutionWithFacts, Collection<ExceptionResolutionFact>> FACTS = new Function<ResolutionWithFacts, Collection<ExceptionResolutionFact>>() {
        @Override
        public Collection<ExceptionResolutionFact> apply(ResolutionWithFacts rwf) {
            return rwf.getFacts();
        }
    };

}
