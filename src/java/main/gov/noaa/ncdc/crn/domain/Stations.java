package gov.noaa.ncdc.crn.domain;

import static com.google.common.base.Predicates.not;
import static com.google.common.base.Predicates.or;
import static gov.noaa.ncdc.crn.domain.CrnDomains.matches;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;

/**
 * {@code Function}s and {@code Predicate}s pertaining to {@link Station}
 * @author Andrea Fey
 */
public class Stations {
    /**
     * Finds the first {@link Station} in {@code Collection} which has this WBAN number. Returns {@code null} if not found.
     * @param stations The {@code Station}s to search through
     * @param wbanno The WBAN number to look for
     * @return The first {@code Station} whose WBAN number matches or null if not found
     */
    public static Station findWbanno(Collection<Station> stations, String wbanno) {
        return Iterables.find(stations, matches(WBANNO, wbanno), null);
    }

    /**
     * Filter {@code Collection} for {@code Station}s which are closed by matching operational statuses "A" and "C".
     * @param stations The Stations to filter
     * @return A subset which includes only closed or abandoned Stations
     */
    public static Collection<Station> filterClosed(Collection<Station> stations) {
        return Collections2.filter(stations, CLOSED);
    }

    /**
     * Filter {@code Collection} for {@code Station}s which are not closed by excluding operational statuses "A" and "C".
     * @param stations The Stations to filter
     * @return A subset which includes only Stations not closed or abandoned
     */
    public static Collection<Station> filterNotClosed(Collection<Station> stations) {
        return Collections2.filter(stations, not(CLOSED));
    }

    /**
     * Filter {@code Collection} for {@code Station}s which are not visible, meaning they are not test sites and they
     * are not abandoned.
     * @param stations The {@code Station}s to filter
     * @return A subset which includes only visible {@code Station}s which are not closed or abandoned
     */
    public static Collection<Station> filterVisible(Collection<Station> stations) {
        return Collections2.filter(stations, VISIBLE);
    }

    /**
     * Function which returns a {@code Station}'s networkId
     */
    public static Function<Station, Integer> NETWORK_ID = new Function<Station, Integer>() {
        @Override
        public Integer apply(Station station) {
            return station.getNetworkId();
        }
    };
    /**
     * Function which returns a {@code Station}'s operational status
     */
    public static Function<Station, String> OP_STATUS = new Function<Station, String>() {
        @Override
        public String apply(Station station) {
            return station.getOpStatus();
        }
    };
    /**
     * Function which returns a {@code Station}'s state
     */
    public static Function<Station, String> STATE = new Function<Station, String>() {
        @Override
        public String apply(Station station) {
            return station.getName().getState();
        }
    };

    /**
     * Function which returns a {@code Station}'s WBAN number
     */
    public static Function<Station, String> WBANNO = new Function<Station, String>() {
        @Override
        public String apply(Station station) {
            return station.getWbanno();
        }
    };

    /**
     * Function which returns a {@code Station}'s name {@code String} (e.g. "NC Asheville 13 S")
     */
    public static Function<Station, String> NAME_STRING = new Function<Station, String>() {
        @Override
        public String apply(Station station) {
            return station.getNameString();
        }
    };

    /** returns true for stations which are strictly CRN stations */
    public static Predicate<Station> CRN = new Predicate<Station>() {
        @Override
        public boolean apply(Station station) {
            return station.getNetworkId() == 1;
        }
    };
    /** returns true for stations which are strictly USRCRN or AL RCRN stations */
    public static Predicate<Station> RCRN = new Predicate<Station>() {
        @Override
        public boolean apply(Station station) {
            return station.getNetworkId() == 3 || station.getNetworkId() == 2;
        }
    };
    /**
     * returns true for stations which are strictly CRN identified as grid points for inclusion in RCRN reports
     */
    public static Predicate<Station> PSEUDO_RCRN = new Predicate<Station>() {
        @Override
        public boolean apply(Station station) {
            return station.isPseudoRcrn();
        }
    };

    /** returns true for stations which are strictly USRCRN or representative grid points */
    public static Predicate<Station> RCRN_INCLUDE_REPS = Predicates.or(RCRN, PSEUDO_RCRN);

    /** {@code Predicate} which returns true for {@code Station}s which are commissioned */
    public static Predicate<Station> COMMISSIONED = new Predicate<Station>() {
        @Override
        public boolean apply(Station station) {
            return "Y".equals(station.getCommCode());
        }
    };
    /** {@code Predicate} which returns true for {@code Station}s which are closed or abandoned */
    public static Predicate<Station> CLOSED = new Predicate<Station>() {
        @Override
        public boolean apply(Station station) {
            return station.isClosed();
        }
    };
    /** {@code Predicate} which returns true for {@code Station}s which are test sites */
    public static Predicate<Station> TEST_SITE = new Predicate<Station>() {
        @Override
        public boolean apply(Station station) {
            return station.getTestSiteOnly();
        }
    };
    /** {@code Predicate} which returns true for abandoned {@code Station}s */
    public static Predicate<Station> ABANDONED = new Predicate<Station>() {
        @Override
        public boolean apply(Station station) {
            return "A".equals(station.getOpStatus());
        }
    };
    /**
     * {@code Predicate} which returns true if a {@code Station} is visible, i.e. not a test site and not abandoned
     */
    public static Predicate<Station> VISIBLE = not(or(TEST_SITE, ABANDONED));

}
