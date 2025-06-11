package br.dev.guereguere.entity;

import lombok.*;
import javax.persistence.*;
import java.io.Serializable;


/**
 *
 * Objeto que representa uma PessoaFilho.
 *
 * @author Diego Umpierre
 * @since 11/11/2020
 */
@Entity
@Table(name = "001_pessoa_filho")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PessoaFilho implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "oid_001_pessoa_filho")
	private Long oidPessoaFilho;

	@ManyToOne
	@JoinColumn(name="oid_001_pessoa")
	private Pessoa pessoa;

	@Column(name = "nom_nome")
	private String nomNome;

	
}
