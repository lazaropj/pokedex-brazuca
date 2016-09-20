package br.com.alura.pokedex.repository.search;

import br.com.alura.pokedex.domain.Pokemon;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Pokemon entity.
 */
public interface PokemonSearchRepository extends ElasticsearchRepository<Pokemon, Long> {
}
