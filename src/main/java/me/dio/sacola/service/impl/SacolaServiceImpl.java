package me.dio.sacola.service.impl;

import lombok.RequiredArgsConstructor;
import me.dio.sacola.enumeration.FormaPagamento;
import me.dio.sacola.model.Item;
import me.dio.sacola.model.Restaurante;
import me.dio.sacola.model.Sacola;
import me.dio.sacola.repository.ItemRepository;
import me.dio.sacola.repository.ProdutoRepository;
import me.dio.sacola.repository.SacolaRepository;
import me.dio.sacola.resource.dto.ItemDto;
import me.dio.sacola.service.SacolaService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SacolaServiceImpl implements SacolaService {
    private final SacolaRepository sacolaRepository;
    private final ProdutoRepository produtoRepository;
    private final ItemRepository itemRepository;
    @Override
    public Item incluirItem(ItemDto itemDto) {

        Sacola sacola = verSacola(itemDto.getSacolaId());

        if(sacola.isFechada()){
            throw new RuntimeException("Esta sacola está fechada.");
        }

        Item itemParaInserir = Item.builder()
                .quantidade(itemDto.getQuantidade())
                .sacola(sacola)
                .produto(produtoRepository.findById(itemDto.getProdutoId()).orElseThrow(
                        () -> {
                            throw new RuntimeException("Esse produto não existe!");
                        }
                ))
                .build();

        List<Item> itensDaSacola = sacola.getItens();
        if(itensDaSacola.isEmpty()){
            itensDaSacola.add((itemParaInserir));
        } else {
            Restaurante restauranteAtual = itensDaSacola.get(0).getProduto().getRestaurante();
            Restaurante restauranteItem = itemParaInserir.getProduto().getRestaurante();
            if(restauranteAtual.equals(restauranteItem)){
                itensDaSacola.add((itemParaInserir));
            } else {
                throw new RuntimeException("Não é possível adicionar produtos de restaurantes diferentes. Feche a sacola ou esvazie.");
            }
        }

        List<Double> valorItens = new ArrayList<>();
        for(Item itemDaSacola: itensDaSacola){
            double valorTotalItem = itemDaSacola.getProduto().getValorUnitario() * itemDaSacola.getQuantidade();
            valorItens.add(valorTotalItem);
        }

        double valorTotalSacola = valorItens.stream()
                .mapToDouble(valorTotalIndividual -> valorTotalIndividual)
                .sum();

        sacola.setValorTotal(valorTotalSacola);
        sacolaRepository.save(sacola);
        return itemParaInserir;
    }

    @Override
    public Sacola verSacola(Long id) {
        return sacolaRepository.findById(id).orElseThrow(
                () -> {
                    throw new RuntimeException("Essa sacola não existe!");
                }
        );
    }

    @Override
    public Item verItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(
                () -> {
                    throw new RuntimeException("Item não encontrado.");
                }
        );
    }

    @Override
    public Sacola fecharSacola(Long id, int nroFormaPagamento) {
        Sacola sacola = verSacola(id);

        if (sacola.getItens().isEmpty()){
            throw new RuntimeException("Inclua itens na sacola!");
        }

        FormaPagamento formaPagamento = nroFormaPagamento == 0 ? FormaPagamento.DINHEIRO : FormaPagamento.MAQUINETA;

        sacola.setFormaPagamento(formaPagamento);
        sacola.setFechada(true);
        return sacolaRepository.save(sacola);

    }

    @Override
    public void excluirItem(Long sacolaId, Long itemId){

        Sacola sacola = verSacola(sacolaId);
        Item item = verItem(itemId);

        if(sacola.isFechada()){
            throw new RuntimeException("Esta sacola está fechada.");
        }

        if (sacola.getItens().isEmpty()){
            throw new RuntimeException("A sacola já está vazia.");
        }

        sacola.getItens().removeIf(itemDaSacola -> item.getId().equals(itemDaSacola.getId()));
        sacola.setValorTotal(sacola.getValorTotal() - (item.getProduto().getValorUnitario() * item.getQuantidade()));
        sacolaRepository.save(sacola);
        itemRepository.delete(item);

    }
}
