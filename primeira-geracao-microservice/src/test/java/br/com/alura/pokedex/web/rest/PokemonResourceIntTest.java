package br.com.alura.pokedex.web.rest;

import br.com.alura.pokedex.PrimeiraGeracaoMicroserviceApp;

import br.com.alura.pokedex.domain.Pokemon;
import br.com.alura.pokedex.repository.PokemonRepository;
import br.com.alura.pokedex.repository.search.PokemonSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the PokemonResource REST controller.
 *
 * @see PokemonResource
 */
@RunWith(SpringRunner.class)

@SpringBootTest(classes = PrimeiraGeracaoMicroserviceApp.class)

public class PokemonResourceIntTest {
    private static final String DEFAULT_NOME = "AAAAA";
    private static final String UPDATED_NOME = "BBBBB";
    private static final String DEFAULT_IMAGEM_URL = "AAAAA";
    private static final String UPDATED_IMAGEM_URL = "BBBBB";
    private static final String DEFAULT_TIPO = "AAAAA";
    private static final String UPDATED_TIPO = "BBBBB";

    @Inject
    private PokemonRepository pokemonRepository;

    @Inject
    private PokemonSearchRepository pokemonSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restPokemonMockMvc;

    private Pokemon pokemon;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PokemonResource pokemonResource = new PokemonResource();
        ReflectionTestUtils.setField(pokemonResource, "pokemonSearchRepository", pokemonSearchRepository);
        ReflectionTestUtils.setField(pokemonResource, "pokemonRepository", pokemonRepository);
        this.restPokemonMockMvc = MockMvcBuilders.standaloneSetup(pokemonResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pokemon createEntity(EntityManager em) {
        Pokemon pokemon = new Pokemon()
                .nome(DEFAULT_NOME)
                .imagemURL(DEFAULT_IMAGEM_URL)
                .tipo(DEFAULT_TIPO);
        return pokemon;
    }

    @Before
    public void initTest() {
        pokemonSearchRepository.deleteAll();
        pokemon = createEntity(em);
    }

    @Test
    @Transactional
    public void createPokemon() throws Exception {
        int databaseSizeBeforeCreate = pokemonRepository.findAll().size();

        // Create the Pokemon

        restPokemonMockMvc.perform(post("/api/pokemons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(pokemon)))
                .andExpect(status().isCreated());

        // Validate the Pokemon in the database
        List<Pokemon> pokemons = pokemonRepository.findAll();
        assertThat(pokemons).hasSize(databaseSizeBeforeCreate + 1);
        Pokemon testPokemon = pokemons.get(pokemons.size() - 1);
        assertThat(testPokemon.getNome()).isEqualTo(DEFAULT_NOME);
        assertThat(testPokemon.getImagemURL()).isEqualTo(DEFAULT_IMAGEM_URL);
        assertThat(testPokemon.getTipo()).isEqualTo(DEFAULT_TIPO);

        // Validate the Pokemon in ElasticSearch
        Pokemon pokemonEs = pokemonSearchRepository.findOne(testPokemon.getId());
        assertThat(pokemonEs).isEqualToComparingFieldByField(testPokemon);
    }

    @Test
    @Transactional
    public void checkNomeIsRequired() throws Exception {
        int databaseSizeBeforeTest = pokemonRepository.findAll().size();
        // set the field null
        pokemon.setNome(null);

        // Create the Pokemon, which fails.

        restPokemonMockMvc.perform(post("/api/pokemons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(pokemon)))
                .andExpect(status().isBadRequest());

        List<Pokemon> pokemons = pokemonRepository.findAll();
        assertThat(pokemons).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkImagemURLIsRequired() throws Exception {
        int databaseSizeBeforeTest = pokemonRepository.findAll().size();
        // set the field null
        pokemon.setImagemURL(null);

        // Create the Pokemon, which fails.

        restPokemonMockMvc.perform(post("/api/pokemons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(pokemon)))
                .andExpect(status().isBadRequest());

        List<Pokemon> pokemons = pokemonRepository.findAll();
        assertThat(pokemons).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTipoIsRequired() throws Exception {
        int databaseSizeBeforeTest = pokemonRepository.findAll().size();
        // set the field null
        pokemon.setTipo(null);

        // Create the Pokemon, which fails.

        restPokemonMockMvc.perform(post("/api/pokemons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(pokemon)))
                .andExpect(status().isBadRequest());

        List<Pokemon> pokemons = pokemonRepository.findAll();
        assertThat(pokemons).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPokemons() throws Exception {
        // Initialize the database
        pokemonRepository.saveAndFlush(pokemon);

        // Get all the pokemons
        restPokemonMockMvc.perform(get("/api/pokemons?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(pokemon.getId().intValue())))
                .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME.toString())))
                .andExpect(jsonPath("$.[*].imagemURL").value(hasItem(DEFAULT_IMAGEM_URL.toString())))
                .andExpect(jsonPath("$.[*].tipo").value(hasItem(DEFAULT_TIPO.toString())));
    }

    @Test
    @Transactional
    public void getPokemon() throws Exception {
        // Initialize the database
        pokemonRepository.saveAndFlush(pokemon);

        // Get the pokemon
        restPokemonMockMvc.perform(get("/api/pokemons/{id}", pokemon.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(pokemon.getId().intValue()))
            .andExpect(jsonPath("$.nome").value(DEFAULT_NOME.toString()))
            .andExpect(jsonPath("$.imagemURL").value(DEFAULT_IMAGEM_URL.toString()))
            .andExpect(jsonPath("$.tipo").value(DEFAULT_TIPO.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPokemon() throws Exception {
        // Get the pokemon
        restPokemonMockMvc.perform(get("/api/pokemons/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePokemon() throws Exception {
        // Initialize the database
        pokemonRepository.saveAndFlush(pokemon);
        pokemonSearchRepository.save(pokemon);
        int databaseSizeBeforeUpdate = pokemonRepository.findAll().size();

        // Update the pokemon
        Pokemon updatedPokemon = pokemonRepository.findOne(pokemon.getId());
        updatedPokemon
                .nome(UPDATED_NOME)
                .imagemURL(UPDATED_IMAGEM_URL)
                .tipo(UPDATED_TIPO);

        restPokemonMockMvc.perform(put("/api/pokemons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedPokemon)))
                .andExpect(status().isOk());

        // Validate the Pokemon in the database
        List<Pokemon> pokemons = pokemonRepository.findAll();
        assertThat(pokemons).hasSize(databaseSizeBeforeUpdate);
        Pokemon testPokemon = pokemons.get(pokemons.size() - 1);
        assertThat(testPokemon.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testPokemon.getImagemURL()).isEqualTo(UPDATED_IMAGEM_URL);
        assertThat(testPokemon.getTipo()).isEqualTo(UPDATED_TIPO);

        // Validate the Pokemon in ElasticSearch
        Pokemon pokemonEs = pokemonSearchRepository.findOne(testPokemon.getId());
        assertThat(pokemonEs).isEqualToComparingFieldByField(testPokemon);
    }

    @Test
    @Transactional
    public void deletePokemon() throws Exception {
        // Initialize the database
        pokemonRepository.saveAndFlush(pokemon);
        pokemonSearchRepository.save(pokemon);
        int databaseSizeBeforeDelete = pokemonRepository.findAll().size();

        // Get the pokemon
        restPokemonMockMvc.perform(delete("/api/pokemons/{id}", pokemon.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean pokemonExistsInEs = pokemonSearchRepository.exists(pokemon.getId());
        assertThat(pokemonExistsInEs).isFalse();

        // Validate the database is empty
        List<Pokemon> pokemons = pokemonRepository.findAll();
        assertThat(pokemons).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchPokemon() throws Exception {
        // Initialize the database
        pokemonRepository.saveAndFlush(pokemon);
        pokemonSearchRepository.save(pokemon);

        // Search the pokemon
        restPokemonMockMvc.perform(get("/api/_search/pokemons?query=id:" + pokemon.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pokemon.getId().intValue())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME.toString())))
            .andExpect(jsonPath("$.[*].imagemURL").value(hasItem(DEFAULT_IMAGEM_URL.toString())))
            .andExpect(jsonPath("$.[*].tipo").value(hasItem(DEFAULT_TIPO.toString())));
    }
}
