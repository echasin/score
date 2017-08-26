package com.innvo.repository.search;

import com.innvo.domain.Scorestatus;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Scorestatus entity.
 */
public interface ScorestatusSearchRepository extends ElasticsearchRepository<Scorestatus, Long> {
}
