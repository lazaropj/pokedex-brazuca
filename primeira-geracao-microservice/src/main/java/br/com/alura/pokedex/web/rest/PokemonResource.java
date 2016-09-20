package br.com.alura.pokedex.web.rest;

import com.codahale.metrics.annotation.Timed;
import br.com.alura.pokedex.domain.Pokemon;

import br.com.alura.pokedex.repository.PokemonRepository;
import br.com.alura.pokedex.repository.search.PokemonSearchRepository;
import br.com.alura.pokedex.web.rest.util.HeaderUtil;
import br.com.alura.pokedex.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Pokemon.
 */
@RestController
@RequestMapping("/api")
public class PokemonResource {

    private final Logger log = LoggerFactory.getLogger(PokemonResource.class);
        
    @Inject
    private PokemonRepository pokemonRepository;

    @Inject
    private PokemonSearchRepository pokemonSearchRepository;

    /**
     * POST  /pokemons : Create a new pokemon.
     *
     * @param pokemon the pokemon to create
     * @return the ResponseEntity with status 201 (Created) and with body the new pokemon, or with status 400 (Bad Request) if the pokemon has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/pokemons",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Pokemon> createPokemon(@Valid @RequestBody Pokemon pokemon) throws URISyntaxException {
        log.debug("REST request to save Pokemon : {}", pokemon);
        if (pokemon.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("pokemon", "idexists", "A new pokemon cannot already have an ID")).body(null);
        }
        Pokemon result = pokemonRepository.save(pokemon);
        pokemonSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/pokemons/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("pokemon", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /pokemons : Updates an existing pokemon.
     *
     * @param pokemon the pokemon to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated pokemon,
     * or with status 400 (Bad Request) if the pokemon is not valid,
     * or with status 500 (Internal Server Error) if the pokemon couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/pokemons",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Pokemon> updatePokemon(@Valid @RequestBody Pokemon pokemon) throws URISyntaxException {
        log.debug("REST request to update Pokemon : {}", pokemon);
        if (pokemon.getId() == null) {
            return createPokemon(pokemon);
        }
        Pokemon result = pokemonRepository.save(pokemon);
        pokemonSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("pokemon", pokemon.getId().toString()))
            .body(result);
    }

    /**
     * GET  /pokemons : get all the pokemons.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of pokemons in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/pokemons",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Pokemon>> getAllPokemons(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Pokemons");
        Page<Pokemon> page = pokemonRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/pokemons");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /pokemons/:id : get the "id" pokemon.
     *
     * @param id the id of the pokemon to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the pokemon, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/pokemons/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Pokemon> getPokemon(@PathVariable Long id) {
        log.debug("REST request to get Pokemon : {}", id);
        Pokemon pokemon = pokemonRepository.findOne(id);
        return Optional.ofNullable(pokemon)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /pokemons/:id : delete the "id" pokemon.
     *
     * @param id the id of the pokemon to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/pokemons/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deletePokemon(@PathVariable Long id) {
        log.debug("REST request to delete Pokemon : {}", id);
        pokemonRepository.delete(id);
        pokemonSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("pokemon", id.toString())).build();
    }

    /**
     * SEARCH  /_search/pokemons?query=:query : search for the pokemon corresponding
     * to the query.
     *
     * @param query the query of the pokemon search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/_search/pokemons",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Pokemon>> searchPokemons(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Pokemons for query {}", query);
        Page<Pokemon> page = pokemonSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/pokemons");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
