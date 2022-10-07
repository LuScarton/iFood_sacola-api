package me.dio.sacola.resource;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import me.dio.sacola.model.Item;
import me.dio.sacola.model.Sacola;
import me.dio.sacola.resource.dto.ItemDto;
import me.dio.sacola.service.SacolaService;
import org.springframework.web.bind.annotation.*;

@Api(value="/ifood-devweek/sacolas")
@RestController
@RequestMapping("/ifood-devweek/sacolas")
@RequiredArgsConstructor
public class SacolaResource {
    private final SacolaService sacolaService;

    @PostMapping
    public Item incluirItem(@RequestBody ItemDto itemDto){
        return sacolaService.incluirItem(itemDto);
    }

    @GetMapping("/{id}")
    public Sacola verSacola (@PathVariable("id") Long id){
        return sacolaService.verSacola(id);
    }

    @GetMapping("{id}/item/{itemId}")
    public Item verItem (@PathVariable("itemId") Long itemId){
        return sacolaService.verItem(itemId);
    }


    @PatchMapping("/fecharSacola/{sacolaId}")
    public Sacola fecharSacola (@PathVariable("sacolaId") Long sacolaId,
                                @RequestParam("formaPagamento") int formaPagamento){
        return sacolaService.fecharSacola(sacolaId, formaPagamento);
    }

    @DeleteMapping("{sacolaId}/item/{itemId}")
    public void excluirItem(@PathVariable("sacolaId") Long sacolaId,
                            @PathVariable("itemId") Long itemId){
        sacolaService.excluirItem(sacolaId, itemId);
    }
}
