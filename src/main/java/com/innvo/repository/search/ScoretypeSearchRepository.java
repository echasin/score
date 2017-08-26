package com.innvo.repository.search;

import com.innvo.domain.Scoretype;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Scoretype entity.
 */
public interface ScoretypeSearchRepository extends ElasticsearchRepository<Scoretype, Long> {
}
