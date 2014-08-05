patents toolbox
===============

collection of apps to work with patents data

Topic Searcher
------------------
  - Used to create a DB of resilience and sustainability (R&S) related patents.
  - Reads list of topics related to R&S from DB then search for related patents at lucene index or DB then write retrieved patents data to destination DB
  - Dependencies:
    - Apache lucene4.6.0: lucene-core-4.6.0.jar, lucene-analyzers-common-4.6.0.jar, lucene-queries-4.6.0.jar, lucene-queryparser-4.6.0.jar
    - Included: sqlite-jdbc-3.7.2.jar
