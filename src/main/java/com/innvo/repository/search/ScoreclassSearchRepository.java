package com.innvo.repository.search;

import com.innvo.domain.Scoreclass;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Scoreclass entity.
 */
public interface ScoreclassSearchRepository extends ElasticsearchRepository<Scoreclass, Long> {
}
