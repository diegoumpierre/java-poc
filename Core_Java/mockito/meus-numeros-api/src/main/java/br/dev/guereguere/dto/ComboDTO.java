package br.dev.guereguere.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.io.Serializable;

@Builder
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ComboDTO  implements Serializable,Comparable<ComboDTO> {

    private final long serialVersionUID = 1L;

    private Long value;
    private String label;

    @Override
    public int compareTo(ComboDTO o) {

        if (this.label == null || o == null ) return 0;

        return  this.label.compareTo(o.label);
    }
}