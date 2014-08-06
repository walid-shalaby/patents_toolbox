patents toolbox
===============

collection of apps to work with patents data

1. Topic Searcher
------------------
  - Used to create a DB of resilience and sustainability (R&S) related patents.
  - Reads list of topics related to R&S from DB then search for related patents at lucene index or DB then write retrieved patents data to destination DB
  - Usage: java -jar topic_crawler.jar --topics-db "path" --patents-lookup-mode db|index --patents-src "path"
  - Dependencies:
    - Apache lucene4.6.0: lucene-core-4.6.0.jar, lucene-analyzers-common-4.6.0.jar, lucene-queries-4.6.0.jar, lucene-queryparser-4.6.0.jar
    - Included: sqlite-jdbc-3.7.2.jar

2. Patent Indexer
------------------
  - Used to create a lucene index of patents records stored in DB (currently tested on R&S patents).
  - Reads list of patents from DB then dump them to destination lucene index
  - Commandline must include DB columns and Index fields mappings, below is a list of supported DB columns names:
      - id
      - title
      - abstract
      - description
      - claims
      - class
      - subclass
      - further_classification
      - state
      - assignee
      - publication_date
      - city
  - Usage: java -jar patent_indexer.jar --patents-src "path" --patents-index "path" --DBcolunm-Indexfield-tuples {(DBcolumn1:IndexField1),(DBcolumn2:IndexField2),...}
  - Example Usage: --patents-src /home/user/patents.db --patents-index /home/user/patents-index --DBcolunm-Indexfield-tuples "{(id:patent_id),(title:title),(abstract:abstract),(description:description),(claims:claims),(class:class),(subclass:subclass),(further_classification:further_classification),(state:state),(assignee:assignee),(publication_date:publication_date),(city:city)}"
  - Dependencies:
    - Apache lucene4.6.0: lucene-core-4.6.0.jar, lucene-analyzers-common-4.6.0.jar, lucene-queries-4.6.0.jar, lucene-queryparser-4.6.0.jar
    - Included: sqlite-jdbc-3.7.2.jar
