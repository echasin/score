package com.innvo.repository.search;

import com.innvo.domain.Scorerecordtype;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Scorerecordtype entity.
 */
public interface ScorerecordtypeSearchRepository extends ElasticsearchRepository<Scorerecordtype, Long> {
}
