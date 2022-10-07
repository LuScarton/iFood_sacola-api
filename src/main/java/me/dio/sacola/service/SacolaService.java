package me.dio.sacola.service;

import me.dio.sacola.model.Item;
import me.dio.sacola.model.Sacola;
import me.dio.sacola.resource.dto.ItemDto;

public interface SacolaService {

    Item incluirItem (ItemDto itemDto);

    Sacola verSacola (Long id);

    Item verItem (Long itemId);

    Sacola fecharSacola (Long id, int formaPagamento);

    void excluirItem (Long sacolaId, Long itemId);

}