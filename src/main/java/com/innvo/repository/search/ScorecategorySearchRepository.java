package com.innvo.repository.search;

import com.innvo.domain.Scorecategory;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Scorecategory entity.
 */
public interface ScorecategorySearchRepository extends ElasticsearchRepository<Scorecategory, Long> {
}
