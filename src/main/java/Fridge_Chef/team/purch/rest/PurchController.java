package Fridge_Chef.team.purch.rest;

import Fridge_Chef.team.purch.service.PurchService;
import Fridge_Chef.team.purch.service.response.PurchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/purch")
@RequiredArgsConstructor
public class PurchController {
    private final PurchService purchService;

    @GetMapping
    public PurchResponse get(@RequestPart(name = "search") String search) {
        return purchService.searchToShopLink(search);
    }
}
