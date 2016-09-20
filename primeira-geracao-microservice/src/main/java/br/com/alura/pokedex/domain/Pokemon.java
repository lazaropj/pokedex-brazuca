package br.com.alura.pokedex.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Pokemon.
 */
@Entity
@Table(name = "pokemon")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "pokemon")
public class Pokemon implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "nome", nullable = false)
    private String nome;

    @NotNull
    @Column(name = "imagem_url", nullable = false)
    private String imagemURL;

    @NotNull
    @Column(name = "tipo", nullable = false)
    private String tipo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public Pokemon nome(String nome) {
        this.nome = nome;
        return this;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getImagemURL() {
        return imagemURL;
    }

    public Pokemon imagemURL(String imagemURL) {
        this.imagemURL = imagemURL;
        return this;
    }

    public void setImagemURL(String imagemURL) {
        this.imagemURL = imagemURL;
    }

    public String getTipo() {
        return tipo;
    }

    public Pokemon tipo(String tipo) {
        this.tipo = tipo;
        return this;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Pokemon pokemon = (Pokemon) o;
        if(pokemon.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, pokemon.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Pokemon{" +
            "id=" + id +
            ", nome='" + nome + "'" +
            ", imagemURL='" + imagemURL + "'" +
            ", tipo='" + tipo + "'" +
            '}';
    }
}
