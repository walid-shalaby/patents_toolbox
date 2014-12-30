patents toolbox
===============

collection of apps to work with patents data

1. Topic Crawler
------------------
  - Used to create a DB of resilience and sustainability (R&S) related patents.
  - Reads list of topics related to R&S from DB then search for related patents at lucene index or DB then write retrieved patents data to destination DB
  - Usage: java -jar topic_crawler.jar --topics-db "path" --topic-lookup-mode title|abstract|title_abstract --patents-lookup-mode db|index --patents-src "path"
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
  - Usage: java -jar patent_indexer.jar --patents-src "path" --patents-index "path" [--preprocess stem] [--analyzer "lucene-analyzer-class"] --DBcolunm-Indexfield-tuples {(DBcolumn1:IndexField1),(DBcolumn2:IndexField2),...}
  - Example Usage: --patents-src /home/user/patents.db --patents-index /home/user/patents-index --analyzer org.apache.lucene.analysis.core.WhitespaceAnalyzer --DBcolunm-Indexfield-tuples "{(id:patent_id),(title:title),(abstract:abstract),(description:description),(claims:claims),(class:class),(subclass:subclass),(further_classification:further_classification),(state:state),(assignee:assignee),(publication_date:publication_date),(city:city)}"
  - Dependencies:
    - Apache lucene4.6.0: lucene-core-4.6.0.jar, lucene-analyzers-common-4.6.0.jar, lucene-queries-4.6.0.jar, lucene-queryparser-4.6.0.jar
    - Included: sqlite-jdbc-3.7.2.jar

3. Concept Searcher
--------------------
  - Used to create a coincidence DB table of concepts and patents in which these concepts appear (currently lookup in patent title and/or abstract).
  - The list of concepts are stored in a DB table and search proceeds by forming a search query to a lucene index containing patents data for each concept. The hit list of each concept is then written to the conincidence DB table.
  - Usage: java -jar concept_searcher.jar --patents-index "path-to-lucene-index" --concepts-db "path-to-concepts-db" --concepts-table "concepts-table-name" --concepts-field "concepts-field-name" --target-table "target-coincidence-table" --target-patent-field "patent-field-id" --target-concept-field "concept-field-id" [--analyzer "lucene-analyzer-class"] --target-Index-fields "{IndexField1,IndexField2,...}"
  - Example Usage: java -jar concept_searcher.jar --patents-index /home/user/patents --concepts-db /home/user/concepts.db --concepts-table wiki_concepts --concepts-field name --target-table wiki_concepts_patents --target-patent-field innovation_id --target-concept-field concept_id --analyzer org.apache.lucene.analysis.core.WhitespaceAnalyzer --target-Index-fields "{title,abstract}"
  - Dependencies:
    - Apache lucene4.6.0: lucene-core-4.6.0.jar, lucene-analyzers-common-4.6.0.jar, lucene-queries-4.6.0.jar, lucene-queryparser-4.6.0.jar
    - Included: sqlite-jdbc-3.7.2.jar

