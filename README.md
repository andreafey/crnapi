# CRN Java API

This is most of the API code developed by Andrea Fey for the Climate Reference Network at the National Climatic Data Center (NOAA). Note tests can't be run because critical database queries (and network access, for that matter) are missing or masked with dummy table names and columns for security reasons. But it is a nice representative sample of my work.

One particularly interesting class to review is <a href="https://github.com/andreafey/crnapi/tree/master/src/java/main/gov/noaa/ncdc/crn/domain/CrnDomains.java">CrnDomains.java</a>. It contains Guava Predicates which can be used to filter various collections of domain objects by database identfiers, allowing developers to write their own filter compositions and taking advantage of cached queries for increased performance.
