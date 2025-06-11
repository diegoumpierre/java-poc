package br.dev.guereguere.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PageRequestUtils {


    public PageRequest getPageRequestDTO(PesquisaDTO pesquisaDTO){

        Sort sort = null;


        System.out.println(pesquisaDTO.toString());

        if (pesquisaDTO.getSortField() != null ) {
            //ArrayList lstSort = new ArrayList(pesquisaDTO.getSortField());

            //inverte a ordem das chaves respeitando a ordem que foi selecionado em tela

            List<Sort.Order> orders = new ArrayList<Sort.Order>();



            if ("1".equals(pesquisaDTO.getSortOrder())) {
                orders.add(Sort.Order.asc(pesquisaDTO.getSortField()));
            }
            if ("-1".equals(pesquisaDTO.getSortOrder())) {
                orders.add(Sort.Order.desc(pesquisaDTO.getSortField()));
            }

            sort = Sort.by(orders);
        }
        if (sort != null){
            return  PageRequest.of(
                    pesquisaDTO.getPage(),
                    pesquisaDTO.getRows(),
                    sort
            );
        }else{
            return  PageRequest.of(
                    pesquisaDTO.getPage(),
                    pesquisaDTO.getRows()
            );
        }

    }
}