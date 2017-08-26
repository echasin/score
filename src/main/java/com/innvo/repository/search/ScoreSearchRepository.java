package com.innvo.repository.search;

import com.innvo.domain.Score;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Score entity.
 */
public interface ScoreSearchRepository extends ElasticsearchRepository<Score, Long> {
}
