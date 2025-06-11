package br.dev.guereguere.entity;

import lombok.*;
import javax.persistence.*;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;


/**
 *
 * Objeto que representa uma Pessoa.
 *
 * @author Diego Umpierre
 * @since 11/11/2020
 */
@Entity
@Table(name = "001_pessoa")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Pessoa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "oid_001_pessoa")
	private Long oidPessoa;

	@JsonIgnore
	@OneToMany(mappedBy="pessoa")
	private List<PessoaFilho> lstPessoaFilho;

	@Column(name = "nom_nome")
	private String nomNome;

	
}
