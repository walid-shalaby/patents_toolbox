patents toolbox
===============

collection of apps to work with patents data

1. Topic Searcher
------------------
  - Used to create a DB of resilience and sustainability (R&S) related patents.
  - Reads list of topics related to R&S from DB then search for related patents at lucene index or DB then write retrieved patents data to destination DB
  - Dependencies:
    - Apache lucene4.6.0: lucene-core-4.6.0.jar, lucene-analyzers-common-4.6.0.jar, lucene-queries-4.6.0.jar, lucene-queryparser-4.6.0.jar
    - Included: sqlite-jdbc-3.7.2.jar

2. Topic Indexer
------------------
  - Used to create a lucene index of resilience and sustainability (R&S) related patents.
  - Reads list of patents related to R&S from DB then dump them to destination lucene index
  - Dependencies:
    - Apache lucene4.6.0: lucene-core-4.6.0.jar, lucene-analyzers-common-4.6.0.jar, lucene-queries-4.6.0.jar, lucene-queryparser-4.6.0.jar
    - Included: sqlite-jdbc-3.7.2.jar
