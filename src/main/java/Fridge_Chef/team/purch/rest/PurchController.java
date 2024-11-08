package Fridge_Chef.team.purch.rest;

import Fridge_Chef.team.purch.service.PurchService;
import Fridge_Chef.team.purch.service.response.PurchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purch")
@RequiredArgsConstructor
public class PurchController {
    private final PurchService purchService;

    @GetMapping
    public PurchResponse get(@RequestParam(name = "search") String search) {
        return purchService.searchToShopLink(search);
    }
}
